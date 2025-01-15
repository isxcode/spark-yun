package com.isxcode.star.api.datasource.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecurityColumnDto {

    /**
     * 字段名.
     */
    private String name;

    /**
     * 字段值.
     */
    private Object value;

    /**
     * 字段类型.
     */
    private String type;
}
