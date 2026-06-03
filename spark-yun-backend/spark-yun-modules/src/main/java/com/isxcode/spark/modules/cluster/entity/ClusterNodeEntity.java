package com.isxcode.spark.modules.cluster.entity;

import static com.isxcode.spark.common.config.CommonConfig.TENANT_ID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.isxcode.spark.common.jpa.SyId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE SY_CLUSTER_NODE SET deleted = 1 WHERE id = ?")
@SQLRestriction("deleted = 0 ${TENANT_FILTER} ")
@Table(name = "SY_CLUSTER_NODE")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class ClusterNodeEntity {

    @Id
    @SyId
    private String id;

    private String name;

    private String remark;

    private String status;

    private LocalDateTime checkDateTime;

    private Double allMemory;

    private Double usedMemory;

    private Double allStorage;

    private Double usedStorage;

    private Double cpuPercent;

    private String clusterId;

    private String host;

    private String port;

    private String username;

    private String passwd;

    private Boolean installSparkLocal;

    private Boolean installFlinkLocal;

    private String agentHomePath;

    private String agentPort;

    private String hadoopHomePath;

    private String sparkHomePath;

    private String flinkHomePath;

    private String agentLog;

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

    private String tenantId;

    @PrePersist
    public void prePersist() {
        this.tenantId = TENANT_ID.get();
    }
}
