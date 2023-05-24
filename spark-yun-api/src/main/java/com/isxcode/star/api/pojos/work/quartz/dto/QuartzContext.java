package com.isxcode.star.api.pojos.work.quartz.dto;

import lombok.Data;

@Data
public class QuartzContext {

  private String workId;

  private String versionId;

  private String workType;

  private String datasourceId;

  private String clusterId;

  private String corn;

  private String sqlScript;

  private String userId;

  private String tenantId;

  private String sparkConfig;
}
