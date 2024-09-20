package com.isxcode.star.modules.work.run;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class WorkExecutorFactory {

    private final ApplicationContext applicationContext;

    public WorkExecutor create(String workType) {

        return applicationContext.getBeansOfType(WorkExecutor.class).values().stream()
            .filter(agent -> agent.getWorkType().equals(workType)).findFirst()
            .orElseThrow(() -> new IsxAppException("作业类型不支持"));
    }
}
