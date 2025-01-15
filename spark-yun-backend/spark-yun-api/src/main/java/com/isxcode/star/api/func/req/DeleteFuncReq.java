package com.isxcode.star.api.func.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteFuncReq {

    @Schema(title = "udf唯一id", example = "sy_123456789")
    @NotEmpty(message = "自定义函数id不能为空")
    private String id;
}
