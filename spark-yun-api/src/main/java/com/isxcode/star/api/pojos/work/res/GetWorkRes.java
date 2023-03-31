package com.isxcode.star.api.pojos.work.res;

import lombok.Data;

@Data
public class GetWorkRes {

  private String name;

  private String script;

  private String workflowId;

  private String datasourceId;

  private String type;
}
