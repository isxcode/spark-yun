package com.isxcode.star.api.container.pojo.req;

import com.isxcode.star.backend.api.base.pojos.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
public class StartContainerReq {

  @Schema(title = "containerId", example = "sy_213")
  @NotEmpty(message = "id不能为空")
  private String id;
}
