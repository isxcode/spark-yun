package com.isxcode.star.api.pojos.workflow.res;

import lombok.Data;

@Data
public class QueryWorkflowRes {

  private String id;

  private String name;

  private String comment;

  private String status;

  private String label;

  private Integer workNum;
}
