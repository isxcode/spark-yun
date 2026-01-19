package com.isxcode.spark.api.file.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddLibPackageReq {

    @Schema(title = "依赖包名称", example = "名称")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "备注", example = "备注内容")
    private String remark;
}
