package com.isxcode.star.api.instance.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteWorkflowInstanceReq {

    @NotEmpty(message = "作业流实例id不能为空")
    private String workflowInstanceId;
}
