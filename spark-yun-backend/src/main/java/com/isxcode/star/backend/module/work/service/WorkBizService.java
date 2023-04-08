package com.isxcode.star.backend.module.work.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.CodeConstants;
import com.isxcode.star.api.constants.DatasourceType;
import com.isxcode.star.api.constants.EngineNodeStatus;
import com.isxcode.star.api.constants.WorkStatus;
import com.isxcode.star.api.constants.WorkType;
import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import com.isxcode.star.api.pojos.work.req.WokAddWorkReq;
import com.isxcode.star.api.pojos.work.req.WokGetDataReq;
import com.isxcode.star.api.pojos.work.req.WokGetStatusReq;
import com.isxcode.star.api.pojos.work.req.WokGetWorkLogReq;
import com.isxcode.star.api.pojos.work.req.WokQueryWorkReq;
import com.isxcode.star.api.pojos.work.req.WokStopJobReq;
import com.isxcode.star.api.pojos.work.res.WokGetDataRes;
import com.isxcode.star.api.pojos.work.res.WokGetStatusRes;
import com.isxcode.star.api.pojos.work.res.WokGetWorkLogRes;
import com.isxcode.star.api.pojos.work.res.WokGetWorkRes;
import com.isxcode.star.api.pojos.work.res.WokQueryWorkRes;
import com.isxcode.star.api.pojos.work.res.WokRunWorkRes;
import com.isxcode.star.api.pojos.yun.agent.req.YagExecuteWorkReq;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetLogRes;
import com.isxcode.star.api.utils.HttpUtils;
import com.isxcode.star.backend.module.calculate.engine.entity.CalculateEngineEntity;
import com.isxcode.star.backend.module.calculate.engine.repository.CalculateEngineRepository;
import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import com.isxcode.star.backend.module.datasource.repository.DatasourceRepository;
import com.isxcode.star.backend.module.engine.node.entity.EngineNodeEntity;
import com.isxcode.star.backend.module.engine.node.repository.EngineNodeRepository;
import com.isxcode.star.backend.module.work.config.entity.WorkConfigEntity;
import com.isxcode.star.backend.module.work.config.repository.WorkConfigRepository;
import com.isxcode.star.backend.module.work.entity.WorkEntity;
import com.isxcode.star.backend.module.work.mapper.WorkMapper;
import com.isxcode.star.backend.module.work.repository.WorkRepository;
import com.isxcode.star.common.exception.SparkYunException;
import com.isxcode.star.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.sql.DriverManager.getConnection;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkBizService {

  private final WorkRepository workRepository;

  private final WorkConfigRepository workConfigRepository;

  private final DatasourceRepository datasourceRepository;

  private final WorkMapper workMapper;

  private final CalculateEngineRepository calculateEngineRepository;

  private final EngineNodeRepository engineNodeRepository;

  public void addWork(WokAddWorkReq addWorkReq) {

    WorkEntity work = workMapper.addWorkReqToWorkEntity(addWorkReq);

    // 添加默认作业配置
    WorkConfigEntity workConfigEntity = workConfigRepository.save(new WorkConfigEntity());
    work.setWorkConfigId(workConfigEntity.getId());

    work.setStatus(WorkStatus.NEW);
    work.setCreateDateTime(LocalDateTime.now());

    workRepository.save(work);
  }

  public Page<WokQueryWorkRes> queryWork(WokQueryWorkReq wocQueryWorkReq) {

    Page<WorkEntity> workflowPage = workRepository.findAllByWorkflowId(wocQueryWorkReq.getWorkflowId(), PageRequest.of(wocQueryWorkReq.getPage(), wocQueryWorkReq.getPageSize()));

    return workMapper.workEntityListToQueryWorkResList(workflowPage);
  }

  public void delWork(String workId) {

    // 判断作业是否存在
    Optional<WorkEntity> workEntityOptional = workRepository.findById(workId);
    if (!workEntityOptional.isPresent()) {
      throw new SparkYunException("作业不存在");
    }

    // 删除作业配置
    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workEntityOptional.get().getWorkConfigId());
    workConfigEntityOptional.ifPresent(workConfigEntity -> workConfigRepository.deleteById(workConfigEntity.getId()));

    workRepository.deleteById(workId);
  }

  public WokRunWorkRes runWork(String workId) {

    Optional<WorkEntity> workEntityOptional = workRepository.findById(workId);
    if (!workEntityOptional.isPresent()) {
      throw new SparkYunException("作业不存在");
    }
    WorkEntity work = workEntityOptional.get();

    switch (work.getWorkType()) {
      case WorkType.EXECUTE_JDBC_SQL:
        return executeSql(work.getWorkConfigId());
      case WorkType.QUERY_JDBC_SQL:
        return querySql(work.getWorkConfigId());
      case WorkType.QUERY_SPARK_SQL:
        return sparkSql(work.getWorkConfigId());
      default:
        throw new SparkYunException("该作业类型暂不支持");
    }
  }

  public WokGetDataRes getData(WokGetDataReq wokGetDataReq) {

    EngineNodeEntity engineNode = getEngineNodeByWorkId(wokGetDataReq.getWorkId());

    String getDataUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getData?applicationId=" + wokGetDataReq.getApplicationId();
    BaseResponse<?> baseResponse = HttpUtils.doGet(getDataUrl, BaseResponse.class);

    if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
      throw new SparkYunException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
    }
    return JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokGetDataRes.class);
  }

  public WokGetStatusRes getStatus(WokGetStatusReq wokGetStatusReq) {

    EngineNodeEntity engineNode = getEngineNodeByWorkId(wokGetStatusReq.getWorkId());

    String getStatusUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getStatus?applicationId=" + wokGetStatusReq.getApplicationId();
    BaseResponse<?> baseResponse = HttpUtils.doGet(getStatusUrl, BaseResponse.class);
    if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
      throw new SparkYunException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
    }
    return JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokGetStatusRes.class);
  }

  public void stopJob(WokStopJobReq wokStopJobReq) {

    EngineNodeEntity engineNode = getEngineNodeByWorkId(wokStopJobReq.getWorkId());

    String stopJobUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/stopJob?applicationId=" + wokStopJobReq.getApplicationId();
    BaseResponse<?> baseResponse = HttpUtils.doGet(stopJobUrl, BaseResponse.class);

    if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
      throw new SparkYunException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
    }
  }

  public EngineNodeEntity getEngineNodeByWorkId(String workId) {

    Optional<WorkEntity> workEntityOptional = workRepository.findById(workId);
    if (!workEntityOptional.isPresent()) {
      throw new RuntimeException("作业不存在");
    }

    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workEntityOptional.get().getWorkConfigId());
    if (!workConfigEntityOptional.isPresent()) {
      throw new SparkYunException("作业异常，不可用作业");
    }
    WorkConfigEntity workConfig = workConfigEntityOptional.get();

    return getEngineWork(workConfig.getCalculateEngineId());
  }

  public WokGetWorkLogRes getWorkLog(WokGetWorkLogReq wokGetWorkLogReq) {

    EngineNodeEntity engineNode = getEngineNodeByWorkId(wokGetWorkLogReq.getWorkId());

    String getLogUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getLog?applicationId=" + wokGetWorkLogReq.getApplicationId();
    BaseResponse<?> baseResponse = HttpUtils.doGet(getLogUrl, BaseResponse.class);
    if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
      throw new SparkYunException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
    }
    YagGetLogRes yagGetLogRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), YagGetLogRes.class);
    return WokGetWorkLogRes.builder().logList(Arrays.asList(yagGetLogRes.getLog().split("\n"))).build();
  }

  public WokRunWorkRes executeSql(String workConfigId) {

    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workConfigId);
    if (!workConfigEntityOptional.isPresent()) {
      throw new SparkYunException("作业异常，不可用作业");
    }
    WorkConfigEntity workConfig = workConfigEntityOptional.get();

    DatasourceEntity datasource = getDatasource(workConfig.getDatasourceId());

    try (Connection connection =
           getConnection(datasource.getJdbcUrl(), datasource.getUsername(), datasource.getPasswd());
         Statement statement = connection.createStatement();) {
      statement.execute(workConfig.getSql());
      return WokRunWorkRes.builder().build();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new SparkYunException("执行异常", e.getMessage());
    }
  }

  public DatasourceEntity getDatasource(String datasourceId) {

    if (Strings.isEmpty(datasourceId)) {
      throw new SparkYunException("作业未配置数据源");
    }

    Optional<DatasourceEntity> datasourceEntityOptional = datasourceRepository.findById(datasourceId);
    if (!datasourceEntityOptional.isPresent()) {
      throw new SparkYunException("数据源不存在");
    }
    DatasourceEntity datasource = datasourceEntityOptional.get();

    try {
      switch (datasource.getDatasourceType()) {
        case DatasourceType.MYSQL:
          Class.forName("com.mysql.cj.jdbc.Driver");
          break;
        case DatasourceType.ORACLE:
          Class.forName("oracle.jdbc.driver.OracleDriver");
          break;
        case DatasourceType.SQL_SERVER:
          Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
          break;
        default:
          throw new SparkYunException("数据源暂不支持");
      }
    } catch (ClassNotFoundException e) {
      throw new SparkYunException("驱动加载异常", e.getMessage());
    }

    return datasource;
  }


  public WokRunWorkRes querySql(String workConfigId) {

    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workConfigId);
    if (!workConfigEntityOptional.isPresent()) {
      throw new SparkYunException("作业异常，不可用作业");
    }
    WorkConfigEntity workConfig = workConfigEntityOptional.get();

    DatasourceEntity datasource = getDatasource(workConfig.getDatasourceId());

    List<List<String>> result = new ArrayList<>();
    try (Connection connection = getConnection(datasource.getJdbcUrl(), datasource.getUsername(), datasource.getPasswd());
         Statement statement = connection.createStatement()) {

      ResultSet resultSet = statement.executeQuery(workConfig.getSql());
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

      return WokRunWorkRes.builder().data(result).build();

    } catch (Exception e) {
      log.error(e.getMessage());
      throw new SparkYunException("作业执行异常", e.getMessage());
    }
  }

  public WokGetWorkRes getWork(String workId) {

    Optional<WorkEntity> workEntityOptional = workRepository.findById(workId);
    if (!workEntityOptional.isPresent()) {
      throw new SparkYunException("作业不存在");
    }

    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workEntityOptional.get().getWorkConfigId());
    if (!workConfigEntityOptional.isPresent()) {
      throw new SparkYunException("作业异常不可用");
    }

    return workMapper.workEntityAndWorkConfigEntityToGetWorkRes(workEntityOptional.get(), workConfigEntityOptional.get());
  }

  public EngineNodeEntity getEngineWork(String calculateEngineId) {

    if (Strings.isEmpty(calculateEngineId)) {
      throw new SparkYunException("作业未配置计算引擎");
    }

    Optional<CalculateEngineEntity> calculateEngineEntityOptional = calculateEngineRepository.findById(calculateEngineId);
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }

    List<EngineNodeEntity> allEngineNodes = engineNodeRepository.findAllByCalculateEngineIdAndStatus(calculateEngineEntityOptional.get().getId(), EngineNodeStatus.ACTIVE);
    if (allEngineNodes.isEmpty()) {
      throw new SparkYunException("计算引擎无可用节点，请换一个计算引擎");
    }
    return allEngineNodes.get(0);
  }

  public WokRunWorkRes sparkSql(String workConfigId) {

    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workConfigId);
    if (!workConfigEntityOptional.isPresent()) {
      throw new SparkYunException("作业异常，不可用作业");
    }
    WorkConfigEntity workConfig = workConfigEntityOptional.get();

    EngineNodeEntity engineNode = getEngineWork(workConfig.getCalculateEngineId());

    YagExecuteWorkReq executeReq = new YagExecuteWorkReq();
    executeReq.setAppName("spark-yun");
    executeReq.setMainClass("com.isxcode.star.plugin.query.sql.Execute");
    executeReq.setAppResourcePath(engineNode.getAgentHomePath() + File.separator + "spark-yun-agent" + File.separator + "plugins" + File.separator + "spark-query-sql-plugin.jar");
    executeReq.setSparkHomePath(engineNode.getAgentHomePath() + File.separator + "spark-yun-agent" + File.separator + "spark-min");
    executeReq.setAgentLibPath(engineNode.getAgentHomePath() + File.separator + "spark-yun-agent" + File.separator + "lib");

    PluginReq pluginReq = new PluginReq();
    pluginReq.setSql(workConfig.getSql());
    pluginReq.setLimit(200);
    Map<String, String> sparkConfig = new HashMap<>();
    sparkConfig.put("spark.executor.memory", "1g");
    sparkConfig.put("spark.driver.memory", "1g");
    pluginReq.setSparkConfig(sparkConfig);

    executeReq.setPluginReq(pluginReq);

    try {
      String executeWorkUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/executeWork";
      BaseResponse<?> baseResponse = HttpUtils.doPost(executeWorkUrl, executeReq, BaseResponse.class);
      if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
        throw new SparkYunException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
      }
      return JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokRunWorkRes.class);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new SparkYunException("提交作业异常", e.getMessage());
    }
  }
}
