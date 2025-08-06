package com.isxcode.spark.api.layer.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateLayerReq {

    @Schema(title = "数据分层id", example = "123")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "数据分层名称", example = "123")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "表名规范", example = "ods_*")
    private String tableRule;

    @Schema(title = "备注", example = "备注123")
    private String remark;
}
