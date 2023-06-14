package com.isxcode.star.backend.module.work.run;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.cluster.ClusterNodeStatus;
import com.isxcode.star.api.constants.work.WorkLog;
import com.isxcode.star.api.constants.work.instance.InstanceStatus;
import com.isxcode.star.api.exceptions.WorkRunException;
import com.isxcode.star.api.pojos.base.BaseResponse;
import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import com.isxcode.star.api.pojos.work.res.WokRunWorkRes;
import com.isxcode.star.api.pojos.yun.agent.req.SparkSubmit;
import com.isxcode.star.api.pojos.yun.agent.req.YagExecuteWorkReq;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetLogRes;
import com.isxcode.star.backend.module.cluster.ClusterEntity;
import com.isxcode.star.backend.module.cluster.ClusterRepository;
import com.isxcode.star.backend.module.cluster.node.ClusterNodeEntity;
import com.isxcode.star.backend.module.cluster.node.ClusterNodeRepository;
import com.isxcode.star.backend.module.work.instance.WorkInstanceEntity;
import com.isxcode.star.backend.module.work.instance.WorkInstanceRepository;
import com.isxcode.star.common.utils.HttpUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RunSparkSqlService {

  private final WorkInstanceRepository workInstanceRepository;

  private final ClusterRepository clusterRepository;

  private final ClusterNodeRepository clusterNodeRepository;

  /**
   * @param clusterId 集群id
   * @param sqlScript sparkSql脚本
   * @param instanceId 实例id
   * @param tenantId 租户id
   * @param userId 用户id
   */
  @Async("sparkYunWorkThreadPool")
  public void run(
      String clusterId,
      String sqlScript,
      Map<String, String> sparkConfig,
      String instanceId,
      String tenantId,
      String userId) {

    // 异步环境变量
    USER_ID.set(userId);
    TENANT_ID.set(tenantId);

    // 判断实例id是否为空
    Optional<WorkInstanceEntity> instanceEntityOptional =
        workInstanceRepository.findById(instanceId);
    if (!instanceEntityOptional.isPresent()) {
      return;
    }

    // 记录首次执行时间
    WorkInstanceEntity instance = instanceEntityOptional.get();
    StringBuilder logBuilder = new StringBuilder();
    logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始执行作业 \n");
    instance.setExecStartDateTime(new Date());
    instance.setStatus(InstanceStatus.RUNNING);
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);

    try {

      // 执行作业
      executeSparkSql(clusterId, sqlScript, sparkConfig, instance, logBuilder);

      // 记录执行结束时间
      WorkInstanceEntity workInstanceEntity =
          workInstanceRepository.findById(instance.getId()).get();
      workInstanceEntity.setExecEndDateTime(new Date());
      workInstanceRepository.saveAndFlush(workInstanceEntity);
    } catch (WorkRunException e) {
      log.error(e.getMsg());

      // 记录执行失败
      logBuilder.append(e.getMsg());
      logBuilder.append(LocalDateTime.now() + WorkLog.ERROR_INFO + "运行失败 \n");
      instance.setSubmitLog(logBuilder.toString());
      instance.setStatus(InstanceStatus.FAIL);
      instance.setExecEndDateTime(new Date());
      workInstanceRepository.saveAndFlush(instance);
    }
  }

  public void executeSparkSql(
      String clusterId,
      String sqlScript,
      Map<String, String> sparkConfig,
      WorkInstanceEntity instance,
      StringBuilder logBuilder) {

    // 检测计算集群是否存在
    logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始申请资源 \n");
    Optional<ClusterEntity> calculateEngineEntityOptional = clusterRepository.findById(clusterId);
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在  \n");
    }

    // 检测集群中的节点是否合法
    List<ClusterNodeEntity> allEngineNodes =
        clusterNodeRepository.findAllByClusterIdAndStatus(
            calculateEngineEntityOptional.get().getId(), ClusterNodeStatus.RUNNING);
    if (allEngineNodes.isEmpty()) {
      throw new WorkRunException(
          LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
    }

    // 节点选择随机数
    ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));
    logBuilder.append(
        LocalDateTime.now()
            + WorkLog.SUCCESS_INFO
            + "申请资源完成，激活节点:【"
            + engineNode.getName()
            + "】\n");
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);

    // 开始构建作业
    logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始构建作业  \n");
    YagExecuteWorkReq executeReq = new YagExecuteWorkReq();

    SparkSubmit sparkSubmit =
        SparkSubmit.builder()
            .verbose(true)
            .mainClass("com.isxcode.star.plugin.query.sql.Execute")
            .appResource("spark-query-sql-plugin.jar")
            .conf(sparkConfig)
            .build();

    PluginReq pluginReq =
        PluginReq.builder().sql(sqlScript).limit(200).sparkConfig(sparkConfig).build();

    String clusterType = calculateEngineEntityOptional.get().getClusterType();
    executeReq.setSparkSubmit(sparkSubmit);
    executeReq.setPluginReq(pluginReq);
    executeReq.setAgentHomePath(engineNode.getAgentHomePath());
    executeReq.setAgentType(clusterType);

    // 构建作业完成
    logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "构建作业完成 \n");
    sparkConfig.forEach(
        (k, v) ->
            logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + k + ":" + v + " \n"));
    logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始提交作业  \n");
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);

    // 开始提交作业
    String executeWorkUrl =
        "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/executeWork";
    BaseResponse<?> baseResponse;
    try {
      baseResponse = HttpUtils.doPost(executeWorkUrl, executeReq, BaseResponse.class);
    } catch (IOException e) {
      throw new WorkRunException(
          LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + e.getMessage() + "\n");
    }
    if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
      throw new WorkRunException(
          LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
    }
    WokRunWorkRes submitWorkRes =
        JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokRunWorkRes.class);
    logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "提交作业成功  \n");
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);

    // 提交作业成功后开始循环判断状态
    while (true) {

      WorkInstanceEntity workInstanceEntity =
          workInstanceRepository.findById(instance.getId()).get();
      if (InstanceStatus.ABORT.equals(workInstanceEntity.getStatus())) {
        break;
      }

      // 获取作业状态并保存
      String getStatusUrl =
          "http://"
              + engineNode.getHost()
              + ":"
              + engineNode.getAgentPort()
              + "/yag/getStatus?appId="
              + submitWorkRes.getAppId()
              + "&agentType="
              + clusterType;
      baseResponse = HttpUtils.doGet(getStatusUrl, BaseResponse.class);
      if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
        throw new WorkRunException(
            LocalDateTime.now()
                + WorkLog.ERROR_INFO
                + "获取作业状态异常 : "
                + baseResponse.getErr()
                + "\n");
      }
      WokRunWorkRes workStatusRes =
          JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokRunWorkRes.class);
      instance.setSparkStarRes(JSON.toJSONString(workStatusRes));
      workInstanceRepository.saveAndFlush(instance);

      List<String> runningStatus = Arrays.asList("RUNNING", "UNDEFINED");

      // 如果是运行中，更新日志，继续执行
      if (runningStatus.contains(workStatusRes.getAppStatus())) {
        logBuilder.append(
          LocalDateTime.now()
            + WorkLog.SUCCESS_INFO
            + "运行中:"
            + workStatusRes.getAppStatus()
            + "\n");
        instance.setSubmitLog(logBuilder.toString());
        workInstanceRepository.saveAndFlush(instance);
      } else {

        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          throw new WorkRunException(
            LocalDateTime.now() + WorkLog.ERROR_INFO + "睡眠线程异常 : " + e.getMessage() + "\n");
        }

        if (!"KILLED".equals(workStatusRes.getAppStatus())) {
          // 保存日志，生成日志时间比较久
          String getLogUrl =
            "http://"
              + engineNode.getHost()
              + ":"
              + engineNode.getAgentPort()
              + "/yag/getLog?appId="
              + submitWorkRes.getAppId()
              + "&agentType="
              + clusterType;
          baseResponse = HttpUtils.doGet(getLogUrl, BaseResponse.class);
          if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
            throw new WorkRunException(
              LocalDateTime.now()
                + WorkLog.ERROR_INFO
                + "获取作业日志异常 : "
                + baseResponse.getMsg()
                + "\n");
          }
          YagGetLogRes yagGetLogRes =
            JSON.parseObject(JSON.toJSONString(baseResponse.getData()), YagGetLogRes.class);
          logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "保存日志完成 \n");
          instance.setSubmitLog(logBuilder.toString());
          instance.setYarnLog(yagGetLogRes.getLog());
          workInstanceRepository.saveAndFlush(instance);
        }

        // 运行成功，保存数据
        if ("SUCCEEDED".equals(workStatusRes.getAppStatus())) {
          String getDataUrl =
            "http://"
              + engineNode.getHost()
              + ":"
              + engineNode.getAgentPort()
              + "/yag/getData?appId="
              + submitWorkRes.getAppId()
              + "&agentType="
              + clusterType;
          baseResponse = HttpUtils.doGet(getDataUrl, BaseResponse.class);
          if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
            throw new WorkRunException(
              LocalDateTime.now()
                + WorkLog.ERROR_INFO
                + "获取作业数据异常 : "
                + baseResponse.getErr()
                + "\n");
          }
          instance.setResultData(JSON.toJSONString(baseResponse.getData()));
          instance.setStatus(InstanceStatus.SUCCESS);
          logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("作业执行成功 \n");
          instance.setSubmitLog(logBuilder.toString());
          workInstanceRepository.saveAndFlush(instance);
        } else {
          throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "执行失败 \n");
        }
        break;
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new WorkRunException(
            LocalDateTime.now() + WorkLog.ERROR_INFO + "睡眠线程异常 : " + e.getMessage() + "\n");
      }
    }
  }
}
