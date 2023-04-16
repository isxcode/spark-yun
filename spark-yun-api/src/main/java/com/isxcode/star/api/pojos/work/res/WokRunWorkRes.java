package com.isxcode.star.api.pojos.work.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WokRunWorkRes {

  private String executeStatus;

  private String message;

  private String log;

  private List<List<String>> data;

  private String applicationId;

  private String yarnApplicationState;

  private String finalApplicationStatus;

  private String trackingUrl;
}
