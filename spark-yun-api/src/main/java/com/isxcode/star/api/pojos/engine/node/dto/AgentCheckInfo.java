package com.isxcode.star.api.pojos.engine.node.dto;

import lombok.Data;

@Data
public class AgentCheckInfo {

  private String installStatus;

  private String runStatus;

  private Double allMemory;

  private Double usedMemory;

  private Double allStorage;

  private Double usedStorage;

  private Double cpuPercent;
}
