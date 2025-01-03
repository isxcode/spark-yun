package com.isxcode.star.modules.meta.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;

@Data
@Entity
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Table(name = "SY_META_COLUMN_INFO")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@AllArgsConstructor
@IdClass(MetaColumnId.class)
public class MetaColumnInfoEntity {

    @Id
    private String datasourceId;

    @Id
    private String tableName;

    @Id
    private String columnName;

    private String customComment;

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

    public MetaColumnInfoEntity() {}

    @PrePersist
    public void prePersist() {
        this.tenantId = TENANT_ID.get();
    }
}
