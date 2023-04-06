package com.isxcode.star.api.pojos.datasource.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DasTestConnectRes {

  private Boolean canConnect;

  private String connectLog;
}
