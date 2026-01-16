package com.isxcode.spark.api.work.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetWorkVersionInfoReq {

    @Schema(description = "作业版本id", example = "sy_12baf74d710c43a78858e547bf41a586")
    @NotEmpty(message = "作业版本id不能为空")
    private String workVersionId;
}
