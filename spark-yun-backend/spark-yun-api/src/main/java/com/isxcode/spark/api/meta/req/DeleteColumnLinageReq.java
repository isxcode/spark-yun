package com.isxcode.spark.api.meta.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteColumnLinageReq {

    @Schema(title = "血缘id", example = "")
    @NotEmpty(message = "lineageId不能为空")
    private String lineageId;
}
