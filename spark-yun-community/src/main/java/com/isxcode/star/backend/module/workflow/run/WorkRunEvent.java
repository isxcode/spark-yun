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

  private String versionId;

  private WorkRunContext workRunContext;

  public WorkRunEvent(String flowInstanceId, String workId, List<List<String>> nodeMapping, List<String> nodeList, List<String> dagStartList, List<String> dagEndList, String userId, String tenantId) {
    this.flowInstanceId = flowInstanceId;
    this.workId = workId;
    this.nodeMapping = nodeMapping;
    this.nodeList = nodeList;
    this.dagStartList = dagStartList;
    this.dagEndList = dagEndList;
    this.userId = userId;
    this.tenantId = tenantId;
  }
}
