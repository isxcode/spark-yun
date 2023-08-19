package com.isxcode.star.api.work.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DeleteWorkReq {

  @Schema(title = "作业唯一id", example = "sy_123456789")
  @NotEmpty(message = "作业id不能为空")
  private String workId;
}
