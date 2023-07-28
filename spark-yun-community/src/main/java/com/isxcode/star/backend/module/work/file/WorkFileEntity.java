package com.isxcode.star.backend.module.work.file;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@SQLDelete(sql = "UPDATE SY_WORK_FILE SET deleted = 1 WHERE id = ? and version_number = ?")
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Table(name = "SY_WORK_FILE")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@AllArgsConstructor
public class WorkFileEntity {

  @Id
  @GeneratedValue(generator = "sy-id-generator")
  @GenericGenerator(
      name = "sy-id-generator",
      strategy = "com.isxcode.star.backend.config.GeneratedValueConfig")
  private String id;

  private String workId;

  private String fileName;

  private String fileSize;

  private String filePath;

  private String fileType;

  @CreatedDate private LocalDateTime createDateTime;

  @LastModifiedDate private LocalDateTime lastModifiedDateTime;

  @CreatedBy private String createBy;

  @LastModifiedBy private String lastModifiedBy;

  @Version private Long versionNumber;

  @Transient private Integer deleted;

  private String tenantId;

  public WorkFileEntity() {}

  @PrePersist
  public void prePersist() {
    this.tenantId = TENANT_ID.get();
  }
}
