package com.isxcode.star.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CopyDataModelReq {

    @Schema(title = "数据模型id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
    @NotEmpty(message = "modelId不能为空")
    private String modelId;

    @Schema(title = "数据模型名称", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "数据分层id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
    @NotEmpty(message = "layerId不能为空")
    private String layerId;

    @Schema(title = "表名", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
    @NotEmpty(message = "tableName不能为空")
    private String tableName;
}
