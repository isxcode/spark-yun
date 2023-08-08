package com.isxcode.star.modules.work.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.agent.pojos.req.PluginReq;
import com.isxcode.star.api.agent.pojos.req.SparkSubmit;
import com.isxcode.star.api.agent.pojos.req.YagExecuteWorkReq;
import com.isxcode.star.api.agent.pojos.res.YagGetLogRes;
import com.isxcode.star.api.api.constants.PathConstants;
import com.isxcode.star.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.star.api.instance.constants.InstanceStatus;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.pojos.res.WokRunWorkRes;
import com.isxcode.star.backend.api.base.exceptions.SparkYunException;
import com.isxcode.star.backend.api.base.exceptions.WorkRunException;
import com.isxcode.star.backend.api.base.pojos.BaseResponse;
import com.isxcode.star.common.utils.http.HttpUtils;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.work.repository.WorkRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SparkSqlExecutor extends WorkExecutor {

  private final WorkInstanceRepository workInstanceRepository;

  private final ClusterRepository clusterRepository;

  private final ClusterNodeRepository clusterNodeRepository;

  private final WorkRepository workRepository;

  private final WorkConfigRepository workConfigRepository;

  public SparkSqlExecutor(
      WorkInstanceRepository workInstanceRepository,
      ClusterRepository clusterRepository,
      ClusterNodeRepository clusterNodeRepository,
      WorkflowInstanceRepository workflowInstanceRepository,
      WorkRepository workRepository,
      WorkConfigRepository workConfigRepository) {

    super(workInstanceRepository, workflowInstanceRepository);
    this.workInstanceRepository = workInstanceRepository;
    this.clusterRepository = clusterRepository;
    this.clusterNodeRepository = clusterNodeRepository;
    this.workRepository = workRepository;
    this.workConfigRepository = workConfigRepository;
  }

  @Override
  protected void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

    // 获取日志构造器
    StringBuilder logBuilder = workRunContext.getLogBuilder();

    // 检测计算集群是否存在
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始申请资源 \n");
    if (Strings.isEmpty(workRunContext.getClusterId())) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在  \n");
    }

    // 检查计算集群是否存在
    Optional<ClusterEntity> calculateEngineEntityOptional =
        clusterRepository.findById(workRunContext.getClusterId());
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在  \n");
    }

    // 检测集群中是否有合法节点
    List<ClusterNodeEntity> allEngineNodes =
        clusterNodeRepository.findAllByClusterIdAndStatus(
            calculateEngineEntityOptional.get().getId(), ClusterNodeStatus.RUNNING);
    if (allEngineNodes.isEmpty()) {
      throw new WorkRunException(
          LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
    }

    // 节点选择随机数
    ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));
    logBuilder
        .append(LocalDateTime.now())
        .append(WorkLog.SUCCESS_INFO)
        .append("申请资源完成，激活节点:【")
        .append(engineNode.getName())
        .append("】\n");
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成  \n");
    workInstance = updateInstance(workInstance, logBuilder);

    // 开始构建作业
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始构建作业  \n");
    YagExecuteWorkReq executeReq = new YagExecuteWorkReq();

    // 开始构造SparkSubmit
    SparkSubmit sparkSubmit =
        SparkSubmit.builder()
            .verbose(true)
            .mainClass("com.isxcode.star.plugin.query.sql.Execute")
            .appResource("spark-query-sql-plugin.jar")
            .conf(genSparkSubmitConfig(workRunContext.getSparkConfig()))
            .build();

    // 开始构造PluginReq
    PluginReq pluginReq =
        PluginReq.builder()
            .sql(workRunContext.getSqlScript())
            .limit(200)
            .sparkConfig(genSparkConfig(workRunContext.getSparkConfig()))
            .build();

    // 开始构造executeReq
    executeReq.setSparkSubmit(sparkSubmit);
    executeReq.setPluginReq(pluginReq);
    executeReq.setAgentHomePath(
        engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME);
    executeReq.setAgentType(calculateEngineEntityOptional.get().getClusterType());

    // 构建作业完成，并打印作业配置信息
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("构建作业完成 \n");
    workRunContext
        .getSparkConfig()
        .forEach(
            (k, v) ->
                logBuilder
                    .append(LocalDateTime.now())
                    .append(WorkLog.SUCCESS_INFO)
                    .append(k)
                    .append(":")
                    .append(v)
                    .append(" \n"));
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始提交作业  \n");
    workInstance = updateInstance(workInstance, logBuilder);

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
    workInstance.setSparkStarRes(JSON.toJSONString(submitWorkRes));
    workInstance = updateInstance(workInstance, logBuilder);

    // 提交作业成功后，开始循环判断状态
    while (true) {

      // 如果中止，则直接退出
      workInstance = workInstanceRepository.findById(workInstance.getId()).get();
      if (InstanceStatus.ABORT.equals(workInstance.getStatus())) {
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
              + calculateEngineEntityOptional.get().getClusterType();
      baseResponse = HttpUtils.doGet(getStatusUrl, BaseResponse.class);
      if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
        throw new WorkRunException(
            LocalDateTime.now()
                + WorkLog.ERROR_INFO
                + "获取作业状态异常 : "
                + baseResponse.getMsg()
                + "\n");
      }

      // 解析返回状态，并保存
      WokRunWorkRes workStatusRes =
          JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokRunWorkRes.class);
      workInstance.setSparkStarRes(JSON.toJSONString(workStatusRes));
      logBuilder
          .append(LocalDateTime.now())
          .append(WorkLog.SUCCESS_INFO)
          .append("运行状态:")
          .append(workStatusRes.getAppStatus())
          .append("\n");
      workInstance = updateInstance(workInstance, logBuilder);

      // 如果状态是运行中，更新日志，继续执行
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
        // 运行结束逻辑

        // 获取日志并保存
        String getLogUrl =
            "http://"
                + engineNode.getHost()
                + ":"
                + engineNode.getAgentPort()
                + "/yag/getLog?appId="
                + submitWorkRes.getAppId()
                + "&agentType="
                + calculateEngineEntityOptional.get().getClusterType();
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
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("日志保存成功 \n");
        workInstance.setYarnLog(yagGetLogRes.getLog());
        workInstance = updateInstance(workInstance, logBuilder);

        // 如果运行成功，则保存返回数据
        List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");
        if (successStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {

          // 获取数据
          String getDataUrl =
              "http://"
                  + engineNode.getHost()
                  + ":"
                  + engineNode.getAgentPort()
                  + "/yag/getData?appId="
                  + submitWorkRes.getAppId()
                  + "&agentType="
                  + calculateEngineEntityOptional.get().getClusterType();
          baseResponse = HttpUtils.doGet(getDataUrl, BaseResponse.class);
          if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
            throw new WorkRunException(
                LocalDateTime.now()
                    + WorkLog.ERROR_INFO
                    + "获取作业数据异常 : "
                    + baseResponse.getErr()
                    + "\n");
          }

          // 解析数据并保存
          workInstance.setResultData(JSON.toJSONString(baseResponse.getData()));
          logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功 \n");
          updateInstance(workInstance, logBuilder);
        }

        // 运行结束，则退出死循环
        break;
      }
    }
  }

  @Override
  protected void abort(WorkInstanceEntity workInstance) {

    // 判断作业有没有提交成功
    workInstance = workInstanceRepository.findById(workInstance.getId()).get();
    if (!Strings.isEmpty(workInstance.getSparkStarRes())) {
      WokRunWorkRes wokRunWorkRes =
          JSON.parseObject(workInstance.getSparkStarRes(), WokRunWorkRes.class);
      if (!Strings.isEmpty(wokRunWorkRes.getAppId())) {
        // 关闭远程线程

        WorkEntity work = workRepository.findById(workInstance.getWorkId()).get();
        WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();
        List<ClusterNodeEntity> allEngineNodes =
            clusterNodeRepository.findAllByClusterIdAndStatus(
                workConfig.getClusterId(), ClusterNodeStatus.RUNNING);
        if (allEngineNodes.isEmpty()) {
          throw new WorkRunException(
              LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
        }

        ClusterEntity cluster = clusterRepository.findById(workConfig.getClusterId()).get();

        // 节点选择随机数
        ClusterNodeEntity engineNode =
            allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

        String stopJobUrl =
            "http://"
                + engineNode.getHost()
                + ":"
                + engineNode.getAgentPort()
                + "/yag/stopJob?appId="
                + wokRunWorkRes.getAppId()
                + "&agentType="
                + cluster.getClusterType();
        BaseResponse<?> baseResponse = HttpUtils.doGet(stopJobUrl, BaseResponse.class);

        if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
          throw new SparkYunException(
              baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
        }
      }
    } else {
      WORK_THREAD.get(workInstance.getId()).interrupt();
    }
  }

  /** 初始化spark作业提交配置. */
  public Map<String, String> genSparkSubmitConfig(Map<String, String> sparkConfig) {

    // 过滤掉，前缀不包含spark.xxx的配置，spark submit中必须都是spark.xxx
    Map<String, String> sparkSubmitConfig = new HashMap<>();
    sparkConfig.forEach(
        (k, v) -> {
          if (k.startsWith("spark")) {
            sparkSubmitConfig.put(k, v);
          }
        });
    return sparkSubmitConfig;
  }

  /** sparkConfig不能包含k8s的配置 */
  public Map<String, String> genSparkConfig(Map<String, String> sparkConfig) {

    // k8s的配置不能提交到作业中
    sparkConfig.remove("spark.kubernetes.driver.podTemplateFile");
    sparkConfig.remove("spark.kubernetes.executor.podTemplateFile");

    return sparkConfig;
  }
}
