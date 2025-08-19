package com.isxcode.spark.agent.run.spark;

import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SparkAgentFactory {

    private final ApplicationContext applicationContext;

    public SparkAgentService getAgentService(String clusterType) {

        return applicationContext.getBeansOfType(SparkAgentService.class).values().stream()
            .filter(agent -> agent.getAgentType().equals(clusterType)).findFirst()
            .orElseThrow(() -> new IsxAppException("agent类型不支持"));
    }
}
