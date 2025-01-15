package com.isxcode.star.api.datasource.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.isxcode.star.api.datasource.dto.KafkaConfig;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageDatasourceRes {

    private String name;

    private String id;

    private String jdbcUrl;

    private String username;

    private String remark;

    private String status;

    private String checkDateTime;

    private String dbType;

    private String connectLog;

    private String driverId;

    private String driverName;

    private String metastoreUris;

    private String kafkaConfigStr;

    private KafkaConfig kafkaConfig;
}
