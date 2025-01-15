package com.isxcode.star.api.workflow.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateMinuteSerializer;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageWorkflowRes {

    private String id;

    private String name;

    private String remark;

    private String status;

    private String defaultClusterId;

    private String clusterName;

    @JsonSerialize(using = LocalDateMinuteSerializer.class)
    private LocalDateTime nextDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    private String createBy;

    private String createUsername;
}
