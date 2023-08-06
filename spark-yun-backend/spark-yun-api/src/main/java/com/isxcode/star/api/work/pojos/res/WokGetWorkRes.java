package com.isxcode.star.api.work.pojos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WokGetWorkRes {

  private String name;

  private String sqlScript;

  private String workflowId;

  private String datasourceId;

  private String clusterId;

  private String workType;

  private String workId;

  private String corn;

  private String sparkConfig;
}
