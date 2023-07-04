package com.isxcode.star.backend.module.workflow.run;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class WorkRunContext {

  private String instanceId;

  private String datasourceId;

  private String clusterId;

  private String sqlScript;

  private String tenantId;

  private String userId;

  private String workId;

  private String workType;

  private String versionId;

  private StringBuilder logBuilder;

  private Map<String, String> sparkConfig;
}
