package com.isxcode.star.api.work.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WokAddWorkReq {

  @Schema(title = "作业名称", example = "作业1")
  @NotEmpty(message = "作业名称不能为空")
  private String name;

  @Schema(title = "作业类型", example = "QUERY_SPARK_SQL")
  @NotEmpty(message = "作业类型不能为空")
  private String workType;

  @Schema(title = "工作流唯一id", example = "sy_48c4304593ea4897b6af999e48685896")
  @NotEmpty(message = "工作流id不能为空")
  private String workflowId;

  @Schema(title = "备注", example = "星期一执行的作业")
  private String remark;
}
