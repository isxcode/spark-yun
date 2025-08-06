package com.isxcode.spark.api.tenant.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetTenantRes {

    private String id;

    private String name;
}
