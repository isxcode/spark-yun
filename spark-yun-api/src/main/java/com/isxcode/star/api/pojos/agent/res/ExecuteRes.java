package com.isxcode.star.api.pojos.agent.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteRes {

  private String message;

  private String log;

  private String applicationId;
}
