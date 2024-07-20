package com.isxcode.star.api.monitor.pojos.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PageInstancesRes {

    @Schema(title = "作业流实例id")
    private String workflowInstanceId;

    @Schema(title = "作业流名称")
    private String workflowName;

    @Schema(title = "耗时")
    private Long duration;

    @Schema(title = "开始时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDateTime;

    @Schema(title = "结束时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDateTime;

    @Schema(title = "实例状态")
    private String status;

    @Schema(title = "最新操作人")
    private String lastModifiedBy;
}
