package com.isxcode.star.api.work.pojos.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WokGetStatusRes {

  private String yarnApplicationState;

  private String finalApplicationStatus;

  private String trackingUrl;
}
