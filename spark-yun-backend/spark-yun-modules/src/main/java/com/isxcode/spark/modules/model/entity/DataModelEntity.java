package com.isxcode.spark.modules.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@SQLDelete(sql = "UPDATE SY_DATA_MODEL SET deleted = 1 WHERE id = ? and version_number = ?")
@SQLRestriction("deleted = 0")
@Filter(name = "tenantFilter", condition = "tenant_id in (:tenantIds)")
@Table(name = "sy_data_model")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class DataModelEntity {

    @Id
    @SyId
    private String id;

    private String name;

    private String layerId;

    private String dbType;

    private String datasourceId;

    private String tableName;

    private String modelType;

    private String tableConfig;

    private String status;

    private String buildLog;

    private String remark;

    @CreatedDate
    private LocalDateTime createDateTime;

    @LastModifiedDate
    private LocalDateTime lastModifiedDateTime;

    @CreatedBy
    private String createBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @Transient
    private Integer deleted;

    @Version
    private Long versionNumber;

    private String tenantId;

    @PrePersist
    public void prePersist() {
        this.tenantId = ContextHolder.getTenantId();
    }
}
