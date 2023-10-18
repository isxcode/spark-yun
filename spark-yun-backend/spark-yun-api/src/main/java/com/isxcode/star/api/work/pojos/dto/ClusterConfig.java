package com.isxcode.star.api.work.pojos.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

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

	private Map<String, String> sparkConfig;
}