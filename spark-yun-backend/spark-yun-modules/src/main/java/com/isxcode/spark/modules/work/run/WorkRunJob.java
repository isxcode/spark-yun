package com.isxcode.spark.modules.work.run;

import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.QuartzPrefix;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;


import static com.isxcode.spark.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.spark.common.config.CommonConfig.USER_ID;

/**
 * 定时器，每秒执行作业
 */
@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class WorkRunJob implements Job {

    private final WorkExecutorFactory workExecutorFactory;

    private final Scheduler scheduler;

    private final WorkEventRepository workEventRepository;

    @Override
    public void execute(JobExecutionContext context) {

        // 获取调度器中的作业运行所需要的上下文
        String workEventId = String.valueOf(context.getJobDetail().getJobDataMap().get(QuartzPrefix.WORK_EVENT_ID));

        // 异步刷新环境变量
        USER_ID.set(String.valueOf(context.getJobDetail().getJobDataMap().get(QuartzPrefix.USER_ID)));
        TENANT_ID.set(String.valueOf(context.getJobDetail().getJobDataMap().get(QuartzPrefix.TENANT_ID)));

        // 触发作业运行
        String runStatus;
        try {
            WorkExecutor workExecutor = workExecutorFactory
                .create(String.valueOf(context.getJobDetail().getJobDataMap().get(QuartzPrefix.WORK_TYPE)));
            runStatus = workExecutor.runWork(workEventId,
                String.valueOf(context.getJobDetail().getJobDataMap().get(QuartzPrefix.WORK_EVENT_TYPE)));
        } catch (Exception e) {
            // 作业运行漏捕获的异常，直接事件结束，防止一直调度
            log.error("WorkRunJob 执行异常: {}", e.getMessage(), e);
            runStatus = InstanceStatus.FINISHED;
        }

        // 作业事件运行结束，调度器和作业事件都要删除，且只会在这里销毁作业事件和调度器
        if (InstanceStatus.FINISHED.equals(runStatus)) {
            try {
                scheduler.unscheduleJob(TriggerKey.triggerKey(QuartzPrefix.WORK_RUN_PROCESS + workEventId));
                workEventRepository.deleteById(workEventId);
                log.debug("WorkRunJob 执行完成，已清理调度器和事件，EventId: {}", workEventId);
            } catch (Exception ignore) {
                log.warn("清理调度器和事件时发生异常，EventId: {}", workEventId);
            }
        }
    }
}
