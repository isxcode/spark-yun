package com.isxcode.star.api.workflow.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RunWorkflowReq {

  @Schema(description = "作业流唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
  @NotEmpty(message = "作业流id不能为空")
  private String workflowId;
}
