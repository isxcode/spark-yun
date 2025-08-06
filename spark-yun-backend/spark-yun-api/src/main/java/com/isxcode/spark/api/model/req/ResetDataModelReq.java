package com.isxcode.spark.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ResetDataModelReq {

    @Schema(title = "数据模型id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
    @NotEmpty(message = "id不能为空")
    private String id;
}
