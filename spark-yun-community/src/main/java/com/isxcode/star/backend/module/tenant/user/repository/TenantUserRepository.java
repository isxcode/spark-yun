package com.isxcode.star.backend.module.tenant.user.repository;

import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import com.isxcode.star.backend.module.tenant.user.entity.TenantUserEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 数据源模块. */
@Repository
@CacheConfig(cacheNames = {"SY_DATASOURCE"})
public interface TenantUserRepository extends JpaRepository<TenantUserEntity, String> {

  @Query("SELECT D FROM DatasourceEntity D WHERE D.name LIKE %:keyword% OR D.commentInfo LIKE %:keyword% OR D.datasourceType LIKE %:keyword% OR D.username LIKE %:keyword% OR D.jdbcUrl LIKE %:keyword%")
  Page<DatasourceEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);
}
