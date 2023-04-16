package com.isxcode.star.api.pojos.work.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WokGetStatusRes {

  private String yarnApplicationState;

  private String finalApplicationStatus;

  private String trackingUrl;
}
