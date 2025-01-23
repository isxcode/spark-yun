package com.isxcode.star.api.view.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class GetViewLinkReq {

    @Schema(title = "大屏id", example = "sy_123")
    @NotEmpty(message = "viewId不能为空")
    private String viewId;

    @Schema(title = "有效天数", description = "1")
    @NotNull(message = "validDay不能为空")
    private Integer validDay;

}
