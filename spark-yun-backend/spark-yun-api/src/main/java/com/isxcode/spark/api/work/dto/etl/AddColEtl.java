package com.isxcode.spark.api.work.dto.etl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddColEtl {

    /**
     * SOURCE_TABLE、MANUAL .
     */
    private String addType;

    private String colName;

    private String colType;

    private String defaultValue;

    private String remark;

    private String fromAliaCode;

    private String fromColName;
}
