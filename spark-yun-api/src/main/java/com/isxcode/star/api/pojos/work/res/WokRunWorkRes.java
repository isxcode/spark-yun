package com.isxcode.star.api.pojos.work.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WokRunWorkRes {

  private String executeStatus;

  private String message;

  private String log;

  private List<List<String>> data;

  private String appId;

  private String yarnApplicationState;

  private String finalApplicationStatus;

  private String trackingUrl;

  private String instanceId;
}
