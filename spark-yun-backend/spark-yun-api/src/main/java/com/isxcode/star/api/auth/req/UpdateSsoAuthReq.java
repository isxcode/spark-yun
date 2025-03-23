package com.isxcode.star.api.auth.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateSsoAuthReq {

    @Schema(title = "sso认证id", example = "123")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "sso认证名称", example = "测试接口")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "clientId", example = "123")
    @NotEmpty(message = "clientId不能为空")
    private String clientId;

    @Schema(title = "clientSecret", example = "123")
    @NotEmpty(message = "clientSecret不能为空")
    private String clientSecret;

    @Schema(title = "scope", example = "123")
    @NotEmpty(message = "scope不能为空")
    private String scope;

    @Schema(title = "authUrl", example = "123")
    @NotEmpty(message = "authUrl不能为空")
    private String authUrl;

    @Schema(title = "accessTokenUrl", example = "123")
    @NotEmpty(message = "accessTokenUrl不能为空")
    private String accessTokenUrl;

    @Schema(title = "redirectUrl", example = "123")
    @NotEmpty(message = "redirectUrl不能为空")
    private String redirectUrl;

    @Schema(title = "userUrl", example = "123")
    @NotEmpty(message = "userUrl不能为空")
    private String userUrl;

    @Schema(title = "authJsonPath", example = "123")
    @NotEmpty(message = "authJsonPath不能为空")
    private String authJsonPath;

    @Schema(title = "remark", example = "123")
    private String remark;
}
