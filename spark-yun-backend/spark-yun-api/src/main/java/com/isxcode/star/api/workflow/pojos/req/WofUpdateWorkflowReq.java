package com.isxcode.star.api.workflow.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WofUpdateWorkflowReq {

  @Schema(title = "作业流唯一id", example = "sy_12345678")
  @NotEmpty(message = "作业流id不能为空")
  private String id;

  @Schema(title = "作业流名称", example = "周报数据总结作业流")
  @NotEmpty(message = "作业流名称不能为空")
  private String name;

  @Schema(title = "备注", example = "所属安全部门，其他人勿动")
  private String remark;
}
