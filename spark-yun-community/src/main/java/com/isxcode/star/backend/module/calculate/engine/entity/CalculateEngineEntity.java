package com.isxcode.star.backend.module.calculate.engine.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;

/** 计算引擎. */
@Data
@Entity
@Table(name = "SY_CLUSTER")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class CalculateEngineEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
      name = "sy-id-generator",
      strategy = "com.isxcode.star.backend.config.GeneratedValueConfig")
  private String id;

  private String name;

  private String remark;

  private String status;

  private LocalDateTime checkDateTime;

  private Integer allNodeNum;

  private Integer activeNodeNum;

  private Double allMemoryNum;

  private Double usedMemoryNum;

  private Double allStorageNum;

  private Double usedStorageNum;

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

  @Transient
  private Integer deleted;

  private String tenantId;

  @PrePersist
  public void prePersist() {
    this.tenantId = TENANT_ID.get();
  }
}
