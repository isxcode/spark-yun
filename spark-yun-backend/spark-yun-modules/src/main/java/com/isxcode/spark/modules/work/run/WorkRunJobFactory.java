package com.isxcode.spark.modules.work.run;

import com.alibaba.fastjson2.JSON;
import com.isxcode.spark.api.work.constants.QuartzPrefix;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import static com.isxcode.spark.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.spark.common.config.CommonConfig.USER_ID;

/**
 * 作业运行，触发定时器.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkRunJobFactory {

    private final Scheduler scheduler;

    private final WorkEventRepository workEventRepository;

    public void execute(WorkRunContext workRunContext) {

        // 异步赋值环境变量
        USER_ID.set(workRunContext.getUserId());
        TENANT_ID.set(workRunContext.getTenantId());

        // 初始化作业运行事件
        WorkEventEntity workEvent =
            WorkEventEntity.builder().eventProcess(0).eventContext(JSON.toJSONString(workRunContext)).build();
        workEvent = workEventRepository.saveAndFlush(workEvent);

        try {
            // 封装调度器的运行参数
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(QuartzPrefix.USER_ID, workRunContext.getUserId());
            jobDataMap.put(QuartzPrefix.TENANT_ID, workRunContext.getTenantId());
            jobDataMap.put(QuartzPrefix.WORK_TYPE, workRunContext.getWorkType());
            jobDataMap.put(QuartzPrefix.WORK_EVENT_TYPE, workRunContext.getEventType());
            jobDataMap.put(QuartzPrefix.WORK_EVENT_ID, workEvent.getId());

            // 初始化调度器，每1秒执行一次
            JobDetail jobDetail = JobBuilder.newJob(WorkRunJob.class).setJobData(jobDataMap).build();
            Trigger trigger = TriggerBuilder.newTrigger()
                .withSchedule(
                    CronScheduleBuilder.cronSchedule("*/1 * * * * ? ").withMisfireHandlingInstructionDoNothing())
                .withIdentity(QuartzPrefix.WORK_RUN_PROCESS + workEvent.getId()).build();

            // 创建并触发调度器
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.getListenerManager().addJobListener(new QuartzJobErrorListener());
            scheduler.start();
        } catch (Exception e) {
            throw new IsxAppException("刷新作业运行的调度器创建失败: " + e.getMessage());
        }
    }
}
