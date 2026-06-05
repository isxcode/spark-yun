package com.isxcode.spark.config;

import com.isxcode.spark.vip.modules.form.service.FormBizService;
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
public class FormLinkCleanupQuartzJob implements Job {

    private final FormBizService formBizService;

    @Override
    public void execute(JobExecutionContext context) {

        formBizService.scheduleDeleteFormLink();
    }
}
