package com.isxcode.star.api.tenant.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateTenantForTenantAdminReq {

    @Schema(title = "租户唯一id", example = "sy_123456789")
    @NotEmpty(message = "租户id不能为空")
    private String id;

    @Schema(title = "租户简介", example = "企业级大数据计算平台")
    private String introduce;
}
