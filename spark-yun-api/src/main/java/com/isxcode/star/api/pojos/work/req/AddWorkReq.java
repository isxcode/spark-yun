package com.isxcode.star.api.pojos.work.req;

import lombok.Data;

@Data
public class AddWorkReq {

  private String name;

  private String type;

  private String workflowId;

  private String comment;

  private String label;
}
