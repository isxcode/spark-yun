package com.isxcode.star.api.pojos.user.req;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UsrLoginReq {

  @NotEmpty(message = "账号不能为空")
  private String account;

  @NotEmpty(message = "密码不能为空")
  private String passwd;
}
