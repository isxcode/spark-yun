package com.isxcode.star.api.work.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetWorkflowDefaultClusterReq {

    @Schema(title = "作业流唯一id", example = "sy_123456789")
    @NotEmpty(message = "作业流id不能为空")
    private String workflowId;
}
