package com.isxcode.star.backend.module.workflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 只负责数据库对象映射.
 */
@Data
@Entity
@Table(name = "sy_works")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class WorkEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
    name = "sy-id-generator",
    strategy = "com.isxcode.star.backend.config.GeneratedValueConfig")
  private String id;

  private String name;

  private String type;

  private String commentInfo;

  private String status;

  private String label;

  private String script;

  private String datasourceId;

  private String engineId;

}
