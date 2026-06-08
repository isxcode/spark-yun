package com.isxcode.spark.modules.workflow.entity;

import com.isxcode.spark.common.security.ContextHolder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.isxcode.spark.common.jpa.SyId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE SY_WORKFLOW_VERSION SET deleted = 1 WHERE id = ? and version_number = ?")
@SQLRestriction("deleted = 0")
@Filter(name = "tenantFilter", condition = "tenant_id in (:tenantIds)")
@Table(name = "sy_workflow_version")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class WorkflowVersionEntity {

    @Id
    @SyId
    private String id;

    private String name;

    private String workflowId;

    private String workflowType;

    private String nodeMapping;

    private String nodeList;

    private String dagStartList;

    private String dagEndList;

    private String workVersionMap;

    private String cronConfig;

    private String webConfig;

    private String alarmList;

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
        this.tenantId = ContextHolder.getTenantId();
    }
}
