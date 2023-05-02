package com.isxcode.star.backend.module.tenant.user.repository;

import com.isxcode.star.backend.module.tenant.user.entity.TenantUserEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {"SY_DATASOURCE"})
public interface TenantUserRepository extends JpaRepository<TenantUserEntity, String> {

  Optional<TenantUserEntity> findByTenantIdAndUserId(String tenantId, String userId);

  List<TenantUserEntity> findAllByUserId(String userId);

  @Query(value = "" +
    "select " +
    "   T.id as id, " +
    "   U.account as account, " +
    "   U.username as username, " +
    "   U.phone as phone, " +
    "   U.email as email, " +
    "   DATE_FORMAT(T.createDateTime, '%Y-%m-%d %H:%i:%s') as createDateTime, " +
    "   T.roleCode as roleCode " +
    "from TenantUserEntity T left join UserEntity U on T.userId = U.id  " +
    "WHERE U.roleCode != 'ROLE_SYS_ADMIN' " +
    "   and T.tenantId=:tenantId " +
    "   and (U.username LIKE %:keyword% OR U.account LIKE %:keyword% OR U.phone LIKE %:keyword% OR U.email LIKE %:keyword%) ")
  Page<Map> searchTenantUser(@Param("tenantId") String tenantId, @Param("keyword") String searchKeyWord, Pageable pageable);

  long countByTenantId(String tenantId);
}
