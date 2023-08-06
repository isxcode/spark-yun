package com.isxcode.star.api.cluster.pojos.dto;

import lombok.Data;

@Data
public class AgentInfo {

  private String execStatus;

  private String status;

  private String log;

  private Double allMemory;

  private Double usedMemory;

  private Double allStorage;

  private Double usedStorage;

  private Double cpuPercent;

  private String hadoopHome;
}
