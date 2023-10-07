package com.isxcode.star.api.user.pojos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRes {

	private String username;

  private String phone;

  private String email;

  private String remark;

	private String token;

	private String tenantId;

	private String role;
}
