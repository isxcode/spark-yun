package com.isxcode.spark.modules.alarm.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.isxcode.spark.common.jpa.SyId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.isxcode.spark.common.security.ContextHolder;

@Data
@Entity
@SQLDelete(sql = "UPDATE SY_ALARM_INSTANCE SET deleted = 1 WHERE id = ?")
@SQLRestriction("deleted = 0")
@Filter(name = "tenantFilter", condition = "tenant_id in (:tenantIds)")
@Table(name = "SY_ALARM_INSTANCE")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@AllArgsConstructor
public class AlarmInstanceEntity {

    @Id
    @SyId
    private String id;

    private String alarmId;

    private String sendStatus;

    private String alarmType;

    private String alarmEvent;

    private String msgId;

    private String content;

    private String response;

    private String receiver;

    private String instanceId;

    private LocalDateTime sendDateTime;

    @Transient
    private Integer deleted;

    @CreatedDate
    private LocalDateTime createDateTime;

    private String tenantId;

    public AlarmInstanceEntity() {}

    @PrePersist
    public void prePersist() {
        this.tenantId = ContextHolder.getTenantId();
    }
}
