package com.isxcode.star.backend.module.work.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.CodeConstants;
import com.isxcode.star.api.constants.EngineNodeStatus;
import com.isxcode.star.api.constants.WorkStatus;
import com.isxcode.star.api.constants.WorkType;
import com.isxcode.star.api.constants.work.instance.InstanceType;
import com.isxcode.star.api.exception.SparkYunException;
import com.isxcode.star.api.pojos.work.req.WokAddWorkReq;
import com.isxcode.star.api.pojos.work.req.WokGetDataReq;
import com.isxcode.star.api.pojos.work.req.WokGetStatusReq;
import com.isxcode.star.api.pojos.work.req.WokGetWorkLogReq;
import com.isxcode.star.api.pojos.work.req.WokQueryWorkReq;
import com.isxcode.star.api.pojos.work.req.WokStopJobReq;
import com.isxcode.star.api.pojos.work.req.WokUpdateWorkReq;
import com.isxcode.star.api.pojos.work.res.WokGetDataRes;
import com.isxcode.star.api.pojos.work.res.WokGetStatusRes;
import com.isxcode.star.api.pojos.work.res.WokGetSubmitLogRes;
import com.isxcode.star.api.pojos.work.res.WokGetWorkLogRes;
import com.isxcode.star.api.pojos.work.res.WokGetWorkRes;
import com.isxcode.star.api.pojos.work.res.WokQueryWorkRes;
import com.isxcode.star.api.pojos.work.res.WokRunWorkRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetLogRes;
import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.api.response.BaseResponse;
import com.isxcode.star.api.utils.HttpUtils;
import com.isxcode.star.backend.module.cluster.entity.ClusterEntity;
import com.isxcode.star.backend.module.cluster.node.entity.ClusterNodeEntity;
import com.isxcode.star.backend.module.cluster.node.repository.ClusterNodeRepository;
import com.isxcode.star.backend.module.cluster.repository.ClusterRepository;
import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import com.isxcode.star.backend.module.datasource.repository.DatasourceRepository;
import com.isxcode.star.backend.module.datasource.service.DatasourceBizService;
import com.isxcode.star.backend.module.work.config.entity.WorkConfigEntity;
import com.isxcode.star.backend.module.work.config.repository.WorkConfigRepository;
import com.isxcode.star.backend.module.work.entity.WorkEntity;
import com.isxcode.star.backend.module.work.instance.entity.WorkInstanceEntity;
import com.isxcode.star.backend.module.work.instance.repository.WorkInstanceRepository;
import com.isxcode.star.backend.module.work.mapper.WorkMapper;
import com.isxcode.star.backend.module.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkBizService {

  private final RunExecuteSqlService runExecuteSqlService;

  private final RunSparkSqlService runSparkSqlService;

  private final WorkRepository workRepository;

  private final WorkConfigRepository workConfigRepository;

  private final DatasourceRepository datasourceRepository;

  private final WorkMapper workMapper;

  private final ClusterRepository calculateEngineRepository;

  private final ClusterNodeRepository engineNodeRepository;

  private final DatasourceBizService datasourceBizService;

  private final WorkInstanceRepository workInstanceRepository;

  public void addWork(WokAddWorkReq addWorkReq) {

    WorkEntity work = workMapper.addWorkReqToWorkEntity(addWorkReq);

    // 添加默认作业配置
    WorkConfigEntity workConfigEntity = workConfigRepository.save(new WorkConfigEntity());
    work.setConfigId(workConfigEntity.getId());

    work.setStatus(WorkStatus.UN_PUBLISHED);

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
    WorkEntity work = workEntityOptional.get();

    // 如果不是已下线状态或者未发布状态 不让删除
    if (WorkStatus.UN_PUBLISHED.equals(work.getStatus()) || WorkStatus.STOP.equals(work.getStatus())) {
      // 删除作业配置
      Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workEntityOptional.get().getConfigId());
      workConfigEntityOptional.ifPresent(workConfigEntity -> workConfigRepository.deleteById(workConfigEntity.getId()));

      workRepository.deleteById(workId);
    } else {
      throw new SparkYunException("请下线作业");
    }
  }

  public WokRunWorkRes submitWork(String workId) {

    Optional<WorkEntity> workEntityOptional = workRepository.findById(workId);
    if (!workEntityOptional.isPresent()) {
      throw new SparkYunException("作业不存在");
    }
    WorkEntity work = workEntityOptional.get();

    WorkInstanceEntity workInstanceEntity = new WorkInstanceEntity();
    workInstanceEntity.setWorkId(workId);
    workInstanceEntity.setInstanceType(InstanceType.MANUAL);
    workInstanceRepository.saveAndFlush(workInstanceEntity);

    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(work.getConfigId());
    if (!workConfigEntityOptional.isPresent()) {
      throw new SparkYunException("作业异常，请联系开发者");
    }
    WorkConfigEntity workConfig = workConfigEntityOptional.get();

    switch (work.getWorkType()) {
      case WorkType.EXECUTE_JDBC_SQL:
        runExecuteSqlService.run(workConfig.getDatasourceId(), workConfig.getSqlScript(), workInstanceEntity.getId(),TENANT_ID.get(),USER_ID.get());
        return WokRunWorkRes.builder().instanceId(workInstanceEntity.getId()).build();
      case WorkType.QUERY_JDBC_SQL:
        querySql(work.getConfigId());
        return new WokRunWorkRes();
      case WorkType.QUERY_SPARK_SQL:
        runSparkSqlService.run(workConfig.getClusterId(), workConfig.getSqlScript(), workInstanceEntity.getId(), TENANT_ID.get(), USER_ID.get());
        return WokRunWorkRes.builder().instanceId(workInstanceEntity.getId()).build();
      default:
        throw new SparkYunException("该作业类型暂不支持");
    }
  }

  public WokGetDataRes getData(String instanceId) {

    // 获取实例
    Optional<WorkInstanceEntity> instanceEntityOptional = workInstanceRepository.findById(instanceId);
    if (!instanceEntityOptional.isPresent()) {
      throw new SparkYunException("实例不存在");
    }
    WorkInstanceEntity workInstanceEntity = instanceEntityOptional.get();

    // 获取sparkStar返回对象
    WokRunWorkRes wokRunWorkRes = JSON.parseObject(workInstanceEntity.getSparkStarRes(), WokRunWorkRes.class);
    if (Strings.isEmpty(wokRunWorkRes.getApplicationId())) {
      return WokGetDataRes.builder().data(new ArrayList<>()).build();
    }

    ClusterNodeEntity engineNode = getEngineNodeByWorkId(workInstanceEntity.getWorkId());

    String getDataUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getData?applicationId=" + wokRunWorkRes.getApplicationId();
    BaseResponse<?> baseResponse = HttpUtils.doGet(getDataUrl, BaseResponse.class);

    if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
      throw new SparkYunException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
    }
    return JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokGetDataRes.class);
  }

  public WokGetStatusRes getStatus(String instanceId) {

    Optional<WorkInstanceEntity> workInstanceEntityOptional = workInstanceRepository.findById(instanceId);
    if (!workInstanceEntityOptional.isPresent()) {
      throw new SparkYunException("实例暂未生成请稍后再试");
    }
    WorkInstanceEntity workInstanceEntity = workInstanceEntityOptional.get();

    WokRunWorkRes wokRunWorkRes = JSON.parseObject(workInstanceEntity.getSparkStarRes(), WokRunWorkRes.class);

    if (Strings.isEmpty(wokRunWorkRes.getApplicationId())) {
      return WokGetStatusRes.builder().yarnApplicationState("NO_RUNNING").build();
    }

    ClusterNodeEntity engineNode = getEngineNodeByWorkId(workInstanceEntity.getWorkId());

    String getStatusUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getStatus?applicationId=" + wokRunWorkRes.getApplicationId();
    BaseResponse<?> baseResponse = HttpUtils.doGet(getStatusUrl, BaseResponse.class);
    if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
      throw new SparkYunException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
    }
    return JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokGetStatusRes.class);
  }

  public void stopJob(WokStopJobReq wokStopJobReq) {

    ClusterNodeEntity engineNode = getEngineNodeByWorkId(wokStopJobReq.getWorkId());

    String stopJobUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/stopJob?applicationId=" + wokStopJobReq.getApplicationId();
    BaseResponse<?> baseResponse = HttpUtils.doGet(stopJobUrl, BaseResponse.class);

    if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
      throw new SparkYunException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
    }
  }

  public ClusterNodeEntity getEngineNodeByWorkId(String workId) {

    Optional<WorkEntity> workEntityOptional = workRepository.findById(workId);
    if (!workEntityOptional.isPresent()) {
      throw new RuntimeException("作业不存在");
    }

    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workEntityOptional.get().getConfigId());
    if (!workConfigEntityOptional.isPresent()) {
      throw new SparkYunException("作业异常，不可用作业");
    }
    WorkConfigEntity workConfig = workConfigEntityOptional.get();

    return getEngineWork(workConfig.getClusterId());
  }

  public WokGetWorkLogRes getWorkLog(String instanceId) {

    Optional<WorkInstanceEntity> workInstanceEntityOptional = workInstanceRepository.findById(instanceId);
    if (!workInstanceEntityOptional.isPresent()) {
      throw new SparkYunException("实例暂未生成，请稍后再试");
    }
    WorkInstanceEntity workInstanceEntity = workInstanceEntityOptional.get();

    WokRunWorkRes wokRunWorkRes = JSON.parseObject(workInstanceEntity.getSparkStarRes(), WokRunWorkRes.class);

    if (Strings.isEmpty(wokRunWorkRes.getApplicationId())) {
      return WokGetWorkLogRes.builder().yarnLog("待运行").build();
    }

    ClusterNodeEntity engineNode = getEngineNodeByWorkId(workInstanceEntity.getWorkId());

    String getLogUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getLog?applicationId=" + wokRunWorkRes.getApplicationId();
    BaseResponse<?> baseResponse = HttpUtils.doGet(getLogUrl, BaseResponse.class);
    if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
      throw new SparkYunException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
    }
    YagGetLogRes yagGetLogRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), YagGetLogRes.class);
    return WokGetWorkLogRes.builder().yarnLog(yagGetLogRes.getLog()).build();
  }

  @Async("sparkYunWorkThreadPool")
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
    try (Connection connection = datasourceBizService.getDbConnection(datasource);
         Statement statement = connection.createStatement()) {

      String regex = "/\\*(?:.|[\\n\\r])*?\\*/|--.*";
      String noCommentSql = workConfig.getSqlScript().replaceAll(regex, "");
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

  public DatasourceEntity getDatasource(String datasourceId) {

    if (Strings.isEmpty(datasourceId)) {
      throw new SparkYunException("作业未配置数据源");
    }

    Optional<DatasourceEntity> datasourceEntityOptional = datasourceRepository.findById(datasourceId);
    if (!datasourceEntityOptional.isPresent()) {
      throw new SparkYunException("数据源不存在");
    }
    DatasourceEntity datasource = datasourceEntityOptional.get();

    datasourceBizService.loadDriverClass(datasource.getDbType());

    return datasource;
  }

  public WokGetWorkRes getWork(String workId) {

    Optional<WorkEntity> workEntityOptional = workRepository.findById(workId);
    if (!workEntityOptional.isPresent()) {
      throw new SparkYunException("作业不存在");
    }

    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workEntityOptional.get().getConfigId());
    if (!workConfigEntityOptional.isPresent()) {
      throw new SparkYunException("作业异常不可用");
    }

    return workMapper.workEntityAndWorkConfigEntityToGetWorkRes(workEntityOptional.get(), workConfigEntityOptional.get());
  }

  public ClusterNodeEntity getEngineWork(String calculateEngineId) {

    if (Strings.isEmpty(calculateEngineId)) {
      throw new SparkYunException("作业未配置计算引擎");
    }

    Optional<ClusterEntity> calculateEngineEntityOptional = calculateEngineRepository.findById(calculateEngineId);
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }

    List<ClusterNodeEntity> allEngineNodes = engineNodeRepository.findAllByClusterIdAndStatus(calculateEngineEntityOptional.get().getId(), EngineNodeStatus.RUNNING);
    if (allEngineNodes.isEmpty()) {
      throw new SparkYunException("计算引擎无可用节点，请换一个计算引擎");
    }
    return allEngineNodes.get(0);
  }

  public WokGetSubmitLogRes getSubmitLog(String instanceId) {

    Optional<WorkInstanceEntity> workInstanceEntityOptional = workInstanceRepository.findById(instanceId);
    if (!workInstanceEntityOptional.isPresent()) {
      throw new SparkYunException("请稍后再试");
    }
    WorkInstanceEntity workInstanceEntity = workInstanceEntityOptional.get();

    return WokGetSubmitLogRes.builder().log(workInstanceEntity.getSubmitLog()).status(workInstanceEntity.getStatus()).build();
  }
}
