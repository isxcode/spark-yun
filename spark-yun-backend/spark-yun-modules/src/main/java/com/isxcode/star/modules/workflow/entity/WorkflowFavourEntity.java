package com.isxcode.star.modules.workflow.entity;

import static com.isxcode.star.security.main.WebSecurityConfig.TENANT_ID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@SQLDelete(sql = "UPDATE SY_WORKFLOW_FAVOUR SET deleted = 1 WHERE id = ? and version_number = ?")
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SY_WORKFLOW_FAVOUR")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class WorkflowFavourEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
      name = "sy-id-generator",
      strategy = "com.isxcode.star.config.GeneratedValueConfig")
  private String id;

  private String userId;

  private String workflowId;

  private Integer topIndex;

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
