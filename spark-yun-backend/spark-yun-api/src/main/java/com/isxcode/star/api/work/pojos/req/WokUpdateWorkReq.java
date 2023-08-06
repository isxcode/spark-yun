package com.isxcode.star.api.work.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WokUpdateWorkReq {

  @Schema(title = "作业唯一id", example = "sy_123456789")
  @NotEmpty(message = "作业id不能为空")
  private String id;

  @Schema(title = "作业名称", example = "作业1")
  @NotEmpty(message = "作业名称不能为空")
  private String name;

  @Schema(title = "作业类型", example = "QUERY_SPARK_SQL")
  private String workType;

  @Schema(title = "备注", example = "星期一执行的作业")
  private String remark;
}
