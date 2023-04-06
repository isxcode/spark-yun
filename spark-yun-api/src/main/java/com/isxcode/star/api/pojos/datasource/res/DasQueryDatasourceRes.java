package com.isxcode.star.api.pojos.datasource.res;

import lombok.Data;

@Data
public class DasQueryDatasourceRes {

  private String name;

  private String id;

  private String jdbcUrl;

  private String username;

  private String comment;

  private String status;

  private String checkTime;

  private String type;
}
