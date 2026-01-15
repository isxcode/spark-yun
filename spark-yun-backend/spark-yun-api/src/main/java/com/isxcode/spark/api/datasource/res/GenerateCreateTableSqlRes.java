package com.isxcode.spark.api.datasource.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerateCreateTableSqlRes {

    private String createTableSql;
}
