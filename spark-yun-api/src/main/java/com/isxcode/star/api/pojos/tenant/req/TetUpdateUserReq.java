package com.isxcode.star.api.pojos.tenant.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class TetUpdateUserReq {

  @Schema(title = "用户id", example = "ispong")
  @NotEmpty(message = "用户id不能为空")
  private String id;

  @Schema(title = "账号", example = "ispong")
  @Size(min = 5, max = 20, message = "账号长度5～20")
  private String account;

  @Schema(title = "密码", example = "ispong123")
  @NotEmpty(message = "密码不能为空")
  @Size(min = 10, max = 30, message = "密码长度10～30")
  private String password;

  @Schema(title = "邮箱", example = "ispong@123.com")
  @Email(message = "格式为邮箱格式")
  private String email;

  @Schema(title = "手机号", example = "1234567890")
  @NotEmpty(message = "手机号不能为空")
  private String phone;

  @Schema(title = "备注", example = "这个人不能删除")
  private String comment;
}
