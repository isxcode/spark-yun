package com.isxcode.spark.api.meta.ao;

import lombok.Data;

@Data
public class MetaDbLinageAo {

    private String fromDbId;

    private String toDbId;

    public MetaDbLinageAo(String fromDbId, String toDbId) {
        this.fromDbId = fromDbId;
        this.toDbId = toDbId;
    }
}
