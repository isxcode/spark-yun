package com.isxcode.star.api.work.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 数据同步配置信息.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncRule {

    private String setMode;

    private Integer numPartitions;

    private Integer numConcurrency;

    private String lowerBound;

    private String upperBound;

    private Map<String, String> sqlConfig;

    private String sqlConfigJson;
}
