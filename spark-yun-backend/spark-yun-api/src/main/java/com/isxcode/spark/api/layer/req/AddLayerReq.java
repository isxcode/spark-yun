package com.isxcode.spark.api.layer.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddLayerReq {

    @Schema(title = "数据分层名称", example = "123")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "表名规范", example = "ods_*")
    private String tableRule;

    @Schema(title = "父级分层id", example = "123")
    private String parentLayerId;

    @Schema(title = "备注", example = "备注123")
    private String remark;
}
