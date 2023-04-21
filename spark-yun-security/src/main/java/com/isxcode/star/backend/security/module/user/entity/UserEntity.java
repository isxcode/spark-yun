package com.isxcode.star.backend.security.module.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.isxcode.star.common.base.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@SQLDelete(
  sql = "UPDATE SY_USER SET deleted = 1 WHERE id = ? and version_number = ?"
)
@Where(clause = "deleted = 0")
@Table(name = "SY_USER")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class UserEntity extends BaseEntity {

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
}
