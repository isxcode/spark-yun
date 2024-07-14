package com.isxcode.star.api.workflow.pojos.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowToken {

	private String userId;

	private String tenantId;

	private String type;

	private String workflowId;
}
