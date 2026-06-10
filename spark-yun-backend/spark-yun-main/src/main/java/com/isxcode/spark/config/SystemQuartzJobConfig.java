package com.isxcode.spark.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SystemQuartzJobConfig {

    private static final String SYSTEM_JOB_GROUP = "system";

    private final Scheduler scheduler;

    @PostConstruct
    public void initSystemJobs() throws SchedulerException {

        scheduleCronJob(NodeMonitorQuartzJob.class, "node-monitor", "0 * * * * ?");
        scheduleCronJob(FormLinkCleanupQuartzJob.class, "form-link-cleanup", "0 0 0 * * ?");
    }

    private void scheduleCronJob(Class<? extends Job> jobClass, String name, String cron) throws SchedulerException {

        JobKey jobKey = JobKey.jobKey(name, SYSTEM_JOB_GROUP);
        TriggerKey triggerKey = TriggerKey.triggerKey(name, SYSTEM_JOB_GROUP);
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey).storeDurably().build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).forJob(jobKey)
            .withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing()).build();

        scheduler.addJob(jobDetail, true);
        if (scheduler.checkExists(triggerKey)) {
            scheduler.rescheduleJob(triggerKey, trigger);
            return;
        }

        try {
            scheduler.scheduleJob(trigger);
        } catch (ObjectAlreadyExistsException e) {
            log.debug("System quartz trigger already exists, rescheduling it: {}", triggerKey);
            scheduler.rescheduleJob(triggerKey, trigger);
        }
    }
}
