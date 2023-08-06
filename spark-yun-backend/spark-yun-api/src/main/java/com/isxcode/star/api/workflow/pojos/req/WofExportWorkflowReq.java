package com.isxcode.star.api.workflow.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
public class WofExportWorkflowReq {

  @Schema(title = "作业流唯一id", example = "xxxx")
  private String workflowId;

  @Schema(title = "需要导出的作业id列表", example = "")
  private List<String> workIds;
}
