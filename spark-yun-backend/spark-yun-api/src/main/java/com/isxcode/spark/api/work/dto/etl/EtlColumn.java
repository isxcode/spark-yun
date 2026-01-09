package com.isxcode.spark.api.work.dto.etl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtlColumn {

    private String colName;

    private String fromAliaCode;

    private String fromColName;

    private String remark;

    private String colType;

    private String transformSql;

}

