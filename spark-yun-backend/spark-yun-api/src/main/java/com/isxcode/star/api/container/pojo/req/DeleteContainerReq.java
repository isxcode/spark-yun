package com.isxcode.star.api.container.pojo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteContainerReq {

  @Schema(title = "containerId", example = "sy_213")
  @NotEmpty(message = "id不能为空")
  private String id;
}
