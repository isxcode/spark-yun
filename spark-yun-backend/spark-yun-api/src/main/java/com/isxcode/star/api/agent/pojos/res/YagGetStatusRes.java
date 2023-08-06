package com.isxcode.star.api.agent.pojos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YagGetStatusRes {

  private String yarnApplicationState;

  private String finalApplicationStatus;

  private String trackingUrl;

  private String appStatus;

  private String appId;
}
