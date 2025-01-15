package com.isxcode.star.api.workflow.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class KillWorkflowReq {

    @Schema(description = "作业流唯一id", example = "sy_344c3d583fa344f7a2403b19c5a654dc")
    private String workflowId;
}
