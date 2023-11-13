package com.isxcode.star.api.work.pojos.res;

import com.isxcode.star.api.work.pojos.dto.SyncColumnInfo;
import com.isxcode.star.api.work.pojos.dto.SyncColumnMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetSyncWorkConfigRes {

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
