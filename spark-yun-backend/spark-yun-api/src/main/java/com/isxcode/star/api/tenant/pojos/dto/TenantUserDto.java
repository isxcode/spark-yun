package com.isxcode.star.api.tenant.pojos.dto;

import lombok.Data;

@Data
public class TenantUserDto {

    private String name;

    private String account;

    private String roleCode;

    private String createDateTime;
}
