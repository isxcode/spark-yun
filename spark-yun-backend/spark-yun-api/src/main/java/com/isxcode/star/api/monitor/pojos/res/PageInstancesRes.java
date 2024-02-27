package com.isxcode.star.api.monitor.pojos.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PageInstancesRes {

	private String workflowInstanceId;

	private String workflowName;

	private Long duration;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime startDateTime;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime endDateTime;

	private String status;

	private String lastModifiedBy;
}
