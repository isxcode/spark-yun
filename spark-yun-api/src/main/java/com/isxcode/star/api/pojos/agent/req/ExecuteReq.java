package com.isxcode.star.api.pojos.agent.req;

import lombok.Data;

@Data
public class ExecuteReq {

  private String appName;

  private String mainClass;

  private String appResourceName;

  private PluginReq pluginReq;

  private String sql;

  private String homePath;
}
