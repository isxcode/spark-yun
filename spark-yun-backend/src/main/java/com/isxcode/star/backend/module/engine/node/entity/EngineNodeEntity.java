package com.isxcode.star.backend.module.engine.node.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "SY_ENGINE_NODE")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class EngineNodeEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
    name = "sy-id-generator",
    strategy = "com.isxcode.star.backend.config.GeneratedValueConfig")
  private String id;

  private String name;

  private String commentInfo;

  private String status;

  private LocalDateTime checkDateTime;

  private Double allMemory;

  private Double usedMemory;

  private Double allStorage;

  private Double usedStorage;

  private String cpuPercent;

  private String calculateEngineId;

  private String host;

  private String port;

  private String username;

  private String passwd;

  private String agentHomePath;

  private String agentPort;

  private String hadoopHomePath;
}
