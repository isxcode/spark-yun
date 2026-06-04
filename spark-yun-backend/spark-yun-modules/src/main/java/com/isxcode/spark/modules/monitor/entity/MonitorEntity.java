package com.isxcode.spark.modules.monitor.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.isxcode.spark.common.jpa.SyId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@SQLDelete(sql = "UPDATE SY_MONITOR SET deleted = 1 WHERE id = ?")
@SQLRestriction("deleted = 0")
@Filter(name = "tenantFilter", condition = "tenant_id in (:tenantIds)")
@Table(name = "SY_MONITOR")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MonitorEntity {

    @Id
    @SyId
    private String id;

    private String clusterId;

    private String clusterNodeId;

    private String status;

    private String log;

    private Double usedStorageSize;

    private Double usedMemorySize;

    private Double networkIoReadSpeed;

    private Double networkIoWriteSpeed;

    private Double diskIoReadSpeed;

    private Double diskIoWriteSpeed;

    private Double cpuPercent;

    private LocalDateTime createDateTime;

    @Transient
    private Integer deleted;

    private String tenantId;
}
