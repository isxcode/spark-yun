package com.isxcode.star.api.datasource.pojos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DasQueryDatasourceRes {

  private String name;

  private String id;

  private String jdbcUrl;

  private String username;

  private String remark;

  private String status;

  private String checkDateTime;

  private String dbType;

  private String connectLog;
}
