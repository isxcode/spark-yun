package com.isxcode.spark.api.model.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.spark.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ColumnFormatPageRes {

    private String id;

    private String name;

    private String columnTypeCode;

    private String columnType;

    private String columnRule;

    private String remark;

    private String status;

    private String defaultValue;

    private String isNull;

    private String isDuplicate;

    private String isPartition;

    private String isPrimary;

    private String createBy;

    private String createUsername;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastModifiedDateTime;
}
