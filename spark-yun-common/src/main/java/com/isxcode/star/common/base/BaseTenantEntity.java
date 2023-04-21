package com.isxcode.star.common.base;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Version;
import java.time.LocalDateTime;

public class BaseTenantEntity {

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

  private Integer deleted;

  private String tenantId;
}
