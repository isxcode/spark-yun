package com.isxcode.star.modules.api.repository;

import java.util.Optional;

import com.isxcode.star.api.main.constants.ModuleVipCode;
import com.isxcode.star.modules.api.entity.ApiEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleVipCode.VIP_API})
public interface ApiRepository extends JpaRepository<ApiEntity, String> {

    Optional<ApiEntity> findByPathAndApiType(String path, String tenantId);

    @Query("select A from ApiEntity A where A.name LIKE %:keyword% or A.remark LIKE %:keyword% order by A.createDateTime desc")
    Page<ApiEntity> pageApi(@Param("keyword") String searchKeyWord, Pageable pageable);

    long countByStatus(String status);

    Optional<ApiEntity> findByName(String name);
}
