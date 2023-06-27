package com.isxcode.star.backend.module.user.jwt;

import lombok.Data;

@Data
public class UserJwtEntity {
  private String jwtToken;

  private String userId;

  public UserJwtEntity() {}

  public UserJwtEntity(String userId, String jwtToken) {
    this.jwtToken = jwtToken;
    this.userId = userId;
  }
}
