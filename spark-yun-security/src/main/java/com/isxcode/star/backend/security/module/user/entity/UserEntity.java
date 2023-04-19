package com.isxcode.star.backend.security.module.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "SY_USER")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class UserEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
      name = "sy-id-generator",
      strategy = "com.isxcode.star.backend.config.GeneratedValueConfig")
  private String id;

  private String username;

  private String account;

  private String passwd;

  private String phone;

  private String email;

  private String commentInfo;

  private String status;

  private String latestTenantId;

  private LocalDateTime createDateTime;
}
