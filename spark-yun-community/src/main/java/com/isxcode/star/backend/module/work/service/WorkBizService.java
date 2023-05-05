package com.isxcode.star.backend.module.work.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.CodeConstants;
import com.isxcode.star.api.constants.datasource.DatasourceType;
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
import com.isxcode.star.api.pojos.work.req.WokUpdateWorkReq;
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
import com.isxcode.star.api.exception.SparkYunException;
import com.isxcode.star.api.response.BaseResponse;
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

  public void updateWork(WokUpdateWorkReq wokUpdateWorkReq) {

    Optional<WorkEntity> workEntityOptional = workRepository.findById(wokUpdateWorkReq.getId());
    if (!workEntityOptional.isPresent()) {
      throw new SparkYunException("作业不存在");
    }

    WorkEntity work = workMapper.updateWorkReqToWorkEntity(wokUpdateWorkReq, workEntityOptional.get());

    workRepository.save(work);
  }

  public Page<WokQueryWorkRes> queryWork(WokQueryWorkReq wocQueryWorkReq) {

    Page<WorkEntity> workflowPage = workRepository.searchAllByWorkflowId(wocQueryWorkReq.getSearchKeyWord(), wocQueryWorkReq.getWorkflowId(), PageRequest.of(wocQueryWorkReq.getPage(), wocQueryWorkReq.getPageSize()));

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

    if (Strings.isEmpty(wokGetDataReq.getApplicationId())) {
      return WokGetDataRes.builder().data(new ArrayList<>()).build();
    }

    EngineNodeEntity engineNode = getEngineNodeByWorkId(wokGetDataReq.getWorkId());

    String getDataUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getData?applicationId=" + wokGetDataReq.getApplicationId();
    BaseResponse<?> baseResponse = HttpUtils.doGet(getDataUrl, BaseResponse.class);

    if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
      throw new SparkYunException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
    }
    return JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokGetDataRes.class);
  }

  public WokGetStatusRes getStatus(WokGetStatusReq wokGetStatusReq) {

    if (Strings.isEmpty(wokGetStatusReq.getApplicationId())) {
      return WokGetStatusRes.builder().yarnApplicationState("NO_RUNNING").build();
    }

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

    if (Strings.isEmpty(wokGetWorkLogReq.getApplicationId())) {
      return WokGetWorkLogRes.builder().yarnLog("待运行").build();
    }

    EngineNodeEntity engineNode = getEngineNodeByWorkId(wokGetWorkLogReq.getWorkId());

    String getLogUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getLog?applicationId=" + wokGetWorkLogReq.getApplicationId();
    BaseResponse<?> baseResponse = HttpUtils.doGet(getLogUrl, BaseResponse.class);
    if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
      throw new SparkYunException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
    }
    YagGetLogRes yagGetLogRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), YagGetLogRes.class);
    return WokGetWorkLogRes.builder().yarnLog(yagGetLogRes.getLog()).build();
  }

  public WokRunWorkRes querySql(String workConfigId) {

    StringBuilder logBuilder = new StringBuilder();
    String infoHeader = LocalDateTime.now() + " INFO : ";
    String errorHeader = LocalDateTime.now() + " ERROR : ";

    // 检测配置是否存在
    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workConfigId);
    logBuilder.append(infoHeader + "开始检测作业 \n");
    if (!workConfigEntityOptional.isPresent()) {
      logBuilder.append(errorHeader + "检测作业失败  \n");
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("ERROR").build();
    }
    WorkConfigEntity workConfig = workConfigEntityOptional.get();
    logBuilder.append(infoHeader + "检测作业完成  \n");

    // 检测数据源是否存在
    logBuilder.append(infoHeader + "开始检测运行环境 \n");
    if (Strings.isEmpty(workConfig.getDatasourceId())) {
      logBuilder.append(errorHeader + "检测运行环境失败: 未配置有效数据源  \n");
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("ERROR").build();
    }
    logBuilder.append(infoHeader + "检测运行环境完成  \n");

    // 开始执行作业
    logBuilder.append(infoHeader + "开始执行作业 \n");
    DatasourceEntity datasource = getDatasource(workConfig.getDatasourceId());

    // 初始化返回结果
    List<List<String>> result = new ArrayList<>();
    try (Connection connection = getConnection(datasource.getJdbcUrl(), datasource.getUsername(), datasource.getPasswd());
         Statement statement = connection.createStatement()) {

      String regex = "/\\*(?:.|[\\n\\r])*?\\*/|--.*";
      String noCommentSql = workConfig.getSql().replaceAll(regex, "");
      String realSql = noCommentSql.replace("\n", " ");
      String[] sqls = realSql.split(";");

      for (int i = 0; i < sqls.length - 1; i++) {

        logBuilder.append(infoHeader + "开始执行SQL: " + sqls[i] + " \n");
        if (!Strings.isEmpty(sqls[i])) {
          statement.execute(sqls[i]);
        }
        logBuilder.append(infoHeader + "SQL执行成功  \n");
      }

      // 执行最后一句查询语句
      logBuilder.append(infoHeader + "开始查询SQL: " + sqls[sqls.length - 1] + " \n");
      ResultSet resultSet = statement.executeQuery(sqls[sqls.length - 1]);
      logBuilder.append(infoHeader + "查询SQL执行成功  \n");

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
      logBuilder.append(infoHeader + "[SUCCESS] \n");
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("SUCCESS").data(result).build();
    } catch (Exception e) {
      logBuilder.append(errorHeader + e.getMessage() + "\n");
      log.error(e.getMessage());
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("ERROR").build();
    }
  }

  public WokRunWorkRes executeSql(String workConfigId) {

    StringBuilder logBuilder = new StringBuilder();
    String infoHeader = LocalDateTime.now() + " INFO : ";
    String errorHeader = LocalDateTime.now() + " ERROR : ";

    // 检测配置是否存在
    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workConfigId);
    logBuilder.append(infoHeader + "开始检测作业 \n");
    if (!workConfigEntityOptional.isPresent()) {
      logBuilder.append(errorHeader + "检测作业失败: 作业配置不存在 \n");
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("ERROR").build();
    }
    WorkConfigEntity workConfig = workConfigEntityOptional.get();
    logBuilder.append(infoHeader + "检测作业完成  \n");

    // 检测数据源是否存在
    logBuilder.append(infoHeader + "开始检测运行环境 \n");
    if (Strings.isEmpty(workConfig.getDatasourceId())) {
      logBuilder.append(errorHeader + "检测运行环境失败: 未配置有效数据源  \n");
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("ERROR").build();
    }
    logBuilder.append(infoHeader + "检测运行环境完成  \n");

    // 开始执行作业
    logBuilder.append(infoHeader + "开始执行作业 \n");
    DatasourceEntity datasource = getDatasource(workConfig.getDatasourceId());
    try (Connection connection = getConnection(datasource.getJdbcUrl(), datasource.getUsername(), datasource.getPasswd());
         Statement statement = connection.createStatement();) {

      String regex = "/\\*(?:.|[\\n\\r])*?\\*/|--.*";
      String noCommentSql = workConfig.getSql().replaceAll(regex, "");
      String realSql = noCommentSql.replace("\n", " ");
      for (String sql : realSql.split(";")) {

        logBuilder.append(infoHeader + "开始执行SQL: " + sql + " \n");
        if (!Strings.isEmpty(sql)) {
          statement.execute(sql);
        }
        logBuilder.append(infoHeader + "SQL执行成功  \n");
      }
      logBuilder.append(infoHeader + "[SUCCESS] \n");
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("SUCCESS").build();
    } catch (Exception e) {
      logBuilder.append(errorHeader + e.getMessage() + "\n");
      log.error(e.getMessage());
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("ERROR").build();
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
      switch (datasource.getDbType()) {
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

    StringBuilder logBuilder = new StringBuilder();
    String infoHeader = LocalDateTime.now() + " INFO : ";
    String errorHeader = LocalDateTime.now() + " ERROR : ";

    logBuilder.append(infoHeader + "开始检查作业  \n");
    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workConfigId);
    if (!workConfigEntityOptional.isPresent()) {
      logBuilder.append(errorHeader + "检查作业失败 : 作业配置不存在 \n");
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("ERROR").build();
    }
    WorkConfigEntity workConfig = workConfigEntityOptional.get();
    logBuilder.append(infoHeader + "检查作业完成  \n");

    logBuilder.append(infoHeader + "开始申请资源  \n");
    if (Strings.isEmpty(workConfig.getCalculateEngineId())) {
      logBuilder.append(errorHeader + "申请资源失败 : 未配置计算引擎 \n");
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("ERROR").build();
    }

    Optional<CalculateEngineEntity> calculateEngineEntityOptional = calculateEngineRepository.findById(workConfig.getCalculateEngineId());
    if (!calculateEngineEntityOptional.isPresent()) {
      logBuilder.append(errorHeader + "申请资源失败 : 计算引擎不存在 \n");
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("ERROR").build();
    }

    List<EngineNodeEntity> allEngineNodes = engineNodeRepository.findAllByCalculateEngineIdAndStatus(calculateEngineEntityOptional.get().getId(), EngineNodeStatus.ACTIVE);
    if (allEngineNodes.isEmpty()) {
      logBuilder.append(errorHeader + "申请资源失败 : 集群不存在可用节点，请切换一个集群 \n");
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("ERROR").build();
    }
    EngineNodeEntity engineNode = allEngineNodes.get(0);
    logBuilder.append(infoHeader + "申请资源完成，激活节点:【" + engineNode.getName() + "】\n");

    logBuilder.append(infoHeader + "开始构建作业  \n");
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
    logBuilder.append(infoHeader + "构建作业完，SparkConfig: \n");
    sparkConfig.forEach((k, v) -> logBuilder.append(infoHeader + k + ":" + v + " \n"));

    try {
      logBuilder.append(infoHeader + "开始提交作业  \n");
      String executeWorkUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/executeWork";
      BaseResponse<?> baseResponse = HttpUtils.doPost(executeWorkUrl, executeReq, BaseResponse.class);
      if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
        logBuilder.append(errorHeader + "提交作业失败 : " + baseResponse.getErr() + "\n");
        return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("ERROR").build();
      }
      WokRunWorkRes wokRunWorkRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokRunWorkRes.class);
      logBuilder.append(infoHeader + "提交作业完成  \n");
      wokRunWorkRes.setLog(logBuilder.toString());
      wokRunWorkRes.setExecuteStatus("SUCCESS");
      return wokRunWorkRes;

    } catch (Exception e) {
      log.error(e.getMessage());
      logBuilder.append(errorHeader + "提交作业失败 : " + e.getMessage() + "\n");
      return WokRunWorkRes.builder().log(logBuilder.toString()).executeStatus("ERROR").build();
    }
  }
}
