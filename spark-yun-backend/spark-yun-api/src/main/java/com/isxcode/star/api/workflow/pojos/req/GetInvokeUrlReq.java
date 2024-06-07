package com.isxcode.star.api.workflow.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetInvokeUrlReq {

	@Schema(title = "工作流id", example = "123")
	@NotEmpty(message = "工作流id不能为空")
	private String workflowId;
}
