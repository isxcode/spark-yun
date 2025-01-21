package com.isxcode.star.api.view.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetViewLinkInfoReq {

    @Schema(title = "大屏链接id", example = "sy_123")
    @NotEmpty(message = "viewLinkId不能为空")
    private String viewLinkId;
}
