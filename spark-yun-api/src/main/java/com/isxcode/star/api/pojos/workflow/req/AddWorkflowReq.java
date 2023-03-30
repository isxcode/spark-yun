package com.isxcode.star.api.pojos.workflow.req;

import lombok.Data;

@Data
public class AddWorkflowReq {

  private String name;

  private String comment;

  private String label;
}
