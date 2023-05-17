package com.isxcode.star.api.pojos.engine.node.dto;

import lombok.Data;

@Data
public class AgentCheckInfo {

  private String execStatus;

  private String checkStatus;

  private String log;

  private Double allMemory;

  private Double usedMemory;

  private Double allStorage;

  private Double usedStorage;

  private Double cpuPercent;
}
