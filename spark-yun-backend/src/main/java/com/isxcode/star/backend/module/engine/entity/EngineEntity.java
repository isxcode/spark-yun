package com.isxcode.star.backend.module.engine.entity;

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
@Table(name = "sy_engines")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class EngineEntity {

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

  private Integer allNode;

  private Integer activeNode;

  private Integer allMemory;

  private Integer activeMemory;

  private Integer allStorage;

  private Integer activeStorage;
}
