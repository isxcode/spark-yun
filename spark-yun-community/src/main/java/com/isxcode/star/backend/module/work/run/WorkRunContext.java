package com.isxcode.star.backend.module.work.run;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

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

  private String workName;

  private String workType;

  private String versionId;

  private StringBuilder logBuilder;

  private Map<String, String> sparkConfig;
}
