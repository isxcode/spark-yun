package com.isxcode.star.backend.security.module.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "SY_USER")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
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

  private String userRole;

  @CreatedDate
  private LocalDateTime createDateTime;

  @LastModifiedDate
  private LocalDateTime lastModifiedDateTime;

  @CreatedBy
  private String createBy;

  @LastModifiedBy
  private String lastModifiedBy;

  @Version
  private Long versionNumber;
}
