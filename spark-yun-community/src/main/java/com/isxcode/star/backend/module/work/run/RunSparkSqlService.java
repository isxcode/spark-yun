package com.isxcode.star.backend.module.work.run;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.api.PathConstants;
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
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
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
    if (Strings.isEmpty(clusterId)) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在  \n");
    }
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

    // 过滤掉 前缀不包含spark.xxx的配置
    Map<String, String> sparkSubmitConfig = new HashMap<>();
    sparkConfig.forEach(
        (k, v) -> {
          if (k.startsWith("spark")) {
            sparkSubmitConfig.put(k, v);
          }
        });

    SparkSubmit sparkSubmit =
        SparkSubmit.builder()
            .verbose(true)
            .mainClass("com.isxcode.star.plugin.query.sql.Execute")
            .appResource("spark-query-sql-plugin.jar")
            .conf(sparkSubmitConfig)
            .build();

    PluginReq pluginReq =
        PluginReq.builder().sql(sqlScript).limit(200).sparkConfig(sparkConfig).build();

    String clusterType = calculateEngineEntityOptional.get().getClusterType();
    executeReq.setSparkSubmit(sparkSubmit);
    executeReq.setPluginReq(pluginReq);
    executeReq.setAgentHomePath(
        engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME);
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

    // 解析返回对象,获取appId
    WokRunWorkRes submitWorkRes =
        JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokRunWorkRes.class);
    logBuilder
        .append(LocalDateTime.now())
        .append(WorkLog.SUCCESS_INFO)
        .append("提交作业成功 : ")
        .append(submitWorkRes.getAppId())
        .append("\n");
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);

    // 提交作业成功后开始循环判断状态
    while (true) {

      // 如果中止，则直接退出
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

      // 解析返回对象
      WokRunWorkRes workStatusRes =
          JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokRunWorkRes.class);
      instance.setSparkStarRes(JSON.toJSONString(workStatusRes));
      workInstanceRepository.saveAndFlush(instance);

      // 打印当前作业执行状态
      logBuilder
          .append(LocalDateTime.now())
          .append(WorkLog.SUCCESS_INFO)
          .append("运行状态:")
          .append(workStatusRes.getAppStatus())
          .append("\n");
      instance.setSubmitLog(logBuilder.toString());
      workInstanceRepository.saveAndFlush(instance);

      // 如果是运行中，更新日志，继续执行 ContainerCreating
      List<String> runningStatus =
          Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING");
      if (runningStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new WorkRunException(
              LocalDateTime.now() + WorkLog.ERROR_INFO + "睡眠线程异常 : " + e.getMessage() + "\n");
        }
      } else {
        // 如果运行结束，获取运行日志并保存
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

        // 解析日志并保存
        YagGetLogRes yagGetLogRes =
            JSON.parseObject(JSON.toJSONString(baseResponse.getData()), YagGetLogRes.class);
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("保存日志完成 \n");
        instance.setSubmitLog(logBuilder.toString());
        instance.setYarnLog(yagGetLogRes.getLog());
        workInstanceRepository.saveAndFlush(instance);

        // 如果运行成功，则保存返回数据
        List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");

        if (successStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {
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
          // 如果不成功，直接返回失败
          throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业远程执行失败 \n");
        }
        break;
      }
    }
  }
}
