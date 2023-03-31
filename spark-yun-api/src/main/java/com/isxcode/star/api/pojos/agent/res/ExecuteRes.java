package com.isxcode.star.api.pojos.agent.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExecuteRes {

  private String message;

  private String log;

  private String applicationId;
}
