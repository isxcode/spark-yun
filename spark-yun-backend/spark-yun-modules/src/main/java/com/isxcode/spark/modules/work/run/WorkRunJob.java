package com.isxcode.spark.modules.work.run;

import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.QuartzPrefix;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;


import static com.isxcode.spark.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.spark.common.config.CommonConfig.USER_ID;

@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class WorkRunJob implements Job {

    private final WorkExecutorFactory workExecutorFactory;

    private final Scheduler scheduler;

    private final WorkEventRepository workEventRepository;
    private final Locker locker;

    @Override
    public void execute(JobExecutionContext context) {

        // 定时器中获取事件id
        String workEventId = String.valueOf(context.getJobDetail().getJobDataMap().get(QuartzPrefix.WORK_EVENT_ID));

        // 加锁
        Integer lockerKey = locker.lockOnly(workEventId);

        // 刷新异步环境变量
        USER_ID.set(String.valueOf(context.getJobDetail().getJobDataMap().get(QuartzPrefix.USER_ID)));
        TENANT_ID.set(String.valueOf(context.getJobDetail().getJobDataMap().get(QuartzPrefix.TENANT_ID)));

        // 获取当前作业运行状态
        String runStatus;
        try {
            // 通过作业类型，获取作业执行器
            String workType = String.valueOf(context.getJobDetail().getJobDataMap().get(QuartzPrefix.WORK_TYPE));
            WorkExecutor workExecutor = workExecutorFactory.create(workType);

            // 运行作业获取作业运行状态
            String eventType = String.valueOf(context.getJobDetail().getJobDataMap().get(QuartzPrefix.WORK_EVENT_TYPE));
            runStatus = workExecutor.runWork(workEventId, eventType);

        } catch (Exception e) {

            // 捕获的作业运行可能漏掉的异常，直接完成，防止死循环
            log.error(e.getMessage(), e);
            runStatus = InstanceStatus.FINISHED;
        }

        // 作业事件运行结束，调度器和作业事件都要删除，且只会在这里销毁作业事件和调度器
        try {
            if (InstanceStatus.FINISHED.equals(runStatus)) {
                scheduler.unscheduleJob(TriggerKey.triggerKey(QuartzPrefix.WORK_RUN_PROCESS + workEventId));
                workEventRepository.deleteById(workEventId);
                log.debug("WorkRunJob 执行完成，已清理调度器和事件，EventId: {}", workEventId);
            }
        } catch (Exception ignore) {
            log.warn("清理调度器和事件时发生异常，EventId: {}", workEventId);
        } finally {
            // 最终都要解锁
            locker.unlock(lockerKey);
        }

    }
}
