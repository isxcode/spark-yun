package com.isxcode.star.api.monitor.res;

import com.isxcode.star.api.monitor.dto.SystemMonitorDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetSystemMonitorRes {

    @Schema(title = "集群监控信息")
    private SystemMonitorDto clusterMonitor;

    @Schema(title = "数据源监控信息")
    private SystemMonitorDto datasourceMonitor;

    @Schema(title = "作业监控信息")
    private SystemMonitorDto workflowMonitor;

    @Schema(title = "接口监控信息")
    private SystemMonitorDto apiMonitor;
}
