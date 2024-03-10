package com.isxcode.star.api.container.pojo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StopContainerReq {

	@Schema(title = "containerId", example = "sy_213")
	@NotEmpty(message = "id不能为空")
	private String id;
}
