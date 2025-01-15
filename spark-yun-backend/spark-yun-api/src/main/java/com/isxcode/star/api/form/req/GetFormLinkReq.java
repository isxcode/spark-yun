package com.isxcode.star.api.form.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class GetFormLinkReq {

    @Schema(title = "表单id", example = "sy_123")
    @NotEmpty(message = "formId不能为空")
    private String formId;

    @Schema(title = "有效天数", description = "1")
    @NotNull(message = "validDay不能为空")
    private Integer validDay;

}
