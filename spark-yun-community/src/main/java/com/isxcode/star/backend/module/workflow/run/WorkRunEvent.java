package com.isxcode.star.backend.module.workflow.run;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class WorkRunEvent {

  private String flowInstanceId;

  private String workId;

  private List<List<String>> nodeMapping;

  private List<String> nodeList;

  private List<String> dagStartList;

  private List<String> dagEndList;

  private String userId;

  private String tenantId;
}
