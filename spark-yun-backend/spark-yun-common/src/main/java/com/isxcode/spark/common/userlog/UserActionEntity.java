package com.isxcode.spark.common.userlog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "SY_USER_ACTION")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class UserActionEntity {

    @Id
    @GeneratedValue(generator = "sy-id-generator")
    @GenericGenerator(name = "sy-id-generator", strategy = "com.isxcode.spark.config.GeneratedValueConfig")
    private String id;

    private String userId;

    private String tenantId;

    private String reqPath;

    private String reqMethod;

    private String reqHeader;

    private String reqBody;

    private String resBody;

    private Long startTimestamp;

    private Long endTimestamp;

    @CreatedDate
    private LocalDateTime createDateTime;

    @CreatedBy
    private String createBy;
}
