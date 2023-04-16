package com.isxcode.star.api.pojos.datasource.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
