package com.isxcode.spark.api.real.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.spark.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PageRealRes {

    private String id;

    private String name;

    private String status;

    private String remark;

    private String clusterId;

    private String clusterName;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    private String createBy;

    private String createUsername;
}
