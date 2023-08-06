package com.isxcode.star.api.tenant.pojos.res;

import lombok.Data;

@Data
public class TetQueryUserTenantRes {

  private boolean isCurrentTenant;

  private String id;

  private String name;
}
