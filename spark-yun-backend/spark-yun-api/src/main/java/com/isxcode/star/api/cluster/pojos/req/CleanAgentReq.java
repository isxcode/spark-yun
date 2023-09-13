package com.isxcode.star.api.cluster.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CleanAgentReq {
	@Schema(title = "计算引擎唯一id", example = "sy_b0288cadb2ab4325ae519ff329a95cda")
	@NotEmpty(message = "节点id不能为空")
	private String engineNodeId;
}
