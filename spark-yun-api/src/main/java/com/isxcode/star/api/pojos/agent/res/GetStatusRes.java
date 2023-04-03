package com.isxcode.star.api.pojos.agent.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetStatusRes {

  private String yarnApplicationState;

  private String finalApplicationStatus;

  private String trackingUrl;
}
