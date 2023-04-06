package com.isxcode.star.api.pojos.engine.node.req;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EnoAddNodeReq {

  @NotEmpty(message = "引擎id不能为空")
  private String engineId;

  @NotEmpty(message = "host不能为空")
  private String host;

  private String port;

  @NotEmpty(message = "用户名不能为空")
  private String username;

  @NotEmpty(message = "密码不能为空")
  private String password;

  private String comment;

  @NotEmpty(message = "节点名称不能为空")
  private String name;

  private String homePath;
}
