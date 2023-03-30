package com.isxcode.star.api.pojos.datasource.req;

import lombok.Data;

@Data
public class AddDatasourceReq {

  private String name;

  private String jdbcUrl;

  private String username;

  private String password;

  private String comment;

  private String type;
}
