package com.isxcode.star.api.pojos.tenant.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class TurAddTenantUserReq {

  @Schema(title = "用户id", example = "ispong")
  @NotEmpty(message = "用户id不能为空")
  private String userId;

  @Schema(title = "是否为租户管理者", example = "false")
  private boolean isTenantAdmin;

  @Schema(title = "备注", example = "ispong")
  private String remark;
}
