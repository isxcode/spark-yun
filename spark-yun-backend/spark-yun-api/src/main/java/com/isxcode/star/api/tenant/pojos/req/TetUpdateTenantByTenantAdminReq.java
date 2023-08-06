package com.isxcode.star.api.tenant.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TetUpdateTenantByTenantAdminReq {

  @Schema(title = "租户唯一id", example = "sy_123456789")
  @NotEmpty(message = "租户id不能为空")
  private String id;

  @Schema(title = "租户简介", example = "这是中国超轻量级大数据平台")
  private String introduce;
}
