package com.isxcode.star.api.user.pojos.req;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

/** getUser请求体. */
@Data
public class GetUserReq {

  @NotEmpty(message = "id不能为空")
  private String id;
}
