//package com.isxcode.spark.modules.work.run.impl;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.RuntimeUtil;
//import com.alibaba.fastjson.JSON;
//import com.isxcode.spark.api.work.constants.WorkLog;
//import com.isxcode.spark.api.work.constants.WorkType;
//import com.isxcode.spark.api.instance.constants.InstanceStatus;
//import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
//import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
//import com.isxcode.spark.common.utils.path.PathUtils;
//import com.isxcode.spark.modules.alarm.service.AlarmService;
//import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
//import com.isxcode.spark.modules.work.entity.WorkEventEntity;
//import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
//import com.isxcode.spark.modules.work.repository.WorkEventRepository;
//import com.isxcode.spark.modules.work.run.WorkExecutor;
//import com.isxcode.spark.modules.work.run.WorkRunContext;
//import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
//import com.isxcode.spark.modules.work.repository.VipWorkVersionRepository;
//import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
//import com.isxcode.spark.modules.work.repository.WorkRepository;
//import com.isxcode.spark.common.locker.Locker;
//import com.isxcode.spark.modules.work.sql.SqlFunctionService;
//import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
//import org.quartz.Scheduler;
//import lombok.Data;
//import lombok.Builder;
//import lombok.NoArgsConstructor;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.logging.log4j.util.Strings;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.time.LocalDateTime;
//
//@Service
//@Slf4j
//public class CurlExecutor extends WorkExecutor {
//
//    private final IsxAppProperties isxAppProperties;
//
//    private final WorkEventRepository workEventRepository;
//
//    private final Scheduler scheduler;
//
//    private final WorkRunJobFactory workRunJobFactory;
//
//    private final VipWorkVersionRepository vipWorkVersionRepository;
//
//    private final WorkConfigRepository workConfigRepository;
//
//    private final WorkRepository workRepository;
//
//    private final Locker locker;
//
//    public CurlExecutor(WorkInstanceRepository workInstanceRepository,
//        WorkflowInstanceRepository workflowInstanceRepository, IsxAppProperties isxAppProperties,
//        AlarmService alarmService, SqlFunctionService sqlFunctionService, WorkEventRepository workEventRepository,
//        Scheduler scheduler, WorkRunJobFactory workRunJobFactory, VipWorkVersionRepository vipWorkVersionRepository,
//        WorkConfigRepository workConfigRepository, WorkRepository workRepository, Locker locker) {
//
//        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
//            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository);
//        this.isxAppProperties = isxAppProperties;
//        this.workEventRepository = workEventRepository;
//        this.scheduler = scheduler;
//        this.workRunJobFactory = workRunJobFactory;
//        this.vipWorkVersionRepository = vipWorkVersionRepository;
//        this.workConfigRepository = workConfigRepository;
//        this.workRepository = workRepository;
//        this.locker = locker;
//    }
//
//    @Override
//    public String getWorkType() {
//        return WorkType.CURL;
//    }
//
//    @Override
//    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
//        WorkEventEntity workEvent) {
//
//        // 获取作业运行上下文
//        CurlExecutorContext workEventBody = JSON.parseObject(workEvent.getEventContext(), CurlExecutorContext.class);
//        if (workEventBody == null) {
//            workEventBody = new CurlExecutorContext();
//            workEventBody.setWorkRunContext(workRunContext);
//        }
//        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());
//        // 检查脚本内容和处理，保存脚本信息
//        if (processNeverRun(workEvent, 1)) {
//
//            // 判断执行脚本是否为空
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测脚本内容 \n");
//            if (Strings.isEmpty(workRunContext.getScript())) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测脚本失败 : Curl内容为空不能执行 \n");
//            }
//
//            // 脚本检查通过，处理curl命令
//            String processedScript = workRunContext.getScript();
//            if (!processedScript.contains("curl -s ") && !processedScript.contains("curl --silent ")) {
//                processedScript = processedScript.replace("curl ", "curl -s ");
//            }
//
//            // 添加网络状态字段
//            processedScript = processedScript.replace("curl", "curl -w \"%{http_code}\" ");
//
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("执行作业内容: \n")
//                .append(processedScript).append("\n");
//            workInstance = updateInstance(workInstance, logBuilder);
//
//            // 保存处理后的脚本
//            workEventBody.setProcessedScript(processedScript);
//            workEvent.setEventContext(JSON.toJSONString(workEventBody));
//            workEventRepository.saveAndFlush(workEvent);
//        }
//        // 执行curl命令，保存执行结果
//        if (processNeverRun(workEvent, 2)) {
//
//            // 获取处理后的脚本
//            String processedScript = workEventBody.getProcessedScript();
//
//            // 将脚本推送到本地
//            String bashFile = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + "/work/"
//                + workRunContext.getTenantId() + "/" + workInstance.getId() + ".sh";
//            FileUtil.writeUtf8String(processedScript + " \\\n && echo 'zhiqingyun_success'", bashFile);
//
//            // 执行命令
//            String executeBashWorkCommand = "bash " + PathUtils.parseProjectPath(isxAppProperties.getResourcesPath())
//                + "/work/" + workRunContext.getTenantId() + "/" + workInstance.getId() + ".sh";
//            String result = RuntimeUtil.execForStr(executeBashWorkCommand);
//
//            // 保存运行日志
//            String yarnLog = result.replace("&& echo 'zhiqingyun_success'", "").replace("zhiqingyun_success", "");
//            workInstance.setYarnLog(yarnLog);
//            workInstance.setResultData(yarnLog.substring(0, yarnLog.length() - 4));
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("保存结果成功 \n");
//            updateInstance(workInstance, logBuilder);
//
//            // 删除脚本和日志
//            try {
//                String clearWorkRunFile =
//                    "rm -f " + PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "work"
//                        + File.separator + workRunContext.getTenantId() + File.separator + workInstance.getId() + ".sh";
//                RuntimeUtil.execForStr(clearWorkRunFile);
//            } catch (Exception e) {
//                log.error("删除运行脚本失败");
//            }
//
//            // 判断脚本运行成功还是失败
//            if (!result.contains("200zhiqingyun_success")) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常" + "\n");
//            }
//
//            // 保存执行结果
//            workEventBody.setResult(result);
//            workEvent.setEventContext(JSON.toJSONString(workEventBody));
//            workEventRepository.saveAndFlush(workEvent);
//        }
//
//        return InstanceStatus.SUCCESS;
//    }
//
//    @Override
//    protected void abort(WorkInstanceEntity workInstance) {
//
//        Thread thread = WORK_THREAD.get(workInstance.getId());
//        thread.interrupt();
//    }
//
//    @Data
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class CurlExecutorContext {
//
//        private WorkRunContext workRunContext;
//
//        private String processedScript;
//
//        private String result;
//    }
//}
