package com.isxcode.star.api.pojos.tenant.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class TetUpdateTenantBySystemAdminReq extends TetAddTenantReq {

  @Schema(title = "租户唯一id", example = "sy_123456789")
  @NotEmpty(message = "租户id不能为空")
  private String id;
}
