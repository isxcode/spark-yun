package com.isxcode.star.modules.alarm.entity;

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

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;

@Data
@Entity
@SQLDelete(sql = "UPDATE SY_MESSAGE SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Table(name = "SY_MESSAGE")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@AllArgsConstructor
public class MessageEntity {

	@Id
	@GeneratedValue(generator = "sy-id-generator")
	@GenericGenerator(name = "sy-id-generator", strategy = "com.isxcode.star.config.GeneratedValueConfig")
	private String id;

	private String name;

	private String remark;

	private String status;

	private String msgType;

	private String msgConfig;

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

	public MessageEntity() {
	}

	@PrePersist
	public void prePersist() {
		this.tenantId = TENANT_ID.get();
	}
}
