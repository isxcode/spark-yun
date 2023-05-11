package com.isxcode.star.backend.module.user.action.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "SY_USER_ACTION")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class UserActionEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
    name = "sy-id-generator",
    strategy = "com.isxcode.star.backend.config.GeneratedValueConfig")
  private String id;

  private String userId;

  private String tenantId;

  private String reqPath;

  private String reqMethod;

  private String reqHeader;

  private String reqBody;

  private String resBody;

  private Long startTimestamp;

  private Long endTimestamp;

  @CreatedDate
  private LocalDateTime createDateTime;

  @CreatedBy
  private String createBy;
}
