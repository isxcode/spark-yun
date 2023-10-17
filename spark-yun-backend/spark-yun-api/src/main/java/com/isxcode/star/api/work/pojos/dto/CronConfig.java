package com.isxcode.star.api.work.pojos.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 调度配置信息.
 */
@Data
@Builder
public class CronConfig {

	private String setMode;

	private boolean enable;

	private String startData;

	private String cron;
}