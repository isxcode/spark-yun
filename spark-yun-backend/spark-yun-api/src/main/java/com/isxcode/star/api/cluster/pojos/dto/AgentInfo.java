package com.isxcode.star.api.cluster.pojos.dto;

import lombok.Data;

@Data
public class AgentInfo {

  private String execStatus;

  private String status;

  private String log;

  private String allMemory;

  private String usedMemory;

  private String allStorage;

  private String usedStorage;

  private String cpuPercent;

  private String hadoopHome;
}
