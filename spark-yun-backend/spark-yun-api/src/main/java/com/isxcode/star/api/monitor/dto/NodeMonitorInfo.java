package com.isxcode.star.api.monitor.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NodeMonitorInfo {

    private String status;

    private String log;

    private Double usedStorageSize;

    private Double usedMemorySize;

    private Double networkIoReadSpeed;

    private Double networkIoWriteSpeed;

    private Double diskIoReadSpeed;

    private Double diskIoWriteSpeed;

    private Double cpuPercent;

    private String clusterNodeId;

    private String clusterId;

    private String tenantId;

    private LocalDateTime createDateTime;
}
