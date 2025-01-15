package com.isxcode.star.api.form.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetFormLinkInfoReq {

    @Schema(title = "表单链接id", example = "sy_123")
    @NotEmpty(message = "formLinkId不能为空")
    private String formLinkId;
}
