package com.isxcode.spark.api.tenant.res;

import lombok.Data;

@Data
public class QueryUserTenantRes {

    private boolean isCurrentTenant;

    private String id;

    private String name;
}
