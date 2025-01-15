package com.isxcode.star.api.agent.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ContainerCheckReq {

    private String port;

    private String appId;

    private String agentType;
}
