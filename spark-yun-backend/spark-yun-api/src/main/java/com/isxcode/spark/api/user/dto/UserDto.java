package com.isxcode.spark.api.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.spark.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {

    private String id;

    private String username;

    private String account;

    private String status;

    private String createDateTime;

    private String phone;

    private String email;

    private String remark;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime validStartDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime validEndDateTime;
}
