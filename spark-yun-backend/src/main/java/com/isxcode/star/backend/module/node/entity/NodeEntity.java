package com.isxcode.star.backend.module.node.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

/** 只负责数据库对象映射. */
@Data
@Entity
@Table(name = "sy_nodes")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class NodeEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
      name = "sy-id-generator",
      strategy = "com.isxcode.star.backend.config.GeneratedValueConfig")
  private String id;

  private String name;

  private String commentInfo;

  private String status;

  private LocalDateTime checkDate;

  private Integer allMemory;

  private Integer activeMemory;

  private Integer allStorage;

  private Integer activeStorage;

  private String cpuPercent;

  private String engineId;

  private String host;

  private String port;

  private String username;

  private String passwd;

  private String homePath;
}
