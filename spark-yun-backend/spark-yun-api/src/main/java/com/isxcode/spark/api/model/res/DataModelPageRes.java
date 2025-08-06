package com.isxcode.spark.api.model.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.spark.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DataModelPageRes {

    private String id;

    private String name;

    private String layerId;

    private String layerName;

    private String modelType;

    private String dbType;

    private String datasourceId;

    private String datasourceName;

    private String tableName;

    private String status;

    private String remark;

    private String buildLog;

    private String createBy;

    private String createUsername;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastModifiedDateTime;
}
