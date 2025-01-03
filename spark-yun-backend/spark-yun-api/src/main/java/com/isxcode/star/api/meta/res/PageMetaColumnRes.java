package com.isxcode.star.api.meta.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageMetaColumnRes {

    private String datasourceName;

    private String datasourceId;

    private String dbName;

    private String tableName;

    private String columnType;

    private String columnName;

    private String columnComment;

    private String customComment;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastModifiedDateTime;
}
