package com.isxcode.star.api.monitor.pojos.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateMinuteSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MonitorLineDto {

	private Long activeNodeSize;

	private String tenantId;

	@JsonSerialize(using = LocalDateMinuteSerializer.class)
	private LocalDateTime dateTime;

	private Double cpuPercent;

	private Long diskIoReadSpeed;

	private Long networkIoReadSpeed;

	private Long diskIoWriteSpeed;

	private Long networkIoWriteSpeed;

	private Long usedMemorySize;

	private Long usedStorageSize;

	public MonitorLineDto(Long activeNodeSize, String tenantId, LocalDateTime dateTime, Double cpuPercent,
			Long diskIoReadSpeed, Long networkIoReadSpeed, Long diskIoWriteSpeed, Long networkIoWriteSpeed,
			Long usedMemorySize, Long usedStorageSize) {

		this.activeNodeSize = activeNodeSize;
		this.tenantId = tenantId;
		this.dateTime = dateTime;
		this.cpuPercent = cpuPercent;
		this.diskIoReadSpeed = diskIoReadSpeed;
		this.networkIoReadSpeed = networkIoReadSpeed;
		this.diskIoWriteSpeed = diskIoWriteSpeed;
		this.networkIoWriteSpeed = networkIoWriteSpeed;
		this.usedMemorySize = usedMemorySize;
		this.usedStorageSize = usedStorageSize;
	}
}