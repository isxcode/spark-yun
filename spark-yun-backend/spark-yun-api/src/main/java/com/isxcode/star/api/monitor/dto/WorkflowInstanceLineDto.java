package com.isxcode.star.api.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowInstanceLineDto {

    @Schema(title = "运行中的数量")
    private Long runningNum;

    @Schema(title = "成功的数量")
    private Long successNum;

    @Schema(title = "失败的数量")
    private Long failNum;

    @Schema(title = "当前时间")
    private String localTime;
}
