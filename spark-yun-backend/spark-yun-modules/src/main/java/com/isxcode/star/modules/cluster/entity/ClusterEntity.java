package com.isxcode.star.modules.cluster.entity;

import static com.isxcode.star.security.main.WebSecurityConfig.TENANT_ID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** 计算引擎. */
@Data
@Entity
@SQLDelete(sql = "UPDATE SY_CLUSTER SET deleted = 1 WHERE id = ? and version_number = ?")
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Table(name = "SY_CLUSTER")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class ClusterEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
      name = "sy-id-generator",
      strategy = "com.isxcode.star.config.GeneratedValueConfig")
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

  private String clusterType;

  @CreatedDate private LocalDateTime createDateTime;

  @LastModifiedDate private LocalDateTime lastModifiedDateTime;

  @CreatedBy private String createBy;

  @LastModifiedBy private String lastModifiedBy;

  @Version private Long versionNumber;

  @Transient private Integer deleted;

  private String tenantId;

  @PrePersist
  public void prePersist() {
    this.tenantId = TENANT_ID.get();
  }
}
