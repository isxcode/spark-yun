package com.isxcode.star.api.work.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WokGetDataReq {

  @Schema(title = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
  @NotEmpty(message = "作业id不能为空")
  private String workId;

  @Schema(title = "作业提交返回的容器id", example = "application_1680753912057_0019")
  private String applicationId;
}
