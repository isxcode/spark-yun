package com.isxcode.spark.api.work.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 调度配置信息.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatasourceConfig {

    private String driver;

    private String url;

    private String dbTable;

    private String user;

    private String password;

    private Map<String, String> connectConfig;
}
