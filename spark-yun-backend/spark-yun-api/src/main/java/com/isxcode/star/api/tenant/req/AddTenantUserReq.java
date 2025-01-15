package com.isxcode.star.api.tenant.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddTenantUserReq {

    @Schema(title = "用户id", example = "ispong")
    @NotEmpty(message = "用户id不能为空")
    private String userId;

    @Schema(title = "是否为租户管理者", example = "false")
    private Boolean isTenantAdmin;

    @Schema(title = "备注", example = "ispong")
    private String remark;

    @Schema(title = "租户id", example = "sy_123")
    private String tenantId;
}
