package com.isxcode.star.api.datasource.pojos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryTableDto {

    private String datasourceId;

    private String tableName;

    private String tableComment;
}
