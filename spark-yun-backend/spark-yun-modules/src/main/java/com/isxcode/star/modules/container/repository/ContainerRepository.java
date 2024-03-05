package com.isxcode.star.modules.container.repository;

import com.isxcode.star.modules.container.entity.ContainerEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 只负责数据库查询逻辑.
 */
@Repository
@CacheConfig(cacheNames = {"sy_container"})
public interface ContainerRepository extends JpaRepository<ContainerEntity, String> {

	@Query("select C from ContainerEntity C where C.name LIKE %:keyword% or C.remark LIKE %:keyword% order by C.createDateTime desc")
	Page<ContainerEntity> pageContainer(@Param("keyword") String searchKeyWord, Pageable pageable);
}
