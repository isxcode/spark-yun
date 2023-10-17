package com.isxcode.star.api.work.pojos.dto;

import lombok.Data;

import java.util.List;

@Data
public class SyncWorkConfig {

	private String workId;

	private String sourceDBType;

	private String sourceDBId;

	private String sourceTable;

	private String partitionColumn;

	private String queryCondition;

	private String targetDBType;

	private String targetDBId;

	private String targetTable;

	private String overMode;

	private List<SyncColumnInfo> sourceTableColumn;

	private List<SyncColumnInfo> targetTableColumn;

	private List<SyncColumnMap> columnMap;
}