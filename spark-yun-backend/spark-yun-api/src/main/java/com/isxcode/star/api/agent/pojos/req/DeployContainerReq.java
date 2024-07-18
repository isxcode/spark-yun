package com.isxcode.star.api.agent.pojos.req;

import lombok.Data;

@Data
public class DeployContainerReq {

    private PluginReq pluginReq;

    private SparkSubmit sparkSubmit;

    private String agentHomePath;

    private String agentType;

    private String sparkHomePath;

    private String args;

    private String containerId;
}
