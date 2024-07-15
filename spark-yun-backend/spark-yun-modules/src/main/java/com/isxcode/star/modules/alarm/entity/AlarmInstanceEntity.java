package com.isxcode.star.modules.alarm.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;

@Data
@Entity
@SQLDelete(sql = "UPDATE SY_ALARM_INSTANCE SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Table(name = "SY_ALARM_INSTANCE")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@AllArgsConstructor
public class AlarmInstanceEntity {

    @Id
    @GeneratedValue(generator = "sy-id-generator")
    @GenericGenerator(name = "sy-id-generator", strategy = "com.isxcode.star.config.GeneratedValueConfig")
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
        this.tenantId = TENANT_ID.get();
    }
}
