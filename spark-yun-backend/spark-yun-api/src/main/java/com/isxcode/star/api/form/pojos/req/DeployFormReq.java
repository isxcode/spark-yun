package com.isxcode.star.api.form.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeployFormReq {

	@Schema(title = "表单id", example = "sy_123")
	@NotEmpty(message = "formId不能为空")
	private String formId;

}
