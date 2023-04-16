package com.isxcode.star.api.pojos.engine.node.req;

import javax.validation.constraints.NotEmpty;

import com.isxcode.star.api.pojos.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EnoQueryNodeReq extends BasePageRequest {

  @Schema(title = "计算引擎唯一id", example = "sy_e4d80a6b561d47afa81504e93054e8e8")
  @NotEmpty(message = "calculateEngineId不能为空")
  private String calculateEngineId;

}
