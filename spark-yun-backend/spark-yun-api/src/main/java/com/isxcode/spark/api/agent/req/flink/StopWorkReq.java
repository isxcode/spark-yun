package com.isxcode.spark.api.agent.req.flink;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StopWorkReq {

    private String flinkHome;

    private String appId;

    private String clusterType;

    private String agentHomePath;
}
