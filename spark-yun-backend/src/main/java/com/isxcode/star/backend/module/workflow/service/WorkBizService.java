package com.isxcode.star.backend.module.workflow.service;

import static java.sql.DriverManager.getConnection;

import com.isxcode.star.api.pojos.agent.req.ExecuteReq;
import com.isxcode.star.api.pojos.agent.req.PluginReq;
import com.isxcode.star.api.pojos.agent.res.ExecuteRes;
import com.isxcode.star.api.pojos.agent.res.GetDataRes;
import com.isxcode.star.api.pojos.agent.res.GetLogRes;
import com.isxcode.star.api.pojos.agent.res.GetStatusRes;
import com.isxcode.star.api.pojos.work.req.AddWorkReq;
import com.isxcode.star.api.pojos.work.req.ConfigWorkReq;
import com.isxcode.star.api.pojos.work.res.GetWorkRes;
import com.isxcode.star.api.pojos.work.res.QueryWorkRes;
import com.isxcode.star.api.pojos.work.res.RunWorkRes;
import com.isxcode.star.api.utils.HttpUtils;
import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import com.isxcode.star.backend.module.datasource.repository.DatasourceRepository;
import com.isxcode.star.backend.module.engine.entity.EngineEntity;
import com.isxcode.star.backend.module.engine.repository.EngineRepository;
import com.isxcode.star.backend.module.node.entity.NodeEntity;
import com.isxcode.star.backend.module.node.repository.NodeRepository;
import com.isxcode.star.backend.module.workflow.entity.WorkConfigEntity;
import com.isxcode.star.backend.module.workflow.entity.WorkEntity;
import com.isxcode.star.backend.module.workflow.mapper.WorkMapper;
import com.isxcode.star.backend.module.workflow.repository.WorkConfigRepository;
import com.isxcode.star.backend.module.workflow.repository.WorkRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkBizService {

  private final WorkRepository workRepository;

  private final WorkConfigRepository workConfigRepository;

  private final DatasourceRepository datasourceRepository;

  private final WorkMapper workMapper;

  private final EngineRepository engineRepository;

  private final NodeRepository nodeRepository;

  public void addWork(AddWorkReq addWorkReq) {

    // req 转 entity
    WorkEntity work = workMapper.addWorkReqToWorkEntity(addWorkReq);

    // 添加默认配置
    WorkConfigEntity workConfigEntity = new WorkConfigEntity();
    WorkConfigEntity save = workConfigRepository.save(workConfigEntity);
    work.setWorkConfigId(save.getId());

    work.setStatus("新建");
    work.setCreateDateTime(LocalDateTime.now());

    // 数据持久化
    workRepository.save(work);
  }

  public List<QueryWorkRes> queryWork(String workflowId) {

    List<WorkEntity> workEntities = workRepository.findAllByWorkflowId(workflowId);

    return workMapper.workEntityListToQueryWorkResList(workEntities);
  }

  public void delWork(String workId) {

    workRepository.deleteById(workId);
  }

  public RunWorkRes runWork(String workId) throws ClassNotFoundException {

    // 查询作业
    WorkEntity workEntity = workRepository.findById(workId).get();

    // 判断类型
    switch (workEntity.getType()) {
      case "executeSql":
        return executeSql(workEntity.getWorkConfigId());
      case "querySql":
        return querySql(workEntity.getWorkConfigId());
      case "sparkSql":
        return sparkSql(workEntity.getWorkConfigId());
      default:
        return new RunWorkRes("执行失败", "作业类型暂不支持", null, null, null, null, null);
    }
  }

  public RunWorkRes getData(String workId, String applicationId) {

    WorkEntity workEntity = workRepository.findById(workId).get();

    WorkConfigEntity workConfigEntity =
        workConfigRepository.findById(workEntity.getWorkConfigId()).get();

    NodeEntity node = nodeRepository.findAllByEngineId(workConfigEntity.getEngineId()).get(0);

    GetDataRes getDataRes =
        HttpUtils.doGet(
            "http://"
                + node.getHost()
                + ":"
                + "8080"
                + "/agent/getData?applicationId="
                + applicationId,
            GetDataRes.class);

    RunWorkRes dataResToRunWorkRes = workMapper.getDataResToRunWorkRes(getDataRes);
    dataResToRunWorkRes.setApplicationId(applicationId);
    return dataResToRunWorkRes;
  }

  public RunWorkRes getStatus(String workId, String applicationId) {

    WorkEntity workEntity = workRepository.findById(workId).get();

    WorkConfigEntity workConfigEntity =
        workConfigRepository.findById(workEntity.getWorkConfigId()).get();

    NodeEntity node = nodeRepository.findAllByEngineId(workConfigEntity.getEngineId()).get(0);

    GetStatusRes getStatusRes =
        HttpUtils.doGet(
            "http://"
                + node.getHost()
                + ":"
                + "8080"
                + "/agent/getStatus?applicationId="
                + applicationId,
            GetStatusRes.class);

    RunWorkRes statusToRunWorkRes = workMapper.getStatusToRunWorkRes(getStatusRes);
    statusToRunWorkRes.setApplicationId(applicationId);

    return statusToRunWorkRes;
  }

  public RunWorkRes stopJob(String workId, String applicationId) {

    WorkEntity workEntity = workRepository.findById(workId).get();

    WorkConfigEntity workConfigEntity =
        workConfigRepository.findById(workEntity.getWorkConfigId()).get();

    NodeEntity node = nodeRepository.findAllByEngineId(workConfigEntity.getEngineId()).get(0);

    Map map =
        HttpUtils.doGet(
            "http://"
                + node.getHost()
                + ":"
                + "8080"
                + "/agent/stopJob?applicationId="
                + applicationId,
            Map.class);

    return new RunWorkRes("提交成功", "", null, applicationId, null, null, null);
  }

  public RunWorkRes getWorkLog(String workId, String applicationId) {

    WorkEntity workEntity = workRepository.findById(workId).get();

    WorkConfigEntity workConfigEntity =
        workConfigRepository.findById(workEntity.getWorkConfigId()).get();

    NodeEntity node = nodeRepository.findAllByEngineId(workConfigEntity.getEngineId()).get(0);

    GetLogRes getLogRes =
        HttpUtils.doGet(
            "http://"
                + node.getHost()
                + ":"
                + "8080"
                + "/agent/getLog?applicationId="
                + applicationId,
            GetLogRes.class);

    RunWorkRes logResToRunWorkRes = workMapper.getLogResToRunWorkRes(getLogRes);
    logResToRunWorkRes.setApplicationId(applicationId);
    return logResToRunWorkRes;
  }

  public RunWorkRes executeSql(String workConfigId) throws ClassNotFoundException {

    // 查询配置
    WorkConfigEntity workConfigEntity = workConfigRepository.findById(workConfigId).get();

    // 获取数据源信息
    DatasourceEntity datasourceEntity =
        datasourceRepository.findById(workConfigEntity.getDatasourceId()).get();

    switch (datasourceEntity.getType()) {
      case "mysql":
        Class.forName("com.mysql.cj.jdbc.Driver");
        break;
      case "oracle":
        Class.forName("oracle.jdbc.driver.OracleDriver");
        break;
      case "sqlserver":
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        break;
      default:
        return new RunWorkRes("暂不支持数据源", "暂不支持数据源", null, null, null, null, null);
    }

    try (Connection connection =
            getConnection(
                datasourceEntity.getJdbcUrl(),
                datasourceEntity.getUsername(),
                datasourceEntity.getPasswd());
        Statement statement = connection.createStatement(); ) {
      statement.execute(workConfigEntity.getScript());
      return new RunWorkRes("提交成功", "", null, null, null, null, null);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new RunWorkRes("执行失败", e.getMessage(), null, null, null, null, null);
    }
  }

  public RunWorkRes querySql(String workConfigId) throws ClassNotFoundException {

    // 查询配置
    WorkConfigEntity workConfigEntity = workConfigRepository.findById(workConfigId).get();

    if (Strings.isEmpty(workConfigEntity.getDatasourceId())) {
      return new RunWorkRes("执行失败", "请先在配置中选择数据源", null, null, null, null, null);
    }

    // 获取数据源信息
    DatasourceEntity datasourceEntity =
        datasourceRepository.findById(workConfigEntity.getDatasourceId()).get();

    switch (datasourceEntity.getType()) {
      case "mysql":
        Class.forName("com.mysql.cj.jdbc.Driver");
        break;
      case "oracle":
        Class.forName("oracle.jdbc.driver.OracleDriver");
        break;
      case "sqlserver":
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        break;
      default:
        return new RunWorkRes("暂不支持数据源", "暂不支持数据源", null, null, null, null, null);
    }

    List<List<String>> result = new ArrayList<>();

    try (Connection connection =
            getConnection(
                datasourceEntity.getJdbcUrl(),
                datasourceEntity.getUsername(),
                datasourceEntity.getPasswd());
        Statement statement = connection.createStatement(); ) {
      ResultSet resultSet = statement.executeQuery(workConfigEntity.getScript());
      int columnCount = resultSet.getMetaData().getColumnCount();

      // 表头
      List<String> metaList = new ArrayList<>();
      for (int i = 1; i <= columnCount; i++) {
        metaList.add(resultSet.getMetaData().getColumnName(i));
      }
      result.add(metaList);

      // 数据
      while (resultSet.next()) {
        metaList = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
          metaList.add(String.valueOf(resultSet.getObject(i)));
        }
        result.add(metaList);
      }
      return new RunWorkRes("提交成功", "", result, null, null, null, null);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new RunWorkRes("查询失败", e.getMessage(), null, null, null, null, null);
    }
  }

  public void configWork(ConfigWorkReq configWorkReq) {

    WorkEntity workEntity = workRepository.findById(configWorkReq.getWorkId()).get();

    WorkConfigEntity workConfigEntity =
        workConfigRepository.findById(workEntity.getWorkConfigId()).get();

    if (!Strings.isEmpty(configWorkReq.getDatasourceId())) {
      workConfigEntity.setDatasourceId(configWorkReq.getDatasourceId());
    }

    if (!Strings.isEmpty(configWorkReq.getEngineId())) {
      workConfigEntity.setEngineId(configWorkReq.getEngineId());
    }

    if (!Strings.isEmpty(configWorkReq.getScript())) {
      workConfigEntity.setScript(configWorkReq.getScript());
    }

    workConfigRepository.save(workConfigEntity);
  }

  public GetWorkRes getWork(String workId) {

    WorkEntity workEntity = workRepository.findById(workId).get();

    WorkConfigEntity workConfigEntity =
        workConfigRepository.findById(workEntity.getWorkConfigId()).get();

    return workMapper.workEntityAndWorkConfigEntityToGetWorkRes(workEntity, workConfigEntity);
  }

  public RunWorkRes sparkSql(String workConfigId) {

    // 查询配置
    WorkConfigEntity workConfigEntity = workConfigRepository.findById(workConfigId).get();

    if (Strings.isEmpty(workConfigEntity.getEngineId())) {
      return new RunWorkRes("执行失败", "请先在配置中选择计算引擎", null, null, null, null, null);
    }

    // 找到可用的计算节点
    EngineEntity engine = engineRepository.findById(workConfigEntity.getEngineId()).get();

    List<NodeEntity> allNodes = nodeRepository.findAllByEngineId(engine.getId());

    if (allNodes.size() < 1) {
      return new RunWorkRes("执行失败", "可用计算节点为0", null, null, null, null, null);
    }

    NodeEntity node = allNodes.get(0);

    // 调用远程接口
    ExecuteReq executeReq = new ExecuteReq();
    executeReq.setAppName("spark-star");
    executeReq.setMainClass("com.isxcode.star.plugin.querysql.Execute");
    executeReq.setAppResourceName("spark-query-sql-plugin-3.0.1-plain");
    executeReq.setHomePath(node.getHomePath());

    PluginReq pluginReq = new PluginReq();
    pluginReq.setSql(workConfigEntity.getScript());
    pluginReq.setLimit(200);
    executeReq.setPluginReq(pluginReq);

    ExecuteRes executeRes;
    try {
      executeRes =
          HttpUtils.doPost(
              "http://" + node.getHost() + ":" + "8080" + "/agent/execute",
              new HashMap<>(),
              executeReq,
              ExecuteRes.class);
    } catch (IOException e) {
      return new RunWorkRes("执行失败", "提交作业失败", null, null, null, null, null);
    }

    RunWorkRes runWorkRes = workMapper.executeResToRunWorkRes(executeRes);
    runWorkRes.setLog("");
    return runWorkRes;
  }
}
