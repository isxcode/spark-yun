package com.isxcode.star.api.monitor.pojos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowInstanceLineDto {

	private Long runningNum;

	private Long successNum;

	private Long failNum;
}
