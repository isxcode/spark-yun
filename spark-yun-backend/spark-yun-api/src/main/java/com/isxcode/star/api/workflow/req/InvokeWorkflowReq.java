package com.isxcode.star.api.workflow.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class InvokeWorkflowReq {

    @Schema(description = "作业流唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
    @NotEmpty(message = "作业流id不能为空")
    private String workflowId;

    @Schema(description = "token", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
    @NotEmpty(message = "token不能为空")
    private String token;
}
