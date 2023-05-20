package com.isxcode.star.backend.module.work.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.CodeConstants;
import com.isxcode.star.api.constants.EngineNodeStatus;
import com.isxcode.star.api.constants.PathConstants;
import com.isxcode.star.api.constants.work.WorkLog;
import com.isxcode.star.api.constants.work.instance.InstanceStatus;
import com.isxcode.star.api.exception.SparkYunException;
import com.isxcode.star.api.exception.WorkRunException;
import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import com.isxcode.star.api.pojos.work.res.WokGetDataRes;
import com.isxcode.star.api.pojos.work.res.WokGetWorkLogRes;
import com.isxcode.star.api.pojos.work.res.WokRunWorkRes;
import com.isxcode.star.api.pojos.yun.agent.req.YagExecuteWorkReq;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetLogRes;
import com.isxcode.star.api.response.BaseResponse;
import com.isxcode.star.api.utils.HttpUtils;
import com.isxcode.star.backend.module.cluster.entity.ClusterEntity;
import com.isxcode.star.backend.module.cluster.node.entity.ClusterNodeEntity;
import com.isxcode.star.backend.module.cluster.node.repository.ClusterNodeRepository;
import com.isxcode.star.backend.module.cluster.repository.ClusterRepository;
import com.isxcode.star.backend.module.work.instance.entity.WorkInstanceEntity;
import com.isxcode.star.backend.module.work.instance.repository.WorkInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

  private final WorkInstanceRepository workInstanceRepository;

  private final ClusterRepository clusterRepository;

  private final ClusterNodeRepository clusterNodeRepository;

  /**
   * @param clusterId  集群id
   * @param sqlScript  sparkSql脚本
   * @param instanceId 实例id
   * @param tenantId   租户id
   * @param userId     用户id
   */
  @Async("sparkYunWorkThreadPool")
  public void run(String clusterId, String sqlScript, String instanceId, String tenantId, String userId) {

    // 异步环境变量
    USER_ID.set(userId);
    TENANT_ID.set(tenantId);

    // 判断实例id是否为空
    Optional<WorkInstanceEntity> instanceEntityOptional = workInstanceRepository.findById(instanceId);
    if (!instanceEntityOptional.isPresent()) {
      return;
    }

    // 记录首次执行时间
    WorkInstanceEntity instance = instanceEntityOptional.get();
    StringBuilder logBuilder = new StringBuilder();
    logBuilder.append(WorkLog.SUCCESS_INFO + "开始执行作业 \n");
    instance.setExecStartDateTime(new Date());
    instance.setStatus(InstanceStatus.RUNNING);
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);

    try {

      // 执行作业
      executeSparkSql(clusterId, sqlScript, instance, logBuilder);

      // 记录执行结束时间
      instance.setExecEndDateTime(new Date());
      workInstanceRepository.saveAndFlush(instance);
    } catch (WorkRunException e) {
      log.error(e.getMsg());

      // 记录执行失败
      logBuilder.append(e.getMsg());
      logBuilder.append(WorkLog.ERROR_INFO + "运行失败 \n");
      instance.setSubmitLog(logBuilder.toString());
      instance.setStatus(InstanceStatus.FAIL);
      workInstanceRepository.saveAndFlush(instance);
    }
  }

  public void executeSparkSql(String clusterId, String sqlScript, WorkInstanceEntity instance, StringBuilder logBuilder) {

    // 检测计算集群是否存在
    logBuilder.append(WorkLog.SUCCESS_INFO + "开始申请资源 \n");
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

    // 构建作业完成
    logBuilder.append(WorkLog.SUCCESS_INFO + "构建作业完成 \n");
    sparkConfig.forEach((k, v) -> logBuilder.append(WorkLog.SUCCESS_INFO + k + ":" + v + " \n"));
    logBuilder.append(WorkLog.SUCCESS_INFO + "开始提交作业  \n");
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);

    // 开始提交作业
    String executeWorkUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/executeWork";
    BaseResponse<?> baseResponse;
    try {
      baseResponse = HttpUtils.doPost(executeWorkUrl, executeReq, BaseResponse.class);
    } catch (IOException e) {
      throw new WorkRunException(WorkLog.ERROR_INFO + "提交作业失败 : " + e.getMessage() + "\n");
    }
    if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
      throw new WorkRunException(WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getErr() + "\n");
    }
    WokRunWorkRes submitWorkRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokRunWorkRes.class);
    logBuilder.append(WorkLog.SUCCESS_INFO + "提交作业成功  \n");
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);

    // 提交作业成功后开始循环判断状态
    while (true) {

      // 获取作业状态并保存
      String getStatusUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getStatus?applicationId=" + submitWorkRes.getApplicationId();
      baseResponse = HttpUtils.doGet(getStatusUrl, BaseResponse.class);
      if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
        throw new WorkRunException(WorkLog.ERROR_INFO + "获取作业状态异常 : " + baseResponse.getErr() + "\n");
      }
      WokRunWorkRes workStatusRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokRunWorkRes.class);
      instance.setSparkStarRes(JSON.toJSONString(workStatusRes));
      workInstanceRepository.saveAndFlush(instance);

      // 如果是运行中，更新日志，继续执行
      if ("RUNNING".equals(workStatusRes.getYarnApplicationState()) || "UNDEFINED".equals(workStatusRes.getFinalApplicationStatus())) {
        logBuilder.append(WorkLog.SUCCESS_INFO + "运行中:" + workStatusRes.getYarnApplicationState() + "\n");
        instance.setSubmitLog(logBuilder.toString());
        workInstanceRepository.saveAndFlush(instance);
      } else {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          throw new WorkRunException(WorkLog.ERROR_INFO + "睡眠线程异常 : " + e.getMessage() + "\n");
        }
        // 保存日志，生成日志时间比较久
        String getLogUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getLog?applicationId=" + submitWorkRes.getApplicationId();
        baseResponse = HttpUtils.doGet(getLogUrl, BaseResponse.class);
        if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
          throw new WorkRunException(WorkLog.ERROR_INFO + "获取作业日志异常 : " + baseResponse.getMsg() + "\n");
        }
        YagGetLogRes yagGetLogRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), YagGetLogRes.class);
        logBuilder.append(WorkLog.SUCCESS_INFO + "保存日志完成 \n");
        instance.setSubmitLog(logBuilder.toString());
        instance.setYarnLog(yagGetLogRes.getLog());
        workInstanceRepository.saveAndFlush(instance);

        // 运行成功，保存数据
        if ("SUCCEEDED".equals(workStatusRes.getFinalApplicationStatus())) {
          String getDataUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getData?applicationId=" + submitWorkRes.getApplicationId();
          baseResponse = HttpUtils.doGet(getDataUrl, BaseResponse.class);
          if (!CodeConstants.SUCCESS_CODE.equals(baseResponse.getCode())) {
            throw new WorkRunException(WorkLog.ERROR_INFO + "获取作业数据异常 : " + baseResponse.getErr() + "\n");
          }
          instance.setResultData(JSON.toJSONString(baseResponse.getData()));
          instance.setStatus(InstanceStatus.SUCCESS);
          logBuilder.append(WorkLog.SUCCESS_INFO + "作业执行成功 \n");
          instance.setSubmitLog(logBuilder.toString());
          workInstanceRepository.saveAndFlush(instance);
        } else {
          throw new WorkRunException(WorkLog.ERROR_INFO + "执行失败 \n");
        }
        break;
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new WorkRunException(WorkLog.ERROR_INFO + "睡眠线程异常 : " + e.getMessage() + "\n");
      }
    }
  }
}
