package com.isxcode.star.api.pojos.user.req;

import lombok.Data;

@Data
public class LoginReq {

  private String account;

  private String password;
}
