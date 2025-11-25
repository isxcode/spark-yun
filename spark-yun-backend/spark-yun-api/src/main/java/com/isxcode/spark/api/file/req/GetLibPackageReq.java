package com.isxcode.spark.api.file.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetLibPackageReq {

    @Schema(title = "依赖包id", example = "sy_id")
    @NotEmpty(message = "id不能为空")
    private String id;
}
