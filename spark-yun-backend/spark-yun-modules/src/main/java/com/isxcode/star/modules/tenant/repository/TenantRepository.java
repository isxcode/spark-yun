package com.isxcode.star.modules.tenant.repository;

import com.isxcode.star.modules.tenant.entity.TenantEntity;
import java.util.Optional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {"SY_TENANT"})
public interface TenantRepository extends JpaRepository<TenantEntity, String> {

  Optional<TenantEntity> findByName(String name);

  @Query(
      "SELECT T FROM TenantEntity T WHERE T.name LIKE %:keyword% OR T.remark LIKE %:keyword% order by T.createDateTime desc ")
  Page<TenantEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);
}
