package com.isxcode.star.api.pojos.agent.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetStatusRes {

  private String yarnApplicationState;

  private String finalApplicationStatus;
}
