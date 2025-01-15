package com.isxcode.star.api.agent.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StopWorkReq {

    private String appId;

    private String clusterType;

    private String sparkHomePath;

    private String agentHomePath;
}
