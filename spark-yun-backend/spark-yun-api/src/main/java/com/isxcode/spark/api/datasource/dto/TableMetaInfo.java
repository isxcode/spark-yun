package com.isxcode.spark.api.datasource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableMetaInfo {

    private String catalog;

    private String schema;

    private String tableName;
}
