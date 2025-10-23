package com.isxcode.spark.modules.work.run;

import com.alibaba.fastjson2.JSON;
import com.isxcode.spark.api.work.constants.QuartzPrefix;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
import com.isxcode.spark.modules.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class WorkRunJobFactory {

    private final Scheduler scheduler;

    private final WorkEventRepository workEventRepository;

    private final WorkService workService;

    private final WorkInstanceRepository workInstanceRepository;

    public void run(WorkRunContext workRunContext) {

        // 初始化作业运行事件
        WorkEventEntity workEvent =
            WorkEventEntity.builder().eventProcess(0).eventContext(JSON.toJSONString(workRunContext)).build();
        workEvent = workEventRepository.save(workEvent);

        // 把事件id保存到实例中
        WorkInstanceEntity workInstance = workService.getWorkInstance(workRunContext.getInstanceId());
        workInstance.setEventId(workEvent.getId());
        workInstanceRepository.save(workInstance);

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
            log.error(e.getMessage(), e);
            throw new IsxAppException("作业运行异常: " + e.getMessage());
        }
    }
}
