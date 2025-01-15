package com.isxcode.star.api.monitor.ao;

import lombok.Data;

import java.util.Date;

@Data
public class WorkflowMonitorAo {

    private String workflowInstanceId;

    private String workflowName;

    private Long duration;

    private Date startDateTime;

    private Date endDateTime;

    private String status;

    private String lastModifiedBy;

    public WorkflowMonitorAo(String workflowInstanceId, String workflowName, Long duration, Date startDateTime,
        Date endDateTime, String status, String lastModifiedBy) {
        this.workflowInstanceId = workflowInstanceId;
        this.workflowName = workflowName;
        this.duration = duration;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.lastModifiedBy = lastModifiedBy;
    }
}
