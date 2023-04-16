package com.isxcode.star.backend.module.work.config.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/** 只负责数据库对象映射. */
@Data
@Entity
@Table(name = "SY_WORK_CONFIG")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class WorkConfigEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
      name = "sy-id-generator",
      strategy = "com.isxcode.star.backend.config.GeneratedValueConfig")
  private String id;

  private String calculateEngineId;

  private String datasourceId;

  private String sql;
}
