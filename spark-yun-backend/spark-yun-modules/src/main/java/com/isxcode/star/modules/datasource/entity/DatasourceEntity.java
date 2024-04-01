package com.isxcode.star.modules.datasource.entity;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
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
@SQLDelete(sql = "UPDATE SY_DATASOURCE SET deleted = 1 WHERE id = ? and version_number = ?")
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Table(name = "SY_DATASOURCE")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class DatasourceEntity {

	@Id
	@GeneratedValue(generator = "sy-id-generator")
	@GenericGenerator(name = "sy-id-generator", strategy = "com.isxcode.star.config.GeneratedValueConfig")
	private String id;

	private String name;

	private String remark;

	private String jdbcUrl;

	private String dbType;

	private LocalDateTime checkDateTime;

	private String username;

	private String passwd;

	private String status;

	private String connectLog;

	private String metastoreUris;

	private String driverId;

	private String kafkaConfig;

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

	@PrePersist
	public void prePersist() {
		this.tenantId = TENANT_ID.get();
	}
}
