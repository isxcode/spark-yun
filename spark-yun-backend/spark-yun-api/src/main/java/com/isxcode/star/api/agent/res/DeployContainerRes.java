package com.isxcode.star.api.agent.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeployContainerRes {

    private String appId;

    private int port;

    private String errLog;

    private String status;
}
