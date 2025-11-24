package com.isxcode.spark.api.tenant.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.spark.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageTenantRes {

    private String id;

    private String name;

    private String usedMemberNum;

    private String maxMemberNum;

    private String usedWorkflowNum;

    private String maxWorkflowNum;

    private String remark;

    private String status;

    private String checkDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime validStartDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime validEndDateTime;
}
