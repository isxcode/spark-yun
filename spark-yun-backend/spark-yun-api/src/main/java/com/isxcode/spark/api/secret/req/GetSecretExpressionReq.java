package com.isxcode.spark.api.secret.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetSecretExpressionReq {

    @Schema(title = "id", example = "id")
    @NotEmpty(message = "id不能为空")
    private String id;
}
