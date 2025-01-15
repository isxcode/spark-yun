package com.isxcode.star.api.form.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateFormReq {

    @Schema(title = "formId", example = "sy_213")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "表单名称", example = "测试表单")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "备注", example = "备注123")
    private String remark;
}
