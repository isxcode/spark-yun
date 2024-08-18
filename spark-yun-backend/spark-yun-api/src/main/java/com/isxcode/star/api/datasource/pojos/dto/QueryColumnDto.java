package com.isxcode.star.api.datasource.pojos.dto;

import lombok.Data;

@Data
public class QueryColumnDto {

    private String datasourceId;

    private String tableName;

    private String columnName;

    private String columnType;

    private String columnComment;
}
