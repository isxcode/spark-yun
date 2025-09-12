// package com.isxcode.spark.modules.work.run.impl;
//
// import com.alibaba.fastjson2.JSON;
// import com.isxcode.spark.api.cluster.constants.ClusterStatus;
// import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
// import com.isxcode.spark.api.instance.constants.InstanceStatus;
// import com.isxcode.spark.api.work.constants.WorkLog;
// import com.isxcode.spark.api.work.constants.WorkType;
// import com.isxcode.spark.api.work.dto.ClusterConfig;
// import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
// import com.isxcode.spark.common.locker.Locker;
// import com.isxcode.spark.common.utils.aes.AesUtils;
// import com.isxcode.spark.common.utils.ssh.SshUtils;
// import com.isxcode.spark.modules.alarm.service.AlarmService;
// import com.isxcode.spark.modules.cluster.entity.ClusterEntity;
// import com.isxcode.spark.modules.cluster.entity.ClusterNodeEntity;
// import com.isxcode.spark.modules.cluster.mapper.ClusterNodeMapper;
// import com.isxcode.spark.modules.cluster.repository.ClusterNodeRepository;
// import com.isxcode.spark.modules.cluster.repository.ClusterRepository;
// import com.isxcode.spark.modules.work.entity.*;
// import com.isxcode.spark.modules.work.repository.*;
// import com.isxcode.spark.modules.work.run.WorkExecutor;
// import com.isxcode.spark.modules.work.run.WorkRunContext;
// import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
// import com.isxcode.spark.modules.work.sql.SqlFunctionService;
// import com.isxcode.spark.modules.work.sql.SqlValueService;
// import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
// import com.jcraft.jsch.JSchException;
// import com.jcraft.jsch.SftpException;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.logging.log4j.util.Strings;
// import org.quartz.Scheduler;
// import org.springframework.stereotype.Service;
//
// import java.io.IOException;
// import java.time.LocalDateTime;
// import java.util.*;
// import java.util.regex.Pattern;
//
// import static com.isxcode.spark.common.utils.ssh.SshUtils.*;
//
// @Service
// @Slf4j
// public class BashExecutor extends WorkExecutor {
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
// private final SqlFunctionService sqlFunctionService;
//
// private final WorkEventRepository workEventRepository;
//
// private final VipWorkVersionRepository vipWorkVersionRepository;
//
// private final WorkConfigRepository workConfigRepository;
//
// private final WorkRepository workRepository;
//
// private final Locker locker;
//
// private final WorkInstanceRepository workInstanceRepository;
//
// public BashExecutor(WorkInstanceRepository workInstanceRepository,
// WorkflowInstanceRepository workflowInstanceRepository, ClusterNodeRepository
// clusterNodeRepository,
// ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, ClusterRepository clusterRepository,
// SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService
// alarmService,
// WorkEventRepository workEventRepository, Scheduler scheduler, Locker locker, WorkRepository
// workRepository,
// WorkRunJobFactory workRunJobFactory, WorkConfigRepository workConfigRepository,
// VipWorkVersionRepository vipWorkVersionRepository) {
//
// super(alarmService, scheduler, locker, workRepository, workInstanceRepository,
// workflowInstanceRepository,
// workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository,
// vipWorkVersionRepository);
// this.clusterNodeRepository = clusterNodeRepository;
// this.clusterNodeMapper = clusterNodeMapper;
// this.aesUtils = aesUtils;
// this.clusterRepository = clusterRepository;
// this.sqlValueService = sqlValueService;
// this.sqlFunctionService = sqlFunctionService;
// this.workEventRepository = workEventRepository;
// this.workConfigRepository = workConfigRepository;
// this.vipWorkVersionRepository = vipWorkVersionRepository;
// this.workRepository = workRepository;
// this.locker = locker;
// this.workInstanceRepository = workInstanceRepository;
// }
//
// @Override
// public String getWorkType() {
// return WorkType.BASH;
// }
//
// public String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
// WorkEventEntity workEvent) {
//
// // 获取作业运行上下文
// WorkRunContext workEventBody = JSON.parseObject(workEvent.getEventContext(),
// WorkRunContext.class);
// StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());
//
// // 检查作业是否合法，并保存脚本信息
// if (processNeverRun(workEvent, 1)) {
//
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测脚本内容 \n");
// if (Strings.isEmpty(workRunContext.getScript())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测脚本失败 : BASH内容为空不能执行
// \n");
// }
//
// // 翻译上游参数
// String jsonPathSql = parseJsonPath(workRunContext.getScript(), workInstance);
// // 翻译脚本中的系统变量
// String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);
// // 翻译脚本中的系统函数
// String script = sqlFunctionService.parseSqlFunction(parseValueSql);
//
// // 打印作业脚本内容
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("Bash脚本:
// \n").append(script)
// .append("\n");
// workInstance = updateInstance(workInstance, logBuilder);
//
// // 检查禁用rm指令
// if (Pattern.compile("\\brm\\b", Pattern.CASE_INSENSITIVE).matcher(script).find()) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测语句失败 : BASH内容包含rm指令不能执行
// \n");
// }
//
// // 保存脚本
// workEventBody.setScript(script);
// workEvent.setEventContext(JSON.toJSONString(workEventBody));
// workEventRepository.saveAndFlush(workEvent);
// }
//
// // 检查集群是否合法，并保存节点信息
// if (processNeverRun(workEvent, 2)) {
//
// // 检测计算集群是否存在
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测集群 \n");
// if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 计算引擎未配置 \n");
// }
//
// // 检查计算集群是否存在
// Optional<ClusterEntity> calculateEngineEntityOptional =
// clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
// if (!calculateEngineEntityOptional.isPresent()
// || ClusterStatus.NO_ACTIVE.equals(calculateEngineEntityOptional.get().getStatus())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 计算引擎不可用 \n");
// }
// // 检查计算集群节点是否配置
// if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterNodeId())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 指定运行节点未配置 \n");
// }
// // 检查集群中是否有合法节点
// Optional<ClusterNodeEntity> nodeRepositoryOptional =
// clusterNodeRepository.findById(workRunContext.getClusterConfig().getClusterNodeId());
// if (!nodeRepositoryOptional.isPresent()) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 指定运行节点不存在 \n");
// }
//
// // 作业检查通过
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
// workInstance = updateInstance(workInstance, logBuilder);
//
// // 翻译成scp的对象
// ClusterNodeEntity clusterNode = nodeRepositoryOptional.get();
// ScpFileEngineNodeDto scpNodeInfo =
// clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(clusterNode);
// scpNodeInfo.setPasswd(aesUtils.decrypt(scpNodeInfo.getPasswd()));
//
// // 保存节点信息
// workEventBody.setScpNodeInfo(scpNodeInfo);
// workEventBody.setAgentHomePath(clusterNode.getAgentHomePath());
// workEvent.setEventContext(JSON.toJSONString(workEventBody));
// workEventRepository.saveAndFlush(workEvent);
// }
//
// // 提交作业，保存pid信息
// if (processNeverRun(workEvent, 3)) {
//
// try {
// // 上传脚本
// scpText(workEventBody.getScpNodeInfo(), workEventBody.getScript() + "\necho
// 'zhiqingyun_success'",
// workEventBody.getAgentHomePath() + "/zhiqingyun-agent/works/" + workInstance.getId() + ".sh");
//
// // 执行命令获取pid
// String executeBashWorkCommand = "source /etc/profile && nohup sh " +
// workEventBody.getAgentHomePath()
// + "/zhiqingyun-agent/works/" + workInstance.getId() + ".sh >> " +
// workEventBody.getAgentHomePath()
// + "/zhiqingyun-agent/works/" + workInstance.getId() + ".log 2>&1 & echo $!";
// String pid =
// executeCommand(workEventBody.getScpNodeInfo(), executeBashWorkCommand, false).replace("\n", "");
//
// // 保存pid
// workInstance.setWorkPid(pid);
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("BASH作业提交成功，pid:【")
// .append(pid).append("】\n");
// workInstance = updateInstance(workInstance, logBuilder);
//
// // 保存pid信息
// workEventBody.setPid(pid);
// workEventBody.setCurrentStatus("");
// workEvent.setEventContext(JSON.toJSONString(workEventBody));
// workEventRepository.saveAndFlush(workEvent);
//
// } catch (JSchException | SftpException | InterruptedException | IOException e) {
//
// log.debug(e.getMessage(), e);
// throw new WorkRunException(
// LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业异常 : " + e.getMessage() + "\n");
// }
// }
//
// // 判断作业是否执行完毕
// if (processNeverRun(workEvent, 4)) {
//
// String getPidStatusCommand = "ps -p " + workEventBody.getPid();
//
// String pidStatus;
// try {
// String pidCommandResult = executeCommand(workEventBody.getScpNodeInfo(), getPidStatusCommand,
// false);
// if (pidCommandResult.contains(workEventBody.getPid())) {
// pidStatus = InstanceStatus.RUNNING;
// } else {
// pidStatus = InstanceStatus.FINISHED;
// }
// } catch (JSchException | InterruptedException | IOException e) {
// throw new WorkRunException(
// LocalDateTime.now() + WorkLog.ERROR_INFO + "获取pid状态异常 : " + e.getMessage() + "\n");
// }
//
// // 当前状态变化保存
// if (!workEventBody.getCurrentStatus().equals(pidStatus)) {
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:").append(pidStatus)
// .append("\n");
// workEventBody.setCurrentStatus(pidStatus);
// workEvent.setEventContext(JSON.toJSONString(workEventBody));
// workEventRepository.saveAndFlush(workEvent);
// }
// workInstance = updateInstance(workInstance, logBuilder);
//
// // 如果还是运行
// if (InstanceStatus.RUNNING.equals(pidStatus)) {
// return InstanceStatus.RUNNING;
// }
// }
//
// // 运行结束，获取作业日志和数据，保存日志、数据信息
// if (processNeverRun(workEvent, 5)) {
//
// // 如果作业不在运行中，不获取数据和日志
// WorkInstanceEntity workInstanceEntity =
// workInstanceRepository.findById(workInstance.getId()).get();
// if (!InstanceStatus.RUNNING.equals(workInstanceEntity.getStatus())) {
// return InstanceStatus.RUNNING;
// }
//
// // 获取日志
// String getLogCommand =
// "cat " + workEventBody.getAgentHomePath() + "/zhiqingyun-agent/works/" + workInstance.getId() +
// ".log";
// String logCommand;
// try {
// logCommand = executeCommand(workEventBody.getScpNodeInfo(), getLogCommand, false);
// } catch (JSchException | InterruptedException | IOException e) {
// throw new WorkRunException(
// LocalDateTime.now() + WorkLog.ERROR_INFO + "获取日志异常 : " + e.getMessage() + "\n");
// }
//
// // 保存运行日志
// String backStr = logCommand.replace("zhiqingyun_success", "");
// workInstance.setYarnLog(backStr);
// workInstance.setResultData(backStr.substring(0, backStr.length() - 2));
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("保存日志成功 \n");
// updateInstance(workInstance, logBuilder);
//
// // 保存节点信息
// workEventBody.setLog(logCommand);
// workEvent.setEventContext(JSON.toJSONString(workEventBody));
// workEventRepository.saveAndFlush(workEvent);
// }
//
// // 删除脚本文件
// if (processNeverRun(workEvent, 6)) {
//
// // 删除脚本和日志
// try {
// String clearWorkRunFile = "rm -f " + workEventBody.getAgentHomePath() +
// "/zhiqingyun-agent/works/"
// + workInstance.getId() + ".log && " + "rm -f " + workEventBody.getAgentHomePath()
// + "/zhiqingyun-agent/works/" + workInstance.getId() + ".sh";
// SshUtils.executeCommand(workEventBody.getScpNodeInfo(), clearWorkRunFile, false);
// } catch (JSchException | InterruptedException | IOException e) {
// log.error("删除运行脚本失败");
// }
// }
//
// // 通过日志文件，判断作业是否运行成功
// if (processNeverRun(workEvent, 7)) {
// // 判断脚本运行成功还是失败
// if (!workEventBody.getLog().contains("zhiqingyun_success")) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常" + "\n");
// }
// }
//
// // 如果没有抛异常，默认执行成功
// return InstanceStatus.SUCCESS;
// }
//
// @Override
// protected void abort(WorkInstanceEntity workInstance) throws Exception {
//
// // 如果没有提交成功，杀死进程
// Thread thread = WORK_THREAD.get(workInstance.getId());
// if (thread != null) {
// WORK_THREAD.remove(workInstance.getId());
// thread.interrupt();
// }
//
// // 如果提交成功，杀死pid，并清理脚本文件
// if (!Strings.isEmpty(workInstance.getWorkPid())) {
//
// String killPidCommand = "kill -9 " + workInstance.getWorkPid();
// ClusterConfig clusterConfig;
// if (workInstance.getVersionId() == null) {
// WorkEntity work = workRepository.findById(workInstance.getWorkId()).get();
// WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();
// clusterConfig = JSON.parseObject(workConfig.getClusterConfig(), ClusterConfig.class);
//
// } else {
// VipWorkVersionEntity workVersion =
// vipWorkVersionRepository.findById(workInstance.getVersionId()).get();
// clusterConfig = JSON.parseObject(workVersion.getClusterConfig(), ClusterConfig.class);
// }
//
// ClusterNodeEntity clusterNode =
// clusterNodeRepository.findById(clusterConfig.getClusterNodeId()).get();
// ScpFileEngineNodeDto scpNodeInfo =
// clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(clusterNode);
// scpNodeInfo.setPasswd(aesUtils.decrypt(scpNodeInfo.getPasswd()));
//
// // 杀死进程
// executeCommand(scpNodeInfo, killPidCommand, false);
//
// // 删除脚本文件
// String clearWorkRunFile = "rm -f " + clusterNode.getAgentHomePath() + "/zhiqingyun-agent/works/"
// + workInstance.getId() + ".log && " + "rm -f " + clusterNode.getAgentHomePath()
// + "/zhiqingyun-agent/works/" + workInstance.getId() + ".sh";
// SshUtils.executeCommand(scpNodeInfo, clearWorkRunFile, false);
// }
// }
// }
