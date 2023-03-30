package com.isxcode.star.api.pojos.node.req;

import lombok.Data;

@Data
public class AddNodeReq {

  private String engineId;

  private String host;

  private String port;

  private String username;

  private String password;

  private String comment;

  private String name;
}
