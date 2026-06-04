package com.isxcode.spark.api.tenant.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChooseTenantRes {

    private String token;

    private String tenantId;

    private String role;
}
