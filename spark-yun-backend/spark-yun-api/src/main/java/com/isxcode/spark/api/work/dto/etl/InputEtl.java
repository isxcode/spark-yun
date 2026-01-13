package com.isxcode.spark.api.work.dto.etl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputEtl {

    private String dbType;

    private String datasourceId;

    private String tableName;

    /**
     * 分区数.
     */
    private Integer numPartitions;

    /**
     * 分区键.
     */
    private String partitionColumn;
}

