package com.isxcode.star.api.meta.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.api.work.dto.CronConfig;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageMetaWorkRes {

    private String id;

    private String name;

    private String datasourceName;

    private String datasourceId;

    private String status;

    private String cronConfigStr;

    private String tablePattern;

    private String dbType;

    private String collectType;

    private CronConfig cronConfig;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime nextStartTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;
}
