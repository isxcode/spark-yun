package com.isxcode.star.api.pojos.user.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsrLoginRes {

  private String username;

  private String token;

  private String tenantId;

  private String tenantName;

  private String role;
}
