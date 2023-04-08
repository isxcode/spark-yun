package com.isxcode.star.api.pojos.work.res;

import lombok.Data;

@Data
public class WokGetStatusRes {

  private String yarnApplicationState;

  private String finalApplicationStatus;

  private String trackingUrl;
}
