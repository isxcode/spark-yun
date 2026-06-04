package com.isxcode.spark.modules.work.entity;

import static com.isxcode.spark.common.config.CommonConfig.TENANT_ID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

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

@Data
@Entity
@SQLDelete(sql = "UPDATE SY_WORK_CONFIG SET deleted = 1 WHERE id = ? and version_number = ?")
@SQLRestriction("deleted = 0")
@Filter(name = "tenantFilter", condition = "tenant_id in (:tenantIds)")
@Table(name = "SY_WORK_CONFIG")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class WorkConfigEntity {

    @Id
    @SyId
    private String id;

    private String datasourceId;

    private String script;

    private String cronConfig;

    private String syncWorkConfig;

    private String syncFlinkConfig;

    private String excelSyncConfig;

    private String apiWorkConfig;

    private String syncRule;

    private String clusterConfig;

    private String jarJobConfig;

    private String funcConfig;

    private String libConfig;

    private String libPackageConfig;

    private String containerId;

    private String alarmList;

    private String dbMigrateConfig;

    private String queryConfig;

    private String sparkEtlConfig;

    private String apiSyncConfig;

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
