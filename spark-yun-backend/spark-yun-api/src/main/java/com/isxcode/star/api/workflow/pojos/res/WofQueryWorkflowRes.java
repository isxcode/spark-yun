package com.isxcode.star.api.workflow.pojos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WofQueryWorkflowRes {

  private String id;

  private String name;

  private String remark;

  private String status;
}
