package com.isxcode.spark.api.view.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.spark.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PageViewCardRes {

    private String id;

    private String name;

    private String type;

    private String status;

    private String remark;

    private String datasourceId;

    private String datasourceName;

    private String createBy;

    private String createUsername;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;
}
