package com.isxcode.spark.config;

import com.isxcode.spark.modules.monitor.service.MonitorBizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class NodeMonitorQuartzJob implements Job {

    private final MonitorBizService monitorBizService;

    @Override
    public void execute(JobExecutionContext context) {

        monitorBizService.scheduleGetNodeMonitor();
    }
}
