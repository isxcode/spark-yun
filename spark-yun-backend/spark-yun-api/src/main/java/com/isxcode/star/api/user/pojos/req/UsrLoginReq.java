package com.isxcode.star.api.user.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UsrLoginReq {

  @Schema(title = "账号", example = "admin")
  @NotEmpty(message = "账号不能为空")
  private String account;

  @Schema(title = "密码", example = "admin123")
  @NotEmpty(message = "密码不能为空")
  private String passwd;
}
