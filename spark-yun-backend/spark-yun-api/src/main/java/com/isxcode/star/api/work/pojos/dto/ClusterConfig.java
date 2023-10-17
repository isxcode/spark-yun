package com.isxcode.star.api.work.pojos.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 资源配置信息.
 */
@Data
@Builder
public class ClusterConfig {

	private String setMode;

	private String resourceLevel;

	private String clusterId;

	private String clusterNodeId;

	private String sparkConfig;
}