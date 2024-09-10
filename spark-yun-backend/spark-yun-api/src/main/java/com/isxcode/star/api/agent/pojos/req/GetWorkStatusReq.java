package com.isxcode.star.api.agent.pojos.req;

import lombok.Data;

@Data
public class GetWorkStatusReq {

    private String appId;

    private String clusterType;

    private String sparkHomePath;
}
