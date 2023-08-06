package com.isxcode.star.api.cluster.pojos.req;

import com.isxcode.star.backend.api.base.pojos.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EnoQueryNodeReq extends BasePageRequest {

  @Schema(title = "计算引擎唯一id", example = "sy_e4d80a6b561d47afa81504e93054e8e8")
  @NotEmpty(message = "clusterId不能为空")
  private String clusterId;
}
