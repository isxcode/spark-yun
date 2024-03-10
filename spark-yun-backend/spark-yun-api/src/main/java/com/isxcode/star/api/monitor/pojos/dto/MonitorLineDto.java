package com.isxcode.star.api.monitor.pojos.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateMinuteSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MonitorLineDto {

	@Schema(title = "激活节点个数")
	private Long activeNodeSize;

	@Schema(title = "租户id")
	private String tenantId;

	@Schema(title = "当前时间")
	private String dateTime;

	@Schema(title = "cpu占用率%")
	private String cpuPercent;

	@Schema(title = "磁盘io读速度K/s")
	private String diskIoReadSpeed;

	@Schema(title = "网络io读速度K/s")
	private String networkIoReadSpeed;

	@Schema(title = "磁盘io写速度K/s")
	private String diskIoWriteSpeed;

	@Schema(title = "网络io写速度K/s")
	private String networkIoWriteSpeed;

	@Schema(title = "内存使用GB")
	private String usedMemorySize;

	@Schema(title = "存储使用GB")
	private String usedStorageSize;
}