package com.isxcode.star.modules.monitor.repository;

import com.isxcode.star.api.monitor.pojos.dto.MonitorLineDto;
import com.isxcode.star.modules.file.entity.FileEntity;
import com.isxcode.star.modules.monitor.entity.MonitorEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_file"})
public interface MonitorRepository extends JpaRepository<MonitorEntity, String>, JpaSpecificationExecutor<FileEntity> {

	@Query("select new com.isxcode.star.api.monitor.pojos.dto.MonitorLineDto ( count(1),M.clusterId, M.createDateTime,sum(M.cpuPercent) / count(1),sum(M.diskIoReadSpeed) / count(1),sum(M.diskIoWriteSpeed) / count(1),sum(M.networkIoReadSpeed) / count(1),sum(M.networkIoWriteSpeed) / count(1), sum(M.usedMemorySize) / count(1), sum(M.usedStorageSize) / count(1) ) from MonitorEntity M where M.clusterId=:clusterId and M.createDateTime between :startDateTime and :endDateTime group by M.clusterId, M.createDateTime ")
	List<MonitorLineDto> queryMonitorLine(@Param("clusterId") String clusterId,
			@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

}
