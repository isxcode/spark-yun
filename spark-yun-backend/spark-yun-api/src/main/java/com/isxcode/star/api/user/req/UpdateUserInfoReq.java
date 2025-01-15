package com.isxcode.star.api.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UpdateUserInfoReq {

    @Schema(title = "名称", example = "ispong")
    @NotEmpty(message = "名称不能为空")
    @Size(min = 1, max = 100, message = "名称长度5～100")
    private String username;

    @Schema(title = "邮箱", example = "ispong@123.com")
    @Email(message = "邮箱不合法")
    private String email;

    @Schema(title = "手机号", example = "1234567890")
    private String phone;

    @Schema(title = "备注", example = "这个人不能删除")
    private String remark;
}
