package com.isxcode.star.api.pojos.user.req;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Data;

/** AddUser接口请求对象. */
@Data
public class AddUserReq {

  @Size(min = 5, max = 20, message = "账号长度5～20")
  private String account;

  @NotEmpty(message = "密码不能为空")
  @Size(min = 10, max = 30, message = "密码长度10～30")
  private String password;

  @Email(message = "格式为邮箱格式")
  private String email;
}
