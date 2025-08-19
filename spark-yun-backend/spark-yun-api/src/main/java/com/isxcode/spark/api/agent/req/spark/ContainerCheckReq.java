package com.isxcode.spark.api.agent.req.spark;

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
