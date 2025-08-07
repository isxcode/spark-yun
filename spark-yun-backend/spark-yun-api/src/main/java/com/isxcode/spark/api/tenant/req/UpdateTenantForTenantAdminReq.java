package com.isxcode.spark.api.tenant.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateTenantForTenantAdminReq {

    @Schema(title = "租户唯一id", example = "sy_123456789")
    @NotEmpty(message = "租户id不能为空")
    private String id;

    @Schema(title = "租户简介", example = "超轻量级智能化大数据中心")
    private String introduce;
}
