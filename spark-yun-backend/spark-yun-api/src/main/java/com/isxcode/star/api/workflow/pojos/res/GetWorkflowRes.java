package com.isxcode.star.api.workflow.pojos.res;

import com.isxcode.star.api.work.pojos.dto.CronConfig;
import lombok.Data;

@Data
public class GetWorkflowRes {

	private Object webConfig;

	private CronConfig cronConfig;

	private String invokeStatus;

	private String invokeUrl;
}
