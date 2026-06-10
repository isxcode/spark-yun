package com.isxcode.spark.api.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RefreshTokenReq {

    @Schema(title = "刷新token")
    @NotEmpty(message = "刷新token不能为空")
    private String refreshToken;
}
