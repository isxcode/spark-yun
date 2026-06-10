package com.isxcode.spark.api.real.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class QueryArrayPathReq {

    @Schema(title = "json", example = "{}")
    @NotEmpty(message = "jsonStr不能为空")
    private String jsonStr;
}
