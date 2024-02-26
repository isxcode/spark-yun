package com.isxcode.star.api.monitor.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NodeMonitorInfo {

	private String status;

	private String log;

	private Long usedStorageSize;

	private Long usedMemorySize;

	private Long networkIoReadSpeed;

	private Long networkIoWriteSpeed;

	private Long diskIoReadSpeed;

	private Long diskIoWriteSpeed;

	private Double cpuPercent;

	private String clusterNodeId;

	private String clusterId;

	private String tenantId;

	private LocalDateTime createDateTime;
}
