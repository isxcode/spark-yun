package com.isxcode.spark.api.work.dto.etl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtlSqlBody {

    private String sql;

    private String aliaCode;

    private String etlNodeType;

    private List<EtlColumn> columnList;
}
