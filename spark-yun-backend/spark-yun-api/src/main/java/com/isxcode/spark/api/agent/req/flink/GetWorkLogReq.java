package com.isxcode.spark.api.agent.req.flink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetWorkLogReq {

    private String appId;

    private String flinkHome;

    private String clusterType;

    private String agentHomePath;

    private String workInstanceId;

    private String workStatus;
}
