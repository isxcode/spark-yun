package com.isxcode.star.backend.module.tenant.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.isxcode.star.common.base.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@SQLDelete(
  sql = "UPDATE SY_TENANT SET deleted = 1 WHERE id = ? and version_number = ?"
)
@Data
@Entity
@Table(name = "SY_TENANT")
@Where(clause = "deleted = 0")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class TenantEntity extends BaseEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
    name = "sy-id-generator",
    strategy = "com.isxcode.star.backend.config.GeneratedValueConfig")
  private String id;

  private String name;

  private Integer usedMemberNum;

  private Integer maxMemberNum;

  private Integer usedWorkflowNum;

  private Integer maxWorkflowNum;

  private String status;

  private String introduce;

  private String remark;

  private LocalDateTime checkDateTime;
}
