package com.isxcode.star.api.datasource.pojos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryColumnDto {

    private String datasourceId;

    private String tableName;

    private String columnName;

    private String columnType;

    private String columnComment;

    private Boolean isPartitionColumn;
}
