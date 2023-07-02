package com.isxcode.star.backend.module.work;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.cluster.ClusterNodeStatus;
import com.isxcode.star.api.constants.work.WorkDefault;
import com.isxcode.star.api.constants.work.WorkLog;
import com.isxcode.star.api.constants.work.WorkStatus;
import com.isxcode.star.api.constants.work.WorkType;
import com.isxcode.star.api.constants.work.instance.InstanceStatus;
import com.isxcode.star.api.constants.work.instance.InstanceType;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.exceptions.WorkRunException;
import com.isxcode.star.api.pojos.base.BaseResponse;
import com.isxcode.star.api.pojos.work.req.WokAddWorkReq;
import com.isxcode.star.api.pojos.work.req.WokQueryWorkReq;
import com.isxcode.star.api.pojos.work.req.WokUpdateWorkReq;
import com.isxcode.star.api.pojos.work.res.*;
import com.isxcode.star.api.pojos.work.dto.WorkRunContext;
import com.isxcode.star.backend.module.cluster.ClusterEntity;
import com.isxcode.star.backend.module.cluster.ClusterRepository;
import com.isxcode.star.backend.module.cluster.node.ClusterNodeEntity;
import com.isxcode.star.backend.module.cluster.node.ClusterNodeRepository;
import com.isxcode.star.backend.module.work.config.WorkConfigBizService;
import com.isxcode.star.backend.module.work.config.WorkConfigEntity;
import com.isxcode.star.backend.module.work.config.WorkConfigRepository;
import com.isxcode.star.backend.module.work.instance.WorkInstanceEntity;
import com.isxcode.star.backend.module.work.instance.WorkInstanceRepository;
import com.isxcode.star.backend.module.work.run.WorkExecutor;
import com.isxcode.star.backend.module.work.run.WorkExecutorFactory;
import com.isxcode.star.common.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkBizService {

  private final WorkExecutorFactory workExecutorFactory;

  private final WorkRepository workRepository;

  private final WorkConfigRepository workConfigRepository;

  private final WorkMapper workMapper;

  private final ClusterRepository calculateEngineRepository;

  private final ClusterNodeRepository engineNodeRepository;

  private final WorkInstanceRepository workInstanceRepository;

  private final WorkConfigBizService workConfigBizService;

  public WorkEntity getWorkEntity(String workId) {

    Optional<WorkEntity> workEntityOptional = workRepository.findById(workId);
    if (!workEntityOptional.isPresent()) {
      throw new SparkYunException("作业不存在");
    }
    return workEntityOptional.get();
  }

  public void addWork(WokAddWorkReq addWorkReq) {

    WorkEntity work = workMapper.addWorkReqToWorkEntity(addWorkReq);

    // 如果是sparkSql作业，初始化sparkConfig
    WorkConfigEntity workConfigEntity = new WorkConfigEntity();
    if (WorkType.QUERY_SPARK_SQL.equals(addWorkReq.getWorkType())) {
      workConfigEntity.setSparkConfig(WorkDefault.DEFAULT_SPARK_CONF);
    }

    // 添加默认作业配置
    workConfigEntity = workConfigRepository.save(workConfigEntity);
    work.setConfigId(workConfigEntity.getId());
    work.setStatus(WorkStatus.UN_PUBLISHED);

    workRepository.save(work);
  }

  public void updateWork(WokUpdateWorkReq wokUpdateWorkReq) {

    Optional<WorkEntity> workEntityOptional = workRepository.findById(wokUpdateWorkReq.getId());
    if (!workEntityOptional.isPresent()) {
      throw new SparkYunException("作业不存在");
    }

    WorkEntity work =
        workMapper.updateWorkReqToWorkEntity(wokUpdateWorkReq, workEntityOptional.get());

    workRepository.save(work);
  }

  public Page<WokQueryWorkRes> queryWork(WokQueryWorkReq wocQueryWorkReq) {

    Page<WorkEntity> workflowPage =
        workRepository.searchAllByWorkflowId(
            wocQueryWorkReq.getSearchKeyWord(),
            wocQueryWorkReq.getWorkflowId(),
            PageRequest.of(wocQueryWorkReq.getPage(), wocQueryWorkReq.getPageSize()));

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
    if (WorkStatus.UN_PUBLISHED.equals(work.getStatus())
        || WorkStatus.STOP.equals(work.getStatus())) {
      // 删除作业配置
      Optional<WorkConfigEntity> workConfigEntityOptional =
          workConfigRepository.findById(workEntityOptional.get().getConfigId());
      workConfigEntityOptional.ifPresent(
          workConfigEntity -> workConfigRepository.deleteById(workConfigEntity.getId()));

      workRepository.deleteById(workId);
    } else {
      throw new SparkYunException("请下线作业");
    }
  }

  public WorkInstanceEntity genWorkInstance(String workId) {

    WorkInstanceEntity workInstanceEntity = new WorkInstanceEntity();
    workInstanceEntity.setWorkId(workId);
    workInstanceEntity.setInstanceType(InstanceType.MANUAL);
    return workInstanceRepository.saveAndFlush(workInstanceEntity);
  }

  public WorkRunContext genWorkRunContext(String instanceId, WorkEntity work, WorkConfigEntity workConfig) {

    return WorkRunContext.builder().datasourceId(workConfig.getDatasourceId())
      .sqlScript(workConfig.getSqlScript())
      .instanceId(instanceId)
      .tenantId(TENANT_ID.get())
      .clusterId(workConfig.getClusterId())
      .workType(work.getWorkType())
      .workId(work.getId())
      .sparkConfig(JSON.parseObject(workConfig.getSparkConfig(), Map.class))
      .userId(USER_ID.get())
      .build();
  }
  /**
   * 提交作业.
   */
  public WokRunWorkRes submitWork(String workId) {

    // 获取作业信息
    WorkEntity work = getWorkEntity(workId);

    // 初始化作业实例
    WorkInstanceEntity workInstance = genWorkInstance(work.getId());

    // 获取作业配置
    WorkConfigEntity workConfig = workConfigBizService.getWorkConfigEntity(work.getConfigId());

    // 初始化作业运行上下文
    WorkRunContext workRunContext = genWorkRunContext(workInstance.getId(), work, workConfig);

    // 异步运行作业
    WorkExecutor workExecutor = workExecutorFactory.create(work.getWorkType());
    workExecutor.asyncExecute(workRunContext);

    // 返回作业的实例id
    return WokRunWorkRes.builder().instanceId(workInstance.getId()).build();
  }

  public WokGetDataRes getData(String instanceId) {

    // 获取实例
    Optional<WorkInstanceEntity> instanceEntityOptional =
        workInstanceRepository.findById(instanceId);
    if (!instanceEntityOptional.isPresent()) {
      throw new SparkYunException("实例不存在");
    }
    WorkInstanceEntity workInstanceEntity = instanceEntityOptional.get();
    if (!InstanceStatus.SUCCESS.equals(workInstanceEntity.getStatus())) {
      throw new SparkYunException("只有成功实例，才可查看数据");
    }
    if (Strings.isEmpty(workInstanceEntity.getResultData())) {
      throw new SparkYunException("请等待作业运行完毕或者对应作业无返回结果");
    }

    if (Strings.isEmpty(workInstanceEntity.getYarnLog())) {
      return new WokGetDataRes(JSON.parseArray(workInstanceEntity.getResultData()));
    }
    return JSON.parseObject(workInstanceEntity.getResultData(), WokGetDataRes.class);
  }

  public WokGetStatusRes getStatus(String instanceId) {

    Optional<WorkInstanceEntity> workInstanceEntityOptional =
        workInstanceRepository.findById(instanceId);
    if (!workInstanceEntityOptional.isPresent()) {
      throw new SparkYunException("实例暂未生成请稍后再试");
    }
    WorkInstanceEntity workInstanceEntity = workInstanceEntityOptional.get();

    if (Strings.isEmpty(workInstanceEntity.getSparkStarRes())) {
      throw new SparkYunException("请等待作业提交完毕");
    }

    return JSON.parseObject(workInstanceEntity.getSparkStarRes(), WokGetStatusRes.class);
  }

  /** 中止作业. */
  public void stopJob(String instanceId) {

    // 通过实例 获取workId
    Optional<WorkInstanceEntity> workInstanceEntityOptional =
        workInstanceRepository.findById(instanceId);
    if (!workInstanceEntityOptional.isPresent()) {
      throw new SparkYunException("实例不存在");
    }
    WorkInstanceEntity workInstanceEntity = workInstanceEntityOptional.get();

    if (InstanceStatus.SUCCESS.equals(workInstanceEntity.getStatus())) {
      throw new SparkYunException("已经成功，无法中止");
    }

    if (InstanceStatus.ABORT.equals(workInstanceEntity.getStatus())) {
      throw new SparkYunException("已中止");
    }

    // 获取中作业id
    if (InstanceType.MANUAL.equals(workInstanceEntity.getInstanceType())) {
      WorkEntity workEntity = workRepository.findById(workInstanceEntity.getWorkId()).get();

      // 作业类型不对返回
      if (!WorkType.QUERY_SPARK_SQL.equals(workEntity.getWorkType())) {
        throw new SparkYunException("只有sparkSql作业才支持中止");
      }

      WorkConfigEntity workConfigEntity =
          workConfigRepository.findById(workEntity.getConfigId()).get();
      List<ClusterNodeEntity> allEngineNodes =
          engineNodeRepository.findAllByClusterIdAndStatus(
              workConfigEntity.getClusterId(), ClusterNodeStatus.RUNNING);
      if (allEngineNodes.isEmpty()) {
        throw new WorkRunException(
            LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
      }

      Optional<ClusterEntity> clusterEntityOptional =
          calculateEngineRepository.findById(workConfigEntity.getClusterId());
      if (!clusterEntityOptional.isPresent()) {
        throw new SparkYunException("集群不存在");
      }

      // 节点选择随机数
      ClusterNodeEntity engineNode =
          allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

      if (Strings.isEmpty(workInstanceEntity.getSparkStarRes())) {
        throw new SparkYunException("还未提交，请稍后再试");
      }

      // 解析实例的状态信息
      WokRunWorkRes wokRunWorkRes =
          JSON.parseObject(workInstanceEntity.getSparkStarRes(), WokRunWorkRes.class);

      String stopJobUrl =
          "http://"
              + engineNode.getHost()
              + ":"
              + engineNode.getAgentPort()
              + "/yag/stopJob?appId="
              + wokRunWorkRes.getAppId()
              + "&agentType="
              + clusterEntityOptional.get().getClusterType();
      BaseResponse<?> baseResponse = HttpUtils.doGet(stopJobUrl, BaseResponse.class);

      if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
        throw new SparkYunException(
            baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
      }

      // 修改实例状态
      workInstanceEntity.setStatus(InstanceStatus.ABORT);
      String submitLog =
          workInstanceEntity.getSubmitLog()
              + LocalDateTime.now()
              + WorkLog.SUCCESS_INFO
              + "已中止  \n";
      workInstanceEntity.setSubmitLog(submitLog);
      workInstanceEntity.setExecEndDateTime(new Date());
      workInstanceRepository.saveAndFlush(workInstanceEntity);
    }
  }

  public ClusterNodeEntity getEngineNodeByWorkId(String workId) {

    Optional<WorkEntity> workEntityOptional = workRepository.findById(workId);
    if (!workEntityOptional.isPresent()) {
      throw new RuntimeException("作业不存在");
    }

    Optional<WorkConfigEntity> workConfigEntityOptional =
        workConfigRepository.findById(workEntityOptional.get().getConfigId());
    if (!workConfigEntityOptional.isPresent()) {
      throw new SparkYunException("作业异常，不可用作业");
    }
    WorkConfigEntity workConfig = workConfigEntityOptional.get();

    return getEngineWork(workConfig.getClusterId());
  }

  public WokGetWorkLogRes getWorkLog(String instanceId) {

    Optional<WorkInstanceEntity> workInstanceEntityOptional =
        workInstanceRepository.findById(instanceId);
    if (!workInstanceEntityOptional.isPresent()) {
      throw new SparkYunException("实例暂未生成，请稍后再试");
    }

    WorkInstanceEntity workInstanceEntity = workInstanceEntityOptional.get();
    if (Strings.isEmpty(workInstanceEntity.getYarnLog())) {
      throw new SparkYunException("请等待作业运行完毕");
    }
    return WokGetWorkLogRes.builder().yarnLog(workInstanceEntity.getYarnLog()).build();
  }

  public WokGetWorkRes getWork(String workId) {

    Optional<WorkEntity> workEntityOptional = workRepository.findById(workId);
    if (!workEntityOptional.isPresent()) {
      throw new SparkYunException("作业不存在");
    }

    Optional<WorkConfigEntity> workConfigEntityOptional =
        workConfigRepository.findById(workEntityOptional.get().getConfigId());
    if (!workConfigEntityOptional.isPresent()) {
      throw new SparkYunException("作业异常不可用");
    }

    return workMapper.workEntityAndWorkConfigEntityToGetWorkRes(
        workEntityOptional.get(), workConfigEntityOptional.get());
  }

  public ClusterNodeEntity getEngineWork(String calculateEngineId) {

    if (Strings.isEmpty(calculateEngineId)) {
      throw new SparkYunException("作业未配置计算引擎");
    }

    Optional<ClusterEntity> calculateEngineEntityOptional =
        calculateEngineRepository.findById(calculateEngineId);
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }

    List<ClusterNodeEntity> allEngineNodes =
        engineNodeRepository.findAllByClusterIdAndStatus(
            calculateEngineEntityOptional.get().getId(), ClusterNodeStatus.RUNNING);
    if (allEngineNodes.isEmpty()) {
      throw new SparkYunException("计算引擎无可用节点，请换一个计算引擎");
    }
    return allEngineNodes.get(0);
  }

  public WokGetSubmitLogRes getSubmitLog(String instanceId) {

    Optional<WorkInstanceEntity> workInstanceEntityOptional =
        workInstanceRepository.findById(instanceId);
    if (!workInstanceEntityOptional.isPresent()) {
      throw new SparkYunException("请稍后再试");
    }
    WorkInstanceEntity workInstanceEntity = workInstanceEntityOptional.get();

    return WokGetSubmitLogRes.builder()
        .log(workInstanceEntity.getSubmitLog())
        .status(workInstanceEntity.getStatus())
        .build();
  }
}
