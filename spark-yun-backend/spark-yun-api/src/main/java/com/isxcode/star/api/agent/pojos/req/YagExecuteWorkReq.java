package com.isxcode.star.api.agent.pojos.req;

import lombok.Data;

@Data
public class YagExecuteWorkReq {

  private PluginReq pluginReq;

  private SparkSubmit sparkSubmit;

  private String agentHomePath;

  private String agentType;
}
