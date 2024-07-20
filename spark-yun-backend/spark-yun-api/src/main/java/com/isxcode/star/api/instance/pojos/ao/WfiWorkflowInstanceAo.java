package com.isxcode.star.api.instance.pojos.ao;

import java.util.Date;
import lombok.Data;

@Data
public class WfiWorkflowInstanceAo {

    private String workflowInstanceId;

    private String workflowName;

    private Long duration;

    private Date nextPlanDateTime;

    private Date planStartDateTime;

    private Date startDateTime;

    private Date endDateTime;

    private String status;

    public WfiWorkflowInstanceAo(String workflowInstanceId, String workflowName, Long duration, Date nextPlanDateTime,
        Date planStartDateTime, Date startDateTime, Date endDateTime, String status, String type) {
        this.workflowInstanceId = workflowInstanceId;
        this.workflowName = workflowName;
        this.duration = duration;
        this.nextPlanDateTime = nextPlanDateTime;
        this.planStartDateTime = planStartDateTime;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.type = type;
    }

    private String type;

}
