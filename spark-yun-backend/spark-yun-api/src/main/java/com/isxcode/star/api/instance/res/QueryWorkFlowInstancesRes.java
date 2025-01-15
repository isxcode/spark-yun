package com.isxcode.star.api.instance.res;

import lombok.Data;

@Data
public class QueryWorkFlowInstancesRes {

    private String workflowInstanceId;

    private String workflowName;

    private String startDateTime;

    private String endDateTime;

    private String status;

    private String type;

    private Long duration;

    private String nextPlanDateTime;

    private String planStartDateTime;
}
