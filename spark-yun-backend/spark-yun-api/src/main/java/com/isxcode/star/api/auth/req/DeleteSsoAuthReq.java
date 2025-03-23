package com.isxcode.star.api.auth.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteSsoAuthReq {

    @Schema(title = "单点登录配置id", example = "sy_123")
    @NotEmpty(message = "id不能为空")
    private String id;
}
