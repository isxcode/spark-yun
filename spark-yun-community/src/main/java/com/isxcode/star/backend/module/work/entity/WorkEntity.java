package com.isxcode.star.backend.module.work.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 只负责数据库对象映射.
 */
@Data
@Entity
@Table(name = "SY_WORK")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class WorkEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
    name = "sy-id-generator",
    strategy = "com.isxcode.star.backend.config.GeneratedValueConfig")
  private String id;

  private String name;

  private String workType;

  private String commentInfo;

  private String status;

  private String workConfigId;

  private String workflowId;

  private LocalDateTime createDateTime;
}
