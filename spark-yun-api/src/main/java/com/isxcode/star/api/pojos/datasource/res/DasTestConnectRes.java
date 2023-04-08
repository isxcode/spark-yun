package com.isxcode.star.api.pojos.datasource.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DasTestConnectRes {

  private Boolean canConnect;

  private String connectLog;
}
