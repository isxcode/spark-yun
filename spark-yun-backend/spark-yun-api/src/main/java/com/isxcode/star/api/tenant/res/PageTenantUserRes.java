package com.isxcode.star.api.tenant.res;

import lombok.Data;

@Data
public class PageTenantUserRes {

    private String id;

    private String username;

    private String account;

    private String status;

    private String roleCode;

    private String createDateTime;

    private String phone;

    private String email;

    private String userId;

    public PageTenantUserRes(String id, String account, String username, String phone, String email, String roleCode,
        String userId) {
        this.id = id;
        this.username = username;
        this.account = account;
        this.roleCode = roleCode;
        this.phone = phone;
        this.email = email;
        this.userId = userId;
    }
}
