package com.isxcode.star.api.real.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class StartRealReq {

	@Schema(title = "API id", example = "sy_123")
	@NotEmpty(message = "id不能为空")
	private String id;
}
