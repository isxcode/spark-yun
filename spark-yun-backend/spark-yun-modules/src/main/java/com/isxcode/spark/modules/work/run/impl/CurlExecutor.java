// package com.isxcode.spark.modules.work.run.impl;
//
// import cn.hutool.core.io.FileUtil;
// import cn.hutool.core.util.RuntimeUtil;
// import com.isxcode.spark.api.work.constants.WorkLog;
// import com.isxcode.spark.api.work.constants.WorkType;
// import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
// import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
// import com.isxcode.spark.common.locker.Locker;
// import com.isxcode.spark.common.utils.path.PathUtils;
// import com.isxcode.spark.modules.alarm.service.AlarmService;
// import com.isxcode.spark.modules.datasource.mapper.DatasourceMapper;
// import com.isxcode.spark.modules.datasource.source.DataSourceFactory;
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
// import lombok.extern.slf4j.Slf4j;
// import org.apache.logging.log4j.util.Strings;
// import org.quartz.Scheduler;
// import org.springframework.stereotype.Service;
//
// import java.io.File;
// import java.time.LocalDateTime;
//
// @Service
// @Slf4j
// public class CurlExecutor extends WorkExecutor {
//
// private final IsxAppProperties isxAppProperties;
//
// public CurlExecutor(WorkInstanceRepository workInstanceRepository,
// WorkflowInstanceRepository workflowInstanceRepository, SqlCommentService sqlCommentService,
// SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService
// alarmService,
// DataSourceFactory dataSourceFactory, DatasourceMapper datasourceMapper, SecretKeyRepository
// secretKeyRepository,
// WorkEventRepository workEventRepository, Scheduler scheduler, Locker locker, WorkRepository
// workRepository,
// WorkRunJobFactory workRunJobFactory, WorkConfigRepository workConfigRepository,
// VipWorkVersionRepository vipWorkVersionRepository, IsxAppProperties isxAppProperties) {
//
// super(alarmService, scheduler, locker, workRepository, workInstanceRepository,
// workflowInstanceRepository,
// workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository,
// vipWorkVersionRepository);
// this.isxAppProperties = isxAppProperties;
// }
//
// @Override
// public String getWorkType() {
// return WorkType.CURL;
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
// logBuilder.append(infoLog("⌛️ 开始检测curl脚本"));
// return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
// }
//
// // 判断执行脚本是否为空
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测脚本内容 \n");
// if (Strings.isEmpty(workRunContext.getScript())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测脚本失败 : Curl内容为空不能执行
// \n");
// }
//
// // 脚本检查通过
// if (!workRunContext.getScript().contains("curl -s ")
// && !workRunContext.getScript().contains("curl --silent ")) {
// workRunContext.setScript(workRunContext.getScript().replace("curl ", "curl -s "));
// }
//
// // 添加网络状态字段
// workRunContext.setScript(workRunContext.getScript().replace("curl", "curl -w \"%{http_code}\"
// "));
//
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("执行作业内容: \n")
// .append(workRunContext.getScript()).append("\n");
// workInstance = updateInstance(workInstance, logBuilder);
//
// // 将脚本推送到本地
// String bashFile = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + "/work/"
// + workRunContext.getTenantId() + "/" + workInstance.getId() + ".sh";
// FileUtil.writeUtf8String(workRunContext.getScript() + " \\\n && echo 'zhiqingyun_success'",
// bashFile);
//
// // 执行命令
// String executeBashWorkCommand = "bash " +
// PathUtils.parseProjectPath(isxAppProperties.getResourcesPath())
// + "/work/" + workRunContext.getTenantId() + "/" + workInstance.getId() + ".sh";
// String result = RuntimeUtil.execForStr(executeBashWorkCommand);
//
// // 保存运行日志
// String yarnLog = result.replace("&& echo 'zhiqingyun_success'", "").replace("zhiqingyun_success",
// "");
// workInstance.setYarnLog(yarnLog);
// workInstance.setResultData(yarnLog.substring(0, yarnLog.length() - 4));
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("保存结果成功 \n");
// updateInstance(workInstance, logBuilder);
//
// // 删除脚本和日志
// try {
// String clearWorkRunFile =
// "rm -f " + PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator +
// "work"
// + File.separator + workRunContext.getTenantId() + File.separator + workInstance.getId() + ".sh";
// RuntimeUtil.execForStr(clearWorkRunFile);
// } catch (Exception e) {
// log.error("删除运行脚本失败");
// }
//
// // 判断脚本运行成功还是失败
// if (!result.contains("200zhiqingyun_success")) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常" + "\n");
// }
// }
//
// @Override
// protected void abort(WorkInstanceEntity workInstance) {
//
// Thread thread = WORK_THREAD.get(workInstance.getId());
// thread.interrupt();
// }
// }
