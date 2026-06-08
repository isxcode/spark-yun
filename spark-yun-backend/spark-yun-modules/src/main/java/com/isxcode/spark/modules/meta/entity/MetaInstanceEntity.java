package com.isxcode.spark.modules.meta.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.isxcode.spark.common.jpa.SyId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.isxcode.spark.common.security.ContextHolder;

@Data
@Entity
@SQLDelete(sql = "UPDATE SY_META_INSTANCE SET deleted = 1 WHERE id = ? and version_number = ?")
@SQLRestriction("deleted = 0")
@Filter(name = "tenantFilter", condition = "tenant_id in (:tenantIds)")
@Table(name = "sy_meta_instance")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@AllArgsConstructor
public class MetaInstanceEntity {

    @Id
    @SyId
    private String id;

    private String metaWorkId;

    private String triggerType;

    private String status;

    private String collectLog;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private String runnerOwner;

    private LocalDateTime heartbeatDateTime;

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

    public MetaInstanceEntity() {}

    @PrePersist
    public void prePersist() {
        this.tenantId = ContextHolder.getTenantId();
    }
}
