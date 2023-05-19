package com.isxcode.star.backend.module.work.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.CodeConstants;
import com.isxcode.star.api.constants.EngineNodeStatus;
import com.isxcode.star.api.constants.PathConstants;
import com.isxcode.star.api.constants.work.WorkLog;
import com.isxcode.star.api.constants.work.instance.InstanceStatus;
import com.isxcode.star.api.exception.WorkRunException;
import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import com.isxcode.star.api.pojos.work.res.WokRunWorkRes;
import com.isxcode.star.api.pojos.yun.agent.req.YagExecuteWorkReq;
import com.isxcode.star.api.response.BaseResponse;
import com.isxcode.star.api.utils.HttpUtils;
import com.isxcode.star.backend.module.cluster.entity.ClusterEntity;
import com.isxcode.star.backend.module.cluster.node.entity.ClusterNodeEntity;
import com.isxcode.star.backend.module.cluster.node.repository.ClusterNodeRepository;
import com.isxcode.star.backend.module.cluster.repository.ClusterRepository;
import com.isxcode.star.backend.module.datasource.repository.DatasourceRepository;
import com.isxcode.star.backend.module.datasource.service.DatasourceBizService;
import com.isxcode.star.backend.module.work.config.entity.WorkConfigEntity;
import com.isxcode.star.backend.module.work.instance.entity.WorkInstanceEntity;
import com.isxcode.star.backend.module.work.instance.repository.WorkInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RunSparkSqlService {

  private final DatasourceBizService datasourceBizService;

  private final DatasourceRepository datasourceRepository;

  private final WorkInstanceRepository workInstanceRepository;

  private final ClusterRepository clusterRepository;

  private final ClusterNodeRepository clusterNodeRepository;

  @Async("sparkYunWorkThreadPool")
  public void run(String clusterId, String sqlScript, String instanceId, String tenantId, String userId) {

    USER_ID.set(userId);
    TENANT_ID.set(tenantId);

    Optional<WorkInstanceEntity> instanceEntityOptional = workInstanceRepository.findById(instanceId);
    if (!instanceEntityOptional.isPresent()) {
      return;
    }
    WorkInstanceEntity instance = instanceEntityOptional.get();

    StringBuilder logBuilder = new StringBuilder();
    logBuilder.append(WorkLog.SUCCESS_INFO + "提交作业完成 \n");

    instance.setStatus(InstanceStatus.RUNNING);
    instance.setSubmitLog(logBuilder.toString());
    instance.setExecStartDateTime(new Date());
    workInstanceRepository.saveAndFlush(instance);

    try {
      executeSparkSql(clusterId, sqlScript, instance, logBuilder);
      instance.setStatus(InstanceStatus.SUCCESS);
      instance.setSubmitLog(logBuilder.toString());
      instance.setExecEndDateTime(new Date());
      workInstanceRepository.saveAndFlush(instance);
    } catch (WorkRunException | IOException e) {
      log.error(e.getMessage());
      logBuilder.append(WorkLog.ERROR_INFO + e.getMessage() + "\n");
      instance.setStatus(InstanceStatus.FAIL);
      instance.setSubmitLog(logBuilder.toString());
      workInstanceRepository.saveAndFlush(instance);
    }
  }

  public void executeSparkSql(String clusterId, String sqlScript, WorkInstanceEntity instance, StringBuilder logBuilder) throws IOException {

    // 检测计算集群是否存在
    logBuilder.append(WorkLog.SUCCESS_INFO + "开始申请资源 \n");
    if (Strings.isEmpty(clusterId)) {
      throw new WorkRunException(WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在 \n");
    }
    Optional<ClusterEntity> calculateEngineEntityOptional = clusterRepository.findById(clusterId);
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new WorkRunException(WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在  \n");
    }

    // 检测集群中的节点是否合法
    List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository.findAllByClusterIdAndStatus(calculateEngineEntityOptional.get().getId(), EngineNodeStatus.RUNNING);
    if (allEngineNodes.isEmpty()) {
      throw new WorkRunException(WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
    }

    // 节点选择随机数
    ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));
    logBuilder.append(WorkLog.SUCCESS_INFO + "申请资源完成，激活节点:【" + engineNode.getName() + "】\n");
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);

    // 开始构建作业
    logBuilder.append(WorkLog.SUCCESS_INFO + "开始构建作业  \n");
    YagExecuteWorkReq executeReq = new YagExecuteWorkReq();
    executeReq.setAppName("spark-yun");
    executeReq.setMainClass("com.isxcode.star.plugin.query.sql.Execute");
    executeReq.setAppResourcePath(engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME + File.separator + "plugins" + File.separator + "spark-query-sql-plugin.jar");
    executeReq.setSparkHomePath(engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME + File.separator + "spark-min");
    executeReq.setAgentLibPath(engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME + File.separator + "lib");

    // 封装请求spark作业提交
    PluginReq pluginReq = new PluginReq();
    pluginReq.setSql(sqlScript);
    pluginReq.setLimit(200);
    Map<String, String> sparkConfig = new HashMap<>();
    sparkConfig.put("spark.executor.memory", "1g");
    sparkConfig.put("spark.driver.memory", "1g");
    pluginReq.setSparkConfig(sparkConfig);
    executeReq.setPluginReq(pluginReq);

    // 构建作业
    logBuilder.append(WorkLog.SUCCESS_INFO + "构建作业完，SparkConfig: \n");
    sparkConfig.forEach((k, v) -> logBuilder.append(WorkLog.SUCCESS_INFO + k + ":" + v + " \n"));
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);

    // 开始提交作业
    logBuilder.append(WorkLog.SUCCESS_INFO + "开始提交作业  \n");
    String executeWorkUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/executeWork";
    BaseResponse<?> baseResponse = HttpUtils.doPost(executeWorkUrl, executeReq, Map.class);
    if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
      throw new WorkRunException(WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getErr() + "\n");
    }
    // 保存返回值
    instance.setSparkStarRes(JSON.toJSONString(baseResponse.getData()));

    // 保存日志
    logBuilder.append(WorkLog.SUCCESS_INFO + "提交成功  \n");
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);
  }
}
