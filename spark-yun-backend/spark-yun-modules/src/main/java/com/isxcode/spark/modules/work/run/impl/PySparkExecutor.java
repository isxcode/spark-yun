// package com.isxcode.spark.modules.work.run.impl;
//
// import com.alibaba.fastjson.JSON;
// import com.isxcode.spark.api.agent.constants.AgentType;
// import com.isxcode.spark.api.agent.constants.SparkAgentUrl;
// import com.isxcode.spark.api.agent.req.spark.*;
// import com.isxcode.spark.api.agent.res.spark.GetWorkStderrLogRes;
// import com.isxcode.spark.api.agent.res.spark.GetWorkStdoutLogRes;
// import com.isxcode.spark.api.api.constants.PathConstants;
// import com.isxcode.spark.api.cluster.constants.ClusterNodeStatus;
// import com.isxcode.spark.api.cluster.constants.ClusterStatus;
//
// import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
// import com.isxcode.spark.api.work.constants.WorkLog;
// import com.isxcode.spark.api.instance.constants.InstanceStatus;
//
// import com.isxcode.spark.api.work.constants.WorkType;
// import com.isxcode.spark.api.work.dto.ClusterConfig;
// import com.isxcode.spark.api.work.res.RunWorkRes;
// import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
// import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
// import com.isxcode.spark.backend.api.base.pojos.BaseResponse;
// import com.isxcode.spark.common.locker.Locker;
// import com.isxcode.spark.common.utils.aes.AesUtils;
// import com.isxcode.spark.common.utils.http.HttpUrlUtils;
// import com.isxcode.spark.common.utils.http.HttpUtils;
// import com.isxcode.spark.modules.alarm.service.AlarmService;
// import com.isxcode.spark.modules.cluster.entity.ClusterEntity;
// import com.isxcode.spark.modules.cluster.entity.ClusterNodeEntity;
// import com.isxcode.spark.modules.cluster.mapper.ClusterNodeMapper;
// import com.isxcode.spark.modules.cluster.repository.ClusterNodeRepository;
// import com.isxcode.spark.modules.cluster.repository.ClusterRepository;
// import com.isxcode.spark.modules.work.entity.WorkConfigEntity;
// import com.isxcode.spark.modules.work.entity.WorkEntity;
// import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
// import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
// import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
// import com.isxcode.spark.modules.work.repository.WorkRepository;
// import com.isxcode.spark.modules.work.run.WorkExecutor;
// import com.isxcode.spark.modules.work.run.WorkRunContext;
// import com.isxcode.spark.modules.work.sql.SqlFunctionService;
// import com.isxcode.spark.modules.work.sql.SqlValueService;
// import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
// import com.isxcode.spark.modules.work.entity.WorkEventEntity;
// import com.isxcode.spark.modules.work.repository.WorkEventRepository;
// import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
// import com.isxcode.spark.modules.work.repository.VipWorkVersionRepository;
// import org.quartz.Scheduler;
//
// import lombok.extern.slf4j.Slf4j;
// import org.apache.logging.log4j.util.Strings;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;
//
// import java.time.LocalDateTime;
// import java.util.*;
// import java.util.regex.Pattern;
//
// import static com.isxcode.spark.common.utils.ssh.SshUtils.scpText;
//
// @Service
// @Slf4j
// public class PySparkExecutor extends WorkExecutor {
//
// private final WorkInstanceRepository workInstanceRepository;
//
// private final ClusterNodeRepository clusterNodeRepository;
//
// private final ClusterNodeMapper clusterNodeMapper;
//
// private final AesUtils aesUtils;
//
// private final ClusterRepository clusterRepository;
//
// private final SqlValueService sqlValueService;
//
// private final Locker locker;
//
// private final HttpUrlUtils httpUrlUtils;
//
// private final SqlFunctionService sqlFunctionService;
//
// private final WorkRepository workRepository;
//
// private final WorkConfigRepository workConfigRepository;
// private final WorkEventRepository workEventRepository;
//
//
// public PySparkExecutor(WorkInstanceRepository workInstanceRepository,
// WorkflowInstanceRepository workflowInstanceRepository, ClusterNodeRepository
// clusterNodeRepository,
// ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, ClusterRepository clusterRepository,
// SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService
// alarmService,
// WorkEventRepository workEventRepository, Scheduler scheduler, Locker locker, WorkRepository
// workRepository,
// WorkRunJobFactory workRunJobFactory, WorkConfigRepository workConfigRepository,
// VipWorkVersionRepository vipWorkVersionRepository, HttpUrlUtils httpUrlUtils) {
//
// super(alarmService, scheduler, locker, workRepository, workInstanceRepository,
// workflowInstanceRepository,
// workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository,
// vipWorkVersionRepository);
// this.workInstanceRepository = workInstanceRepository;
// this.clusterNodeRepository = clusterNodeRepository;
// this.clusterNodeMapper = clusterNodeMapper;
// this.aesUtils = aesUtils;
// this.clusterRepository = clusterRepository;
// this.sqlValueService = sqlValueService;
// this.sqlFunctionService = sqlFunctionService;
// this.locker = locker;
// this.httpUrlUtils = httpUrlUtils;
// this.workRepository = workRepository;
// this.workConfigRepository = workConfigRepository;
// this.workEventRepository = workEventRepository;
// }
//
// @Override
// public String getWorkType() {
// return WorkType.PY_SPARK;
// }
//
// protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
// WorkEventEntity workEvent)
// throws Exception {
//
// // 线程登记
// WORK_THREAD.put(workInstance.getId(), Thread.currentThread());
//
// // 获取事件上下文与日志
// WorkRunContext workEventBody = JSON.parseObject(workEvent.getEventContext(),
// WorkRunContext.class);
// StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());
//
// // 步骤1：校验并保存脚本
// if (processNeverRun(workEvent, 1)) {
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测脚本内容 \n");
// if (Strings.isEmpty(workRunContext.getScript())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测脚本失败 : PYTHON内容为空不能执行
// \n");
// }
// if (Pattern.compile("\\brm\\b",
// Pattern.CASE_INSENSITIVE).matcher(workRunContext.getScript()).find()) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测语句失败 :
// PYTHON内容包含rm指令不能执行 \n");
// }
// String script = sqlFunctionService.parseSqlFunction(
// sqlValueService.parseSqlValue(parseJsonPath(workRunContext.getScript(), workInstance)));
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("Python脚本:\n").append(script)
// .append("\n");
// workInstance = updateInstance(workInstance, logBuilder);
// workEventBody.setScript(script);
// workEvent.setEventContext(JSON.toJSONString(workEventBody));
// workEventRepository.saveAndFlush(workEvent);
// }
//
// // 步骤2：选择节点、上传脚本并保存运行环境
// if (processNeverRun(workEvent, 2)) {
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测集群 \n");
// if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 计算引擎未配置 \n");
// }
// Optional<ClusterEntity> clusterOpt =
// clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
// if (!clusterOpt.isPresent()) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在 \n");
// }
// if (ClusterStatus.NO_ACTIVE.equals(clusterOpt.get().getStatus())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 计算引擎不可用 \n");
// }
// if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterNodeId())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 指定运行节点未配置 \n");
// }
// Optional<ClusterNodeEntity> nodeOptional =
// clusterNodeRepository.findById(workRunContext.getClusterConfig().getClusterNodeId());
// if (!nodeOptional.isPresent()) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 指定运行节点不存在 \n");
// }
// ClusterNodeEntity engineNode = nodeOptional.get();
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("申请资源完成，激活节点:【")
// .append(engineNode.getName()).append("】\n");
// workInstance = updateInstance(workInstance, logBuilder);
//
// ScpFileEngineNodeDto scp = clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
// scp.setPasswd(aesUtils.decrypt(scp.getPasswd()));
// scpText(scp, workEventBody.getScript(),
// engineNode.getAgentHomePath() + "/zhiqingyun-agent/works/" + workInstance.getId() + ".py");
//
// workEventBody.setAgentHomePath(engineNode.getAgentHomePath());
// workEvent.setEventContext(JSON.toJSONString(workEventBody));
// workEventRepository.saveAndFlush(workEvent);
// }
//
// // 步骤3：提交作业并保存appId
// if (processNeverRun(workEvent, 3)) {
// ClusterNodeEntity engineNode =
// clusterNodeRepository.findById(workRunContext.getClusterConfig().getClusterNodeId()).get();
//
// SparkSubmit sparkSubmit = SparkSubmit.builder().verbose(true).mainClass("")
// .appResource(
// workEventBody.getAgentHomePath() + "/zhiqingyun-agent/works/" + workInstance.getId() + ".py")
// .conf(genSparkSubmitConfig(workRunContext.getClusterConfig().getSparkConfig())).build();
// PluginReq pluginReq =
// PluginReq.builder().sparkConfig(workRunContext.getClusterConfig().getSparkConfig()).build();
//
// SubmitWorkReq executeReq = new SubmitWorkReq();
// executeReq.setWorkId(workRunContext.getWorkId());
// executeReq.setWorkType(WorkType.PY_SPARK);
// executeReq.setWorkInstanceId(workInstance.getId());
// executeReq.setSparkSubmit(sparkSubmit);
// executeReq.setPluginReq(pluginReq);
// executeReq.setAgentHomePath(workEventBody.getAgentHomePath() + "/" +
// PathConstants.AGENT_PATH_NAME);
// executeReq.setSparkHomePath(engineNode.getSparkHomePath());
// ClusterEntity cluster =
// clusterRepository.findById(workRunContext.getClusterConfig().getClusterId()).get();
// executeReq.setClusterType(cluster.getClusterType());
//
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始构建作业 \n");
// workInstance = updateInstance(workInstance, logBuilder);
//
// Integer lock = locker.lock("REQUEST_" + workInstance.getId());
// try {
// BaseResponse<?> baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(),
// engineNode.getAgentPort(), SparkAgentUrl.SUBMIT_WORK_URL), executeReq, BaseResponse.class);
// if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())
// || baseResponse.getData() == null) {
// throw new WorkRunException(
// LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
// }
// RunWorkRes submitWorkRes =
// JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("提交作业成功 : ")
// .append(submitWorkRes.getAppId()).append("\n");
// workInstance.setSparkStarRes(JSON.toJSONString(submitWorkRes));
// workInstance = updateInstance(workInstance, logBuilder);
//
// workEventBody.setContainerId(submitWorkRes.getAppId());
// workEvent.setEventContext(JSON.toJSONString(workEventBody));
// workEventRepository.saveAndFlush(workEvent);
// } finally {
// locker.unlock(lock);
// }
// }
//
// // 步骤4：查询状态（一次）并决定是否继续运行
// if (processNeverRun(workEvent, 4)) {
// ClusterNodeEntity engineNode =
// clusterNodeRepository.findById(workRunContext.getClusterConfig().getClusterNodeId()).get();
// ClusterEntity cluster =
// clusterRepository.findById(workRunContext.getClusterConfig().getClusterId()).get();
// GetWorkStatusReq getWorkStatusReq =
// GetWorkStatusReq.builder().appId(workEventBody.getContainerId())
// .clusterType(cluster.getClusterType()).sparkHomePath(engineNode.getSparkHomePath()).build();
// BaseResponse<?> baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(),
// engineNode.getAgentPort(), SparkAgentUrl.GET_WORK_STATUS_URL), getWorkStatusReq,
// BaseResponse.class);
// if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
// throw new WorkRunException(
// LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业状态异常 : " + baseResponse.getMsg() + "\n");
// }
// RunWorkRes workStatusRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()),
// RunWorkRes.class);
// workInstance.setSparkStarRes(JSON.toJSONString(workStatusRes));
// if (!Objects.equals(workEventBody.getCurrentStatus(), workStatusRes.getAppStatus())) {
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:")
// .append(workStatusRes.getAppStatus()).append("\n");
// workInstance = updateInstance(workInstance, logBuilder);
// }
// workEventBody.setCurrentStatus(workStatusRes.getAppStatus());
// workEvent.setEventContext(JSON.toJSONString(workEventBody));
// workEventRepository.saveAndFlush(workEvent);
//
// List<String> runningStatus =
// Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING", "PENDING");
// if (runningStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {
// return InstanceStatus.RUNNING;
// }
// }
//
// // 步骤5：获取日志/数据并判断成功失败
// if (processNeverRun(workEvent, 5)) {
// ClusterNodeEntity engineNode =
// clusterNodeRepository.findById(workRunContext.getClusterConfig().getClusterNodeId()).get();
// ClusterEntity cluster =
// clusterRepository.findById(workRunContext.getClusterConfig().getClusterId()).get();
// GetWorkStderrLogReq getWorkStderrLogReq =
// GetWorkStderrLogReq.builder().appId(workEventBody.getContainerId())
// .clusterType(cluster.getClusterType()).sparkHomePath(engineNode.getSparkHomePath()).build();
// BaseResponse<?> logRes =
// HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
// SparkAgentUrl.GET_WORK_STDERR_LOG_URL), getWorkStderrLogReq, BaseResponse.class);
// if (!String.valueOf(HttpStatus.OK.value()).equals(logRes.getCode())) {
// throw new WorkRunException(
// LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业日志异常 : " + logRes.getMsg() + "\n");
// }
// GetWorkStderrLogRes stderr =
// JSON.parseObject(JSON.toJSONString(logRes.getData()), GetWorkStderrLogRes.class);
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("日志保存成功 \n");
// if (stderr != null) {
// workInstance.setYarnLog(stderr.getLog());
// }
// updateInstance(workInstance, logBuilder);
//
// if (AgentType.K8S.equals(cluster.getClusterType())) {
// StopWorkReq stopWorkReq = StopWorkReq.builder().appId(workEventBody.getContainerId())
// .clusterType(AgentType.K8S).sparkHomePath(engineNode.getSparkHomePath())
// .agentHomePath(engineNode.getAgentHomePath()).build();
// HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
// SparkAgentUrl.STOP_WORK_URL), stopWorkReq, BaseResponse.class);
// }
//
// List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");
// if (workEventBody.getCurrentStatus() == null
// || !successStatus.contains(workEventBody.getCurrentStatus().toUpperCase())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常\n");
// }
//
// GetWorkStdoutLogReq getWorkStdoutLogReq =
// GetWorkStdoutLogReq.builder().appId(workEventBody.getContainerId())
// .clusterType(cluster.getClusterType()).sparkHomePath(engineNode.getSparkHomePath()).build();
// BaseResponse<?> outRes =
// HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
// SparkAgentUrl.GET_ALL_WORK_STDOUT_LOG_URL), getWorkStdoutLogReq, BaseResponse.class);
// if (String.valueOf(HttpStatus.OK.value()).equals(outRes.getCode())) {
// GetWorkStdoutLogRes out =
// JSON.parseObject(JSON.toJSONString(outRes.getData()), GetWorkStdoutLogRes.class);
// workInstance.setResultData(out.getLog());
// } else {
// workInstance.setResultData(Strings.isEmpty(outRes.getMsg()) ? "请查看运行日志" : outRes.getMsg());
// }
// updateInstance(workInstance, logBuilder);
// return InstanceStatus.SUCCESS;
// }
//
// return InstanceStatus.SUCCESS;
// }
//
// @Override
// protected void abort(WorkInstanceEntity workInstance) {
//
// // 判断作业有没有提交成功
// locker.lock("REQUEST_" + workInstance.getId());
// try {
// workInstance = workInstanceRepository.findById(workInstance.getId()).get();
// if (!Strings.isEmpty(workInstance.getSparkStarRes())) {
// RunWorkRes wokRunWorkRes = JSON.parseObject(workInstance.getSparkStarRes(), RunWorkRes.class);
// if (!Strings.isEmpty(wokRunWorkRes.getAppId())) {
// // 关闭远程线程
// WorkEntity work = workRepository.findById(workInstance.getWorkId()).get();
// WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();
// ClusterConfig clusterConfig = JSON.parseObject(workConfig.getClusterConfig(),
// ClusterConfig.class);
// List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
// .findAllByClusterIdAndStatus(clusterConfig.getClusterId(), ClusterNodeStatus.RUNNING);
// if (allEngineNodes.isEmpty()) {
// throw new WorkRunException(
// LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群 \n");
// }
// ClusterEntity cluster = clusterRepository.findById(clusterConfig.getClusterId()).get();
//
// // 节点选择随机数
// ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));
// StopWorkReq stopWorkReq = StopWorkReq.builder().appId(wokRunWorkRes.getAppId())
// .clusterType(cluster.getClusterType()).sparkHomePath(engineNode.getSparkHomePath())
// .agentHomePath(engineNode.getAgentHomePath()).build();
// BaseResponse<?> baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(),
// engineNode.getAgentPort(), SparkAgentUrl.STOP_WORK_URL), stopWorkReq, BaseResponse.class);
//
// if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
// throw new IsxAppException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
// }
// } else {
// // 先杀死进程
// WORK_THREAD.get(workInstance.getId()).interrupt();
// }
// }
// } finally {
// locker.clearLock("REQUEST_" + workInstance.getId());
// }
// }
//
// public Map<String, String> genSparkSubmitConfig(Map<String, String> sparkConfig) {
//
// // 过滤掉，前缀不包含spark.xxx的配置，spark submit中必须都是spark.xxx
// Map<String, String> sparkSubmitConfig = new HashMap<>();
// sparkConfig.forEach((k, v) -> {
// if (k.startsWith("spark")) {
// sparkSubmitConfig.put(k, v);
// }
// });
// return sparkSubmitConfig;
// }
// }
