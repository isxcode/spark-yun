package com.isxcode.star.api.work.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteUdfReq {

	@Schema(title = "udf唯一id", example = "sy_123456789")
	@NotEmpty(message = "udf的id不能为空")
	private String udfId;
}
