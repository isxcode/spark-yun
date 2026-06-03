package com.isxcode.spark.security.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@SQLDelete(sql = "UPDATE SY_TENANT SET deleted = 1 WHERE id = ? and version_number = ?")
@Data
@Entity
@Table(name = "SY_TENANT")
@Where(clause = "deleted = 0")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class TenantEntity {

    @Id
    @GeneratedValue(generator = "sy-id-generator")
    @GenericGenerator(name = "sy-id-generator", strategy = "com.isxcode.spark.config.GeneratedValueConfig")
    private String id;

    private String name;

    private Long usedMemberNum;

    private Long maxMemberNum;

    private Long usedWorkflowNum;

    private Long maxWorkflowNum;

    private String status;

    private String introduce;

    private String remark;

    private LocalDateTime checkDateTime;

    private LocalDateTime validStartDateTime;

    private LocalDateTime validEndDateTime;

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
}
