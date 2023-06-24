package com.isxcode.star.api.pojos.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UsrAddUserReq {

  @Schema(title = "名称", example = "ispong")
  @NotEmpty(message = "名称不能为空")
  @Size(min = 1, max = 100, message = "名称长度5～100")
  private String username;

  @Schema(title = "账号", example = "ispong")
  @NotEmpty(message = "账号不能为空")
  @Size(min = 1, max = 100, message = "账号长度5～100")
  private String account;

  @Schema(title = "密码", example = "ispong123")
  @NotEmpty(message = "密码不能为空")
  @Size(min = 1, max = 100, message = "密码长度5～100")
  private String passwd;

  @Schema(title = "邮箱", example = "ispong@123.com")
  @Email(message = "邮箱不合法")
  private String email;

  @Schema(title = "手机号", example = "1234567890")
  private String phone;

  @Schema(title = "备注", example = "这个人不能删除")
  private String remark;
}
