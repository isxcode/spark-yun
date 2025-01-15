package com.isxcode.star.api.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemMonitorDto {

    @Schema(title = "卡片总数")
    private Long total;

    @Schema(title = "激活数")
    private Long activeNum;
}
