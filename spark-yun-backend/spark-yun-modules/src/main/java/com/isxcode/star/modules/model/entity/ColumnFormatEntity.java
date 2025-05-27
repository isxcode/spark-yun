package com.isxcode.star.modules.model.entity;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.Data;
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
@SQLDelete(sql = "UPDATE SY_COLUMN_FORMAT SET deleted = 1 WHERE id = ? and version_number = ?")
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Table(name = "SY_COLUMN_FORMAT")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class ColumnFormatEntity {

    @Id
    @GeneratedValue(generator = "sy-id-generator")
    @GenericGenerator(name = "sy-id-generator", strategy = "com.isxcode.star.config.GeneratedValueConfig")
    private String id;

    private String name;

    private String columnTypeCode;

    private String columnType;

    private String status;

    private String remark;

    private String isNull;

    private String isDuplicate;

    private String isPartition;

    private String isPrimary;

    private String defaultValue;

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
        this.tenantId = TENANT_ID.get();
    }
}
