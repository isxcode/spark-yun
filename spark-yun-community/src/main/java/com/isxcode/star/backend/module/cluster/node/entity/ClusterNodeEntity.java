package com.isxcode.star.backend.module.cluster.node.entity;

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

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;

@Data
@Entity
@SQLDelete(
  sql = "UPDATE SY_CLUSTER_NODE SET deleted = 1 WHERE id = ? and version_number = ?"
)
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Table(name = "SY_CLUSTER_NODE")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class ClusterNodeEntity {

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

  private Double allMemory;

  private Double usedMemory;

  private Double allStorage;

  private Double usedStorage;

  private String cpuPercent;

  private String clusterId;

  private String host;

  private String port;

  private String username;

  private String passwd;

  private String agentHomePath;

  private String agentPort;

  private String hadoopHomePath;

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
