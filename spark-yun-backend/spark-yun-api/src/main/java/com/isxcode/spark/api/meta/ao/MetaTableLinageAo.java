package com.isxcode.spark.api.meta.ao;

import lombok.Data;

@Data
public class MetaTableLinageAo {

    private String fromDbId;

    private String fromTableName;

    private String toDbId;

    private String toTableName;

    public MetaTableLinageAo(String fromDbId, String fromTableName, String toDbId, String toTableName) {
        this.fromDbId = fromDbId;
        this.fromTableName = fromTableName;
        this.toDbId = toDbId;
        this.toTableName = toTableName;
    }
}
