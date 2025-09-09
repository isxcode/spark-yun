package com.isxcode.spark.modules.work.run;

import com.alibaba.fastjson2.JSON;
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

/**
 * 作业运行定时器.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkRunJob implements Job {

    private final WorkExecutorFactory workExecutorFactory;

    private final Scheduler scheduler;

    private final WorkEventRepository workEventRepository;

    private final Locker locker;

    @Override
    public void execute(JobExecutionContext context) {

        // 获取调度器中的参数
        WorkRunContext workRunContext = JSON.parseObject(
            String.valueOf(context.getJobDetail().getJobDataMap().get(QuartzPrefix.WORK_RUN_CONTEXT)), WorkRunContext.class);

        // 异步刷新环境变量
        USER_ID.set(workRunContext.getUserId());
        TENANT_ID.set(workRunContext.getTenantId());

        // 触发作业运行
        String runStatus;
        try {
            WorkExecutor workExecutor = workExecutorFactory.create(workRunContext.getWorkType());
            runStatus = workExecutor.runWork(workRunContext);
        } catch (Exception e) {
            // 作业运行漏捕获的异常，直接事件结束，防止一直调度
            log.error(e.getMessage(), e);
            runStatus = InstanceStatus.FINISHED;
        }

        // 作业事件运行结束，调度器和作业事件都要删除，且只会在这里销毁作业事件和调度器
        if (InstanceStatus.FINISHED.equals(runStatus)) {
            try {
                scheduler.unscheduleJob(TriggerKey.triggerKey("event_" + workRunContext.getEventId()));
                workEventRepository.deleteById(workRunContext.getEventId());
            } catch (Exception ignore) {
                // 异常不处理
            }
        }
    }
}
