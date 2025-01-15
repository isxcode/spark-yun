package com.isxcode.star.api.tenant.req;

import com.isxcode.star.backend.api.base.pojos.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageTenantUserReq extends BasePageRequest {

    @Schema(title = "租户id", example = "sy_f8402cd43898421687fcc7c8b98a359c")
    private String tenantId;
}
