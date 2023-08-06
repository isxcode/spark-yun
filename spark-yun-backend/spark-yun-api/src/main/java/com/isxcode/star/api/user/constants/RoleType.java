package com.isxcode.star.api.user.constants;

/** 角色. */
public interface RoleType {

  /** 系统管理员权限. */
  String SYS_ADMIN = "ROLE_SYS_ADMIN";

  /** 租户管理员权限. */
  String TENANT_ADMIN = "ROLE_TENANT_ADMIN";

  /** 租户成员权限. */
  String TENANT_MEMBER = "ROLE_TENANT_MEMBER";

  /** 普通成员. */
  String NORMAL_MEMBER = "ROLE_NORMAL_MEMBER";
}
