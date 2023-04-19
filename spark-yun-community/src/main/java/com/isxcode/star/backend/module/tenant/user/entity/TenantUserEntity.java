package com.isxcode.star.backend.module.tenant.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/** 只负责数据库对象映射. */
@Data
@Entity
@Table(name = "SY_DATASOURCE")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class TenantUserEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
      name = "sy-id-generator",
      strategy = "com.isxcode.star.backend.config.GeneratedValueConfig")
  private String id;

  private String name;

  private String commentInfo;

  private String jdbcUrl;

  private String datasourceType;

  private LocalDateTime checkDateTime;

  private String username;

  private String passwd;

  private String status;
}
