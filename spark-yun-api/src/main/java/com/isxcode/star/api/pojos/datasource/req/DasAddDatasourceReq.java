package com.isxcode.star.api.pojos.datasource.req;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DasAddDatasourceReq {

  @NotEmpty(message = "数据源名称不能为空")
  private String name;

  @NotEmpty(message = "地址不能为空")
  private String jdbcUrl;

  @NotEmpty(message = "用户名不能为空")
  private String username;

  @NotEmpty(message = "密码不能为空")
  private String password;

  private String comment;

  @NotEmpty(message = "数据源类型不能为空")
  private String type;
}
