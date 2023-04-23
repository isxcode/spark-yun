package com.isxcode.star.backend.module.tenant.repository;

import com.isxcode.star.backend.module.tenant.entity.TenantEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {"SY_TENANT"})
public interface TenantRepository extends JpaRepository<TenantEntity, String> {

  Optional<TenantEntity> findByName(String name);
}
