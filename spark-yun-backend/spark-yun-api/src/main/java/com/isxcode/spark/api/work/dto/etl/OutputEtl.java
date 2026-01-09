package com.isxcode.spark.api.work.dto.etl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutputEtl {

    private String dbType;

    private String datasourceId;

    private String tableName;

    private String writeMode;

    private List<Map<String, String>> colMapping;

    private List<EtlColumn> fromColumnList;

    private List<EtlColumn> toColumnList;
}

