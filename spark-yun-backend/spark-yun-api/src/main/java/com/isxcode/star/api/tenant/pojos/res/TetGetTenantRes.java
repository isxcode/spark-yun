package com.isxcode.star.api.tenant.pojos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TetGetTenantRes {

  private String id;

  private String name;
}
