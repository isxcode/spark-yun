package com.isxcode.star.api.work.pojos.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 数据同步配置信息.
 */
@Data
@Builder
public class SyncRule {

	private String setMode;

	private Integer numPartitions;

	private Integer numConcurrency;

	private String sqlConfig;
}