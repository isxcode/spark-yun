package com.isxcode.star.api.pojos.yun.agent.req;

import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import lombok.Data;

@Data
public class YagExecuteWorkReq {

  private PluginReq pluginReq;

  private SparkSubmit sparkSubmit;

  private String agentHomePath;
}
