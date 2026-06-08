package com.isxcode.spark.modules.layer.entity;

import com.isxcode.spark.common.security.ContextHolder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import jakarta.persistence.*;

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
@SQLDelete(sql = "UPDATE SY_TABLE_LAYER SET deleted = 1 WHERE id = ? and version_number = ?")
@SQLRestriction("deleted = 0")
@Filter(name = "tenantFilter", condition = "tenant_id in (:tenantIds)")
@Table(name = "sy_table_layer")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class LayerEntity {

    @Id
    @SyId
    private String id;

    private String name;

    private String tableRule;

    private String parentLayerId;

    private String parentIdList;

    private String parentNameList;

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
