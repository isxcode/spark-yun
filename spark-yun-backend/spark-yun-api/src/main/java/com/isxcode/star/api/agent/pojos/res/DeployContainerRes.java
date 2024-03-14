package com.isxcode.star.api.agent.pojos.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeployContainerRes {

	private String appId;

	private int port;
}
