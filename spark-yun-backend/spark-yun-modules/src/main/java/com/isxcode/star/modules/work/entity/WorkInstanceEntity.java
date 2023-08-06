package com.isxcode.star.modules.work.entity;

import static com.isxcode.star.security.main.WebSecurityConfig.TENANT_ID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE SY_WORK_INSTANCE SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Table(name = "SY_WORK_INSTANCE")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class WorkInstanceEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
      name = "sy-id-generator",
      strategy = "com.isxcode.star.config.GeneratedValueConfig")
  private String id;

  private String versionId;

  private String workId;

  private String instanceType;

  private String status;

  private Date planStartDateTime;

  private Date nextPlanDateTime;

  private Date execStartDateTime;

  private Date execEndDateTime;

  private String submitLog;

  private String yarnLog;

  private String sparkStarRes;

  private String resultData;

  private String workflowInstanceId;

  private Boolean quartzHasRun;

  @CreatedDate private LocalDateTime createDateTime;

  @LastModifiedDate private LocalDateTime lastModifiedDateTime;

  @CreatedBy private String createBy;

  @LastModifiedBy private String lastModifiedBy;

  @Transient private Integer deleted;

  private String tenantId;

  @PrePersist
  public void prePersist() {
    this.tenantId = TENANT_ID.get();
  }
}
