package com.isxcode.star.api.agent.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetWorkStderrLogReq {

    private String appId;

    private String clusterType;

    private String sparkHomePath;
}
