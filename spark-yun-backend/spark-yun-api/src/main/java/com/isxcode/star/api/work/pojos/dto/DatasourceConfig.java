package com.isxcode.star.api.work.pojos.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 调度配置信息.
 */
@Data
@Builder
public class DatasourceConfig {

	private String driver;

	private String url;

	private String dbTable;

	private String user;

	private String password;
}