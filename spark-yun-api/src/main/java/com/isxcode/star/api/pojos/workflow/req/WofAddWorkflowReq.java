package com.isxcode.star.api.pojos.workflow.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class WofAddWorkflowReq {

  @Schema(title = "作业流名称", example = "周报数据总结作业流")
  @NotEmpty(message = "作业流名称不能为空")
  private String name;

  @Schema(title = "备注", example = "所属安全部门，其他人勿动")
  private String remark;
}
