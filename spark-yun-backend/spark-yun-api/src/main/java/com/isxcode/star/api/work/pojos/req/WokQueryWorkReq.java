package com.isxcode.star.api.work.pojos.req;

import com.isxcode.star.backend.api.base.pojos.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WokQueryWorkReq extends BasePageRequest {

  @Schema(title = "作业流唯一id", example = "sy_48c4304593ea4897b6af999e48685896")
  @NotEmpty(message = "作业流id不能为空")
  private String workflowId;
}
