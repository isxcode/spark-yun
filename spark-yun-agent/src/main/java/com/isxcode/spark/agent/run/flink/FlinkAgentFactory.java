package com.isxcode.spark.agent.run.flink;

import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlinkAgentFactory {

    private final ApplicationContext applicationContext;

    public FlinkAgentService getAgentService(String clusterType) {

        return applicationContext.getBeansOfType(FlinkAgentService.class).values().stream()
            .filter(agent -> agent.getAgentType().equals(clusterType)).findFirst()
            .orElseThrow(() -> new IsxAppException("集群类型不支持"));
    }

}
