package com.isxcode.star.backend.module.work.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.api.PathConstants;
import com.isxcode.star.api.constants.cluster.ClusterNodeStatus;
import com.isxcode.star.api.constants.work.WorkLog;
import com.isxcode.star.api.constants.work.instance.InstanceStatus;
import com.isxcode.star.api.exceptions.WorkRunException;
import com.isxcode.star.api.pojos.base.BaseResponse;
import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import com.isxcode.star.api.pojos.work.res.WokRunWorkRes;
import com.isxcode.star.api.pojos.work.dto.WorkRunContext;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class SparkSqlExecutor extends WorkExecutor{

  private final WorkInstanceRepository workInstanceRepository;

  private final ClusterRepository clusterRepository;

  private final ClusterNodeRepository clusterNodeRepository;

  public SparkSqlExecutor(WorkInstanceRepository workInstanceRepository, ClusterRepository clusterRepository, ClusterNodeRepository clusterNodeRepository) {

    super(workInstanceRepository);
    this.workInstanceRepository = workInstanceRepository;
    this.clusterRepository = clusterRepository;
    this.clusterNodeRepository = clusterNodeRepository;
  }

  /**
   * 初始化spark作业提交配置.
   */
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

  public Map<String, String> genSparkConfig(Map<String, String> sparkConfig) {

    // k8s的配置不能提交到作业中
    sparkConfig.remove("spark.kubernetes.driver.podTemplateFile");
    sparkConfig.remove("spark.kubernetes.executor.podTemplateFile");

    return sparkConfig;
  }

  @Override
  protected void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

    StringBuilder logBuilder = workRunContext.getLogBuilder();

    // 检测计算集群是否存在
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始申请资源 \n");
    if (Strings.isEmpty(workRunContext.getClusterId())) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在  \n");
    }

    // 检查计算集群是否存在
    Optional<ClusterEntity> calculateEngineEntityOptional = clusterRepository.findById(workRunContext.getClusterId());
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在  \n");
    }

    // 检测集群中是否有合法节点
    List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository.findAllByClusterIdAndStatus(calculateEngineEntityOptional.get().getId(), ClusterNodeStatus.RUNNING);
    if (allEngineNodes.isEmpty()) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
    }

    // 节点选择随机数
    ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("申请资源完成，激活节点:【").append(engineNode.getName()).append("】\n");
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成  \n");
    workInstance = updateInstance(workInstance, logBuilder);

    // 开始构建作业
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始构建作业  \n");
    YagExecuteWorkReq executeReq = new YagExecuteWorkReq();

    SparkSubmit sparkSubmit = SparkSubmit.builder()
      .verbose(true)
      .mainClass("com.isxcode.star.plugin.query.sql.Execute")
      .appResource("spark-query-sql-plugin.jar")
      .conf(genSparkSubmitConfig(workRunContext.getSparkConfig()))
      .build();

    PluginReq pluginReq = PluginReq.builder()
      .sql(workRunContext.getSqlScript())
      .limit(200)
      .sparkConfig(genSparkConfig(workRunContext.getSparkConfig()))
      .build();

    executeReq.setSparkSubmit(sparkSubmit);
    executeReq.setPluginReq(pluginReq);
    executeReq.setAgentHomePath(engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME);
    executeReq.setAgentType(calculateEngineEntityOptional.get().getClusterType());

    // 构建作业完成
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("构建作业完成 \n");
    workRunContext.getSparkConfig().forEach((k, v) -> logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append(k).append(":").append(v).append(" \n"));
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始提交作业  \n");
    workInstance = updateInstance(workInstance, logBuilder);

    // 开始提交作业
    String executeWorkUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/executeWork";
    BaseResponse<?> baseResponse;
    try {
      baseResponse = HttpUtils.doPost(executeWorkUrl, executeReq, BaseResponse.class);
    } catch (IOException e) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + e.getMessage() + "\n");
    }
    if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
    }

    // 解析返回对象,获取appId
    WokRunWorkRes submitWorkRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokRunWorkRes.class);
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("提交作业成功 : ").append(submitWorkRes.getAppId()).append("\n");
    workInstance = updateInstance(workInstance, logBuilder);

    // 提交作业成功后，开始循环判断状态
    while (true) {

      // 如果中止，则直接退出
      Optional<WorkInstanceEntity> workInstanceEntityOptional = workInstanceRepository.findById(workInstance.getId());
      if (!workInstanceEntityOptional.isPresent()) {
        break;
      }
      if (InstanceStatus.ABORT.equals(workInstanceEntityOptional.get().getStatus())) {
        break;
      }

      // 获取作业状态并保存
      String getStatusUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getStatus?appId=" + submitWorkRes.getAppId() + "&agentType=" + calculateEngineEntityOptional.get().getClusterType();
      baseResponse = HttpUtils.doGet(getStatusUrl, BaseResponse.class);
      if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
        throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业状态异常 : " + baseResponse.getMsg() + "\n");
      }

      // 解析返回状态，并保存
      WokRunWorkRes workStatusRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokRunWorkRes.class);
      workInstance.setSparkStarRes(JSON.toJSONString(workStatusRes));
      logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:").append(workStatusRes.getAppStatus()).append("\n");
      workInstance = updateInstance(workInstance, logBuilder);

      // 如果状态是运行中，更新日志，继续执行
      List<String> runningStatus = Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING");
      if (runningStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "睡眠线程异常 : " + e.getMessage() + "\n");
        }
      }else {

        // 如果运行结束，获取运行日志并保存

        // 获取日志
        String getLogUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getLog?appId=" + submitWorkRes.getAppId() + "&agentType=" + calculateEngineEntityOptional.get().getClusterType();
        baseResponse = HttpUtils.doGet(getLogUrl, BaseResponse.class);
        if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
          throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业日志异常 : " + baseResponse.getMsg() + "\n");
        }

        // 解析日志并保存
        YagGetLogRes yagGetLogRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), YagGetLogRes.class);
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("日志保存成功 \n");
        workInstance.setYarnLog(yagGetLogRes.getLog());
        workInstance = updateInstance(workInstance, logBuilder);

        // 如果运行成功，则保存返回数据
        List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");
        if (successStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {

          // 获取数据
          String getDataUrl = "http://" + engineNode.getHost() + ":" + engineNode.getAgentPort() + "/yag/getData?appId=" + submitWorkRes.getAppId() + "&agentType=" + calculateEngineEntityOptional.get().getClusterType();
          baseResponse = HttpUtils.doGet(getDataUrl, BaseResponse.class);
          if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业数据异常 : " + baseResponse.getErr() + "\n");
          }

          // 解析数据并保存
          workInstance.setResultData(JSON.toJSONString(baseResponse.getData()));
          logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功 \n");
          updateInstance(workInstance, logBuilder);

        }

        // 运行成功，则退出死循环
        break;
      }
    }
  }

}
