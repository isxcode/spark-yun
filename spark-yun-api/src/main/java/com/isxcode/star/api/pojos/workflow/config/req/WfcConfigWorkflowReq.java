package com.isxcode.star.api.pojos.workflow.config.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WfcConfigWorkflowReq {

  @Schema(title = "工作流id", example = "123")
  @NotEmpty(message = "工作流id不能为空")
  private String workflowId;

  @Schema(title = "工作流配置信息", example = "123")
  private String webConfig;
}
