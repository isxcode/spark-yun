package com.isxcode.star.api.tenant.pojos.dto;

import lombok.Data;

@Data
public class UserDto {

    private String name;

    private String account;

    private String status;

    private String role;

    private String createDateTime;
}
