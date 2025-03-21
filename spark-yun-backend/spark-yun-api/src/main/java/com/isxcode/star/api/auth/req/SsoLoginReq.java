package com.isxcode.star.api.auth.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SsoLoginReq {

    @Schema(title = "clientId", example = "admin")
    @NotEmpty(message = "clientId不能为空")
    private String clientId;

    @Schema(title = "code", example = "admin123")
    @NotEmpty(message = "code不能为空")
    private String code;
}
