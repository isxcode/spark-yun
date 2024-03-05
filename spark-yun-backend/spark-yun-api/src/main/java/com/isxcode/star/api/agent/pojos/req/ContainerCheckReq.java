package com.isxcode.star.api.agent.pojos.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContainerCheckReq {

	private String port;
}
