package com.isxcode.star.api.pojos.datasource.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestConnectRes {

  private Boolean canConnect;

  private String message;
}
