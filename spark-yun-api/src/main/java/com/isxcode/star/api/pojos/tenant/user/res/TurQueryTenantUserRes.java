package com.isxcode.star.api.pojos.tenant.user.res;

import lombok.Data;

@Data
public class TurQueryTenantUserRes {

  private String id;

  private String username;

  private String account;

  private String status;

  private String roleCode;

  private String createDateTime;

  private String phone;

  private String email;
}
