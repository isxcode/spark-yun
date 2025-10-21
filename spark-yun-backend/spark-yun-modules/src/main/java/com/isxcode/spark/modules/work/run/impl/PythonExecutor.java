// package com.isxcode.spark.modules.work.run.impl;
//
// import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
// import com.isxcode.spark.api.instance.constants.InstanceStatus;
// import com.isxcode.spark.api.work.constants.WorkLog;
// import com.isxcode.spark.api.work.constants.WorkType;
// import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
// import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
// import com.isxcode.spark.common.locker.Locker;
// import com.isxcode.spark.common.utils.aes.AesUtils;
// import com.isxcode.spark.common.utils.http.HttpUrlUtils;
// import com.isxcode.spark.common.utils.ssh.SshUtils;
// import com.isxcode.spark.modules.alarm.service.AlarmService;
// import com.isxcode.spark.modules.cluster.entity.ClusterNodeEntity;
// import com.isxcode.spark.modules.cluster.mapper.ClusterNodeMapper;
// import com.isxcode.spark.modules.cluster.repository.ClusterNodeRepository;
// import com.isxcode.spark.modules.cluster.repository.ClusterRepository;
// import com.isxcode.spark.modules.datasource.mapper.DatasourceMapper;
// import com.isxcode.spark.modules.datasource.repository.DatasourceRepository;
// import com.isxcode.spark.modules.datasource.service.DatasourceService;
// import com.isxcode.spark.modules.datasource.source.DataSourceFactory;
// import com.isxcode.spark.modules.file.repository.FileRepository;
// import com.isxcode.spark.modules.func.mapper.FuncMapper;
// import com.isxcode.spark.modules.func.repository.FuncRepository;
// import com.isxcode.spark.modules.secret.repository.SecretKeyRepository;
// import com.isxcode.spark.modules.work.entity.WorkEventEntity;
// import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
// import com.isxcode.spark.modules.work.repository.*;
// import com.isxcode.spark.modules.work.run.WorkExecutor;
// import com.isxcode.spark.modules.work.run.WorkRunContext;
// import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
// import com.isxcode.spark.modules.work.sql.SqlCommentService;
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
// import java.util.regex.Pattern;
//
// import static com.isxcode.spark.common.utils.ssh.SshUtils.executeCommand;
// import static com.isxcode.spark.common.utils.ssh.SshUtils.scpText;
//
// @Service
// @Slf4j
// public class PythonExecutor extends WorkExecutor {
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
// public PythonExecutor(WorkInstanceRepository workInstanceRepository,
// WorkflowInstanceRepository workflowInstanceRepository, DatasourceRepository datasourceRepository,
// SqlCommentService sqlCommentService, SqlValueService sqlValueService, SqlFunctionService
// sqlFunctionService,
// AlarmService alarmService, DataSourceFactory dataSourceFactory, DatasourceMapper
// datasourceMapper,
// SecretKeyRepository secretKeyRepository, WorkEventRepository workEventRepository, Scheduler
// scheduler,
// Locker locker, WorkRepository workRepository, WorkRunJobFactory workRunJobFactory,
// WorkConfigRepository workConfigRepository, VipWorkVersionRepository vipWorkVersionRepository,
// ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, ClusterNodeRepository
// clusterNodeRepository,
// ClusterRepository clusterRepository, HttpUrlUtils httpUrlUtils, FuncRepository funcRepository,
// FuncMapper funcMapper, IsxAppProperties isxAppProperties, FileRepository fileRepository,
// DatasourceService datasourceService) {
//
// super(alarmService, scheduler, locker, workRepository, workInstanceRepository,
// workflowInstanceRepository,
// workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository,
// vipWorkVersionRepository);
// this.clusterRepository = clusterRepository;
// this.clusterNodeRepository = clusterNodeRepository;
// this.clusterNodeMapper = clusterNodeMapper;
// this.aesUtils = aesUtils;
// this.sqlValueService = sqlValueService;
// this.sqlFunctionService = sqlFunctionService;
// }
//
// @Override
// public String getWorkType() {
// return WorkType.PYTHON;
// }
//
// @Override
// protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
// WorkEventEntity workEvent) {
//
// // 获取实例日志
// StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());
//
// // 打印首行日志
// if (workEvent.getEventProcess() == 0) {
// logBuilder.append(infoLog("⌛️ 开始申请集群资源"));
// return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
// }
//
// // 检查集群
// if (workEvent.getEventProcess() == 1) {
//
// // 检查计算是否配置
// if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
// throw new WorkRunException(errorLog("⚠️ 检测集群失败 : 计算引擎未配置"));
// }
//
// // 检测集群是否存在
// clusterRepository.findById(workRunContext.getClusterConfig().getClusterId())
// .orElseThrow(() -> new WorkRunException(errorLog("⚠️ 检测集群失败 : 计算引擎不存在")));
//
// // 检查计算节点是否配置
// if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterNodeId())) {
// throw new WorkRunException(errorLog("⚠️ 检测集群失败 : 指定运行节点未配置"));
// }
//
// // 检测集群中节点是否存在
// ClusterNodeEntity agentNode =
// clusterNodeRepository.findById(workRunContext.getClusterConfig().getClusterNodeId())
// .orElseThrow(() -> new WorkRunException(errorLog("⚠️ 检测集群失败 : 指定运行节点不存在")));
//
// // 解析请求节点信息
// ScpFileEngineNodeDto scpNode =
// clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(agentNode);
// scpNode.setPasswd(aesUtils.decrypt(scpNode.getPasswd()));
//
// // 保存事件
// workRunContext.setScpNodeInfo(scpNode);
// workRunContext.setAgentNode(agentNode);
//
// // 保存事件
// logBuilder.append(infoLog("👌 集群检测正常"));
// logBuilder.append(infoLog("⌛️ 开始检测脚本"));
// return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
// }
//
// // 解析Python脚本
// if (workEvent.getEventProcess() == 2) {
//
// // 判断执行脚本是否为空
// if (Strings.isEmpty(workRunContext.getScript())) {
// throw new WorkRunException(errorLog("⚠️ 检测脚本失败 : Bash内容为空不能执行"));
// }
//
// // 解析上游参数
// String jsonPathSql = parseJsonPath(workRunContext.getScript(), workInstance);
//
// // 翻译脚本中的系统变量
// String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);
//
// // 翻译脚本中的系统函数
// String script = sqlFunctionService.parseSqlFunction(parseValueSql);
//
// // 禁用rm指令
// if (Pattern.compile("\\brm\\b", Pattern.CASE_INSENSITIVE).matcher(script).find()) {
// throw new WorkRunException(errorLog("⚠️ 检测脚本失败 : Python脚本包含rm指令不能执行"));
// }
//
// // 保存事件
// workRunContext.setScript(script);
//
// // 保存日志
// logBuilder.append(infoLog("👌 脚本检测正常"));
// logBuilder.append(script).append("\n");
// logBuilder.append(infoLog("⌛️ 开始执行作业"));
// return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
// }
//
// // 提交作业
// if (workEvent.getEventProcess() == 3) {
//
// // 上下文获取参数
// String script = workRunContext.getScript();
// ClusterNodeEntity agentNode = workRunContext.getAgentNode();
// ScpFileEngineNodeDto scpNode = workRunContext.getScpNodeInfo();
//
// String pid;
// try {
// // 上传脚本
// scpText(scpNode, script + "\nprint('zhiqingyun_success')",
// agentNode.getAgentHomePath() + "/zhiqingyun-agent/works/" + workInstance.getId() + ".py");
//
// // 执行命令获取pid
// String executeBashWorkCommand = "source /etc/profile && nohup python3 " +
// agentNode.getAgentHomePath()
// + "/zhiqingyun-agent/works/" + workInstance.getId() + ".py >> " + agentNode.getAgentHomePath()
// + "/zhiqingyun-agent/works/" + workInstance.getId() + ".log 2>&1 & echo $!";
// pid = executeCommand(scpNode, executeBashWorkCommand, false).replace("\n", "");
// logBuilder.append(infoLog("👌 提交作业成功 pid : " + pid));
//
// // 保存实例
// workInstance.setWorkPid(pid);
//
// // 保存上下文
// workRunContext.setPid(pid);
// } catch (JSchException | SftpException | InterruptedException | IOException e) {
// log.debug(e.getMessage(), e);
// throw new WorkRunException(errorLog("⚠️ 提交作业失败 : " + e.getMessage()));
//
// }
//
// // 保存日志
// logBuilder.append(infoLog("⌛️ 开始监听状态"));
// return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
// }
//
// // 监听作业状态
// if (workEvent.getEventProcess() == 4) {
//
// // 提交作业成功后，开始循环判断状态
// String preStatus = workRunContext.getPreStatus() == null ? "" : workRunContext.getPreStatus();
// String pid = workRunContext.getPid();
// ScpFileEngineNodeDto scpNodeInfo = workRunContext.getScpNodeInfo();
//
// // 获取pid状态
// String pidStatus;
// try {
// String getPidStatusCommand = "ps -p " + pid;
// String pidCommandResult = executeCommand(scpNodeInfo, getPidStatusCommand, false);
// pidStatus = pidCommandResult.contains(pid) ? InstanceStatus.RUNNING : InstanceStatus.FINISHED;
// } catch (JSchException | InterruptedException | IOException e) {
// throw new WorkRunException(errorLog("获取pid状态异常 : " + e.getMessage()));
// }
//
// // 如果状态发生变化，则保存日志
// if (!preStatus.equals(pidStatus)) {
// logBuilder.append(infoLog("⏩ 运行状态: " + pidStatus));
//
// // 更新实例
// updateInstance(workInstance, logBuilder);
//
// // 更新上下文
// workRunContext.setPreStatus(pidStatus);
// updateWorkEvent(workEvent, workRunContext);
// }
//
// // 如果是运行中状态，直接返回
// if (InstanceStatus.RUNNING.equals(pidStatus)) {
// return InstanceStatus.RUNNING;
// }
//
// // 其他状态则为运行结束
// logBuilder.append(infoLog("⌛️ 开始保存作业日志和数据"));
// return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
// }
//
// // 保存作业日志和数据
// if (workEvent.getEventProcess() == 5) {
//
// // 获取上下文
// ScpFileEngineNodeDto scpNodeInfo = workRunContext.getScpNodeInfo();
// ClusterNodeEntity agentNode = workRunContext.getAgentNode();
//
// // 获取日志
// String getLogCommand =
// "cat " + agentNode.getAgentHomePath() + "/zhiqingyun-agent/works/" + workInstance.getId() +
// ".log";
// String logCommand;
// try {
// logCommand = executeCommand(scpNodeInfo, getLogCommand, false);
// } catch (JSchException | InterruptedException | IOException e) {
// throw new WorkRunException(errorLog("⚠️ 获取作业日志异常 : " + e.getMessage()));
// }
//
// // 解析日志并保存
// String backStr = logCommand.replace("zhiqingyun_success", "");
// workInstance.setYarnLog(backStr);
// workInstance.setResultData(backStr.substring(0, backStr.length() - 2));
// logBuilder.append(infoLog("👌 日志保存成功"));
//
// // 如果日志不包含关键字则为失败
// if (!logCommand.contains("zhiqingyun_success")) {
// workRunContext.setPreStatus(InstanceStatus.FAIL);
// }
//
// // 保存日志
// logBuilder.append(infoLog("⌛️ 开始清理执行文件"));
// return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
// }
//
// // 清理作业执行文件
// if (workEvent.getEventProcess() == 6) {
//
// // 获取上下文
// ScpFileEngineNodeDto scpNode = workRunContext.getScpNodeInfo();
// ClusterNodeEntity agentNode = workRunContext.getAgentNode();
//
// // 删除脚本和日志
// try {
// String clearWorkRunFile = "rm -f " + agentNode.getAgentHomePath() + "/zhiqingyun-agent/works/"
// + workInstance.getId() + ".log && " + "rm -f " + agentNode.getAgentHomePath()
// + "/zhiqingyun-agent/works/" + workInstance.getId() + ".py";
// SshUtils.executeCommand(scpNode, clearWorkRunFile, false);
// } catch (JSchException | InterruptedException | IOException e) {
// throw new WorkRunException(errorLog("⚠️ 删除运行脚本失败 : " + e.getMessage()));
// }
//
// // 保存日志
// logBuilder.append(infoLog("👌 清理执行脚本完成"));
// updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
// }
//
// // 如果最终状态为失败，抛出空异常
// if (InstanceStatus.FAIL.equals(workRunContext.getPreStatus())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "⚠️ 作业最终状态为失败");
// }
//
// return InstanceStatus.SUCCESS;
// }
//
// @Override
// protected void abort(WorkInstanceEntity workInstance) {
//
// Thread thread = WORK_THREAD.get(workInstance.getId());
// thread.interrupt();
// }
// }
