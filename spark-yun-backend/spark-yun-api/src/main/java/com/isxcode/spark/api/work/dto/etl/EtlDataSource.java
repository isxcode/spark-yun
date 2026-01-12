package com.isxcode.spark.api.work.dto.etl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtlDataSource {

    private String dbType;

    private String url;

    private String user;

    private String driver;

    private String password;

    private String table;

    private String aliaName;

    private Integer numPartitions;

    private String partitionColumn;

}

