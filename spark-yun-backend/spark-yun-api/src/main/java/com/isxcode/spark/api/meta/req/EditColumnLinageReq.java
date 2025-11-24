package com.isxcode.spark.api.meta.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class EditColumnLinageReq {

    @Schema(title = "血缘id", example = "")
    @NotEmpty(message = "lineageId不能为空")
    private String lineageId;

    @Schema(title = "血缘备注", example = "")
    private String remark;
}
