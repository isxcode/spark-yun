package com.isxcode.star.api.pojos.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UsrUpdateUserReq extends UsrAddUserReq {

  @Schema(title = "用户id", example = "sy_f8402cd43898421687fcc7c8b98a359c")
  @NotEmpty(message = "用户id不能为空")
  private String id;

}
