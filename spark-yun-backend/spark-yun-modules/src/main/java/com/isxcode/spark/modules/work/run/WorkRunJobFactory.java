package com.isxcode.spark.modules.work.run;

import com.alibaba.fastjson2.JSON;
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

        // 设置租户和用户
        USER_ID.set(workRunContext.getUserId());
        TENANT_ID.set(workRunContext.getTenantId());

        // 初始化作业事件
        WorkEventEntity workEvent =
            WorkEventEntity.builder().eventProcess(0).eventContext(JSON.toJSONString(workRunContext)).build();
        workEvent = workEventRepository.saveAndFlush(workEvent);

        // 封装调度器的运行参数
        JobDataMap jobDataMap = new JobDataMap();
        workRunContext.setEventId(workEvent.getId());
        jobDataMap.put("workRunContext", JSON.toJSONString(workRunContext));

        // 初始化调度器，每2秒执行一次
        JobDetail jobDetail = JobBuilder.newJob(WorkRunJob.class).setJobData(jobDataMap).build();
        Trigger trigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule("*/1 * * * * ? ").withMisfireHandlingInstructionDoNothing())
            .withIdentity("event_" + workRunContext.getEventId()).build();

        // 创建并触发调度器
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.getListenerManager().addJobListener(new QuartzJobErrorListener());
            scheduler.start();
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("刷新作业运行的调度器创建失败");
        }
    }
}
