package com.isxcode.star.backend.module.tenant.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.isxcode.star.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
  @Schema(title = "租户唯一id")
  private String id;

  @Schema(title = "租户名称")
  private String name;

  @Schema(title = "备注")
  private String remark;

  @Schema(title = "租户简介")
  private String introduce;

  @Schema(title = "最大工作流数量")
  private Integer maxWorkflowNum;

  @Schema(title = "最大用户数量")
  private Integer maxMemberNum;

  @Schema(title = "租户状态")
  private String Status;
}
