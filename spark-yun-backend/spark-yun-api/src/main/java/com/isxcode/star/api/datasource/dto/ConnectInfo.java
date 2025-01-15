package com.isxcode.star.api.datasource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectInfo {

    private String database;

    private String datasourceId;

    private String tablePattern;

    private String tableName;

    private String rowNumber;

    private String driverId;

    private String dbType;

    private String jdbcUrl;

    private String username;

    private String passwd;

    private Integer loginTimeout;
}

