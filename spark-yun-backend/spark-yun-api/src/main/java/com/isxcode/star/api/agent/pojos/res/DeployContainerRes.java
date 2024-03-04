package com.isxcode.star.api.agent.pojos.res;

import com.isxcode.star.api.agent.pojos.req.PluginReq;
import com.isxcode.star.api.agent.pojos.req.SparkSubmit;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeployContainerRes {

  private String appId;

  private int port;
}
