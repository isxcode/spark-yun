package com.isxcode.star.agent.run;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AgentFactory {

    private final ApplicationContext applicationContext;

    public AgentService getAgentService(String clusterType) {

        return applicationContext.getBeansOfType(AgentService.class).values().stream()
            .filter(agent -> agent.getAgentType().equals(clusterType)).findFirst()
            .orElseThrow(() -> new IsxAppException("agent类型不支持"));
    }
}
