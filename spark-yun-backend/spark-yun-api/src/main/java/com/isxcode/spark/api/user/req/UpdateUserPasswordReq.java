package com.isxcode.spark.api.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserPasswordReq {

    @Schema(title = "用户id", example = "sy_f8402cd43898421687fcc7c8b98a359c")
    @NotEmpty(message = "用户id不能为空")
    private String userId;

    @Schema(title = "新密码", example = "newPass123")
    @NotEmpty(message = "新密码不能为空")
    @Size(min = 1, max = 100, message = "新密码长度1～100")
    private String newPassword;

    @Schema(title = "确认新密码", example = "newPass123")
    @NotEmpty(message = "确认新密码不能为空")
    @Size(min = 1, max = 100, message = "确认新密码长度1～100")
    private String confirmPassword;
}
