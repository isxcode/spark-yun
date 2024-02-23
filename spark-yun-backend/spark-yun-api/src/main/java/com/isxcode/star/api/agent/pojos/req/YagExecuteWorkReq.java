package com.isxcode.star.api.agent.pojos.req;

import lombok.Data;

import java.util.List;

@Data
public class YagExecuteWorkReq {

	private PluginReq pluginReq;

	private SparkSubmit sparkSubmit;

	private String agentHomePath;

	private String agentType;

	private String sparkHomePath;

	private String[] args;

	private String argsStr;

	/**
	 * 作业类型.
	 */
	private String workType;

  /**
   * 依赖配置.
   */
  private List<String> libConfig;
}
