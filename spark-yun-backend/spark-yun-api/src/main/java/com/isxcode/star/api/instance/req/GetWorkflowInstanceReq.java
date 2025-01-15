package com.isxcode.star.api.instance.req;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GetWorkflowInstanceReq {

    @NotEmpty(message = "作业流实例id不能为空")
    private String workflowInstanceId;
}
