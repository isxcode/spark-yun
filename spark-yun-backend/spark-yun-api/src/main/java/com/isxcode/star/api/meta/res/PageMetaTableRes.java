package com.isxcode.star.api.meta.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageMetaTableRes {

    private String id;

    private String datasourceName;

    private String datasourceId;

    private String tableName;

    private String tableCommand;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastModifiedDateTime;
}
