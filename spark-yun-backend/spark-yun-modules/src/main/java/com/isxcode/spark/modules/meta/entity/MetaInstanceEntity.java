package com.isxcode.spark.modules.meta.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.isxcode.spark.common.config.CommonConfig.TENANT_ID;

@Data
@Entity
@SQLDelete(sql = "UPDATE SY_META_INSTANCE SET deleted = 1 WHERE id = ? and version_number = ?")
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Table(name = "SY_META_INSTANCE")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@AllArgsConstructor
public class MetaInstanceEntity {

    @Id
    @GeneratedValue(generator = "sy-id-generator")
    @GenericGenerator(name = "sy-id-generator", strategy = "com.isxcode.spark.config.GeneratedValueConfig")
    private String id;

    private String metaWorkId;

    private String triggerType;

    private String status;

    private String collectLog;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

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
        this.tenantId = TENANT_ID.get();
    }
}
