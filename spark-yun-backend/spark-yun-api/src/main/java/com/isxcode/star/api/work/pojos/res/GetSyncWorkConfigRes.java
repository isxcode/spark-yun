package com.isxcode.star.api.work.pojos.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetSyncWorkConfigRes {

  private String id;

  private String workId;

  private String sourceDBType;

  private String sourceDBId;

  private String sourceTable;

  private String queryCondition;

  private String targetDBType;

  private String targetDBId;

  private String targetTable;

  private String overMode;

  private Object columMapping;
}
