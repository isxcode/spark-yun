package com.isxcode.star.api.instance.pojos.ao;

import java.util.Date;
import lombok.Data;

@Data
public class WfiWorkflowInstanceAo {

  private String workflowInstanceId;

  private String workflowName;

  private Date startDateTime;

  private Date endDateTime;

  private String status;

  private String type;

  public WfiWorkflowInstanceAo(
      String workflowInstanceId,
      String workflowName,
      Date startDateTime,
      Date endDateTime,
      String status,
      String type) {
    this.workflowInstanceId = workflowInstanceId;
    this.workflowName = workflowName;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.status = status;
    this.type = type;
  }
}
