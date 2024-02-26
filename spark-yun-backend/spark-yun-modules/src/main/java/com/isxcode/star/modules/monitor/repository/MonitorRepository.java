package com.isxcode.star.modules.monitor.repository;

import com.isxcode.star.api.monitor.dto.MonitorLineDto;
import com.isxcode.star.modules.file.entity.FileEntity;
import com.isxcode.star.modules.monitor.entity.MonitorEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_file"})
public interface MonitorRepository extends JpaRepository<MonitorEntity, String>, JpaSpecificationExecutor<FileEntity> {

	@Query("select new com.isxcode.star.api.monitor.dto.MonitorLineDto( count(1),\n" + "       M.clusterId,\n"
			+ "       M.createDateTime,\n" + "       sum(M.cpuPercent) / count(1),\n"
			+ "       sum(M.diskIoReadSpeed) / count(1),\n" + "       sum(M.diskIoWriteSpeed) / count(1),\n"
			+ "       sum(M.networkIoReadSpeed) / count(1),\n" + "       sum(M.networkIoWriteSpeed) / count(1),\n"
			+ "       sum(M.usedMemorySize) / count(1),\n" + "       sum(M.usedStorageSize) / count(1) )\n"
			+ "from MonitorEntity M\n" + "group by M.clusterId, M.createDateTime")
	List<MonitorLineDto> queryMonitorLine();

}
