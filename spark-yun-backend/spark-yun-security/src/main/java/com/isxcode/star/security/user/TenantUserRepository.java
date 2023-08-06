package com.isxcode.star.security.user;

import com.isxcode.star.api.tenant.pojos.res.TurQueryTenantUserRes;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {"SY_DATASOURCE"})
public interface TenantUserRepository extends JpaRepository<TenantUserEntity, String> {

  Optional<TenantUserEntity> findByTenantIdAndUserId(String tenantId, String userId);

  List<TenantUserEntity> findAllByUserId(String userId);

  @Query(
      value =
          "select "
              + "   new com.isxcode.star.api.tenant.pojos.res.TurQueryTenantUserRes(T.id , "
              + "   U.account , "
              + "   U.username , "
              + "   U.phone , "
              + "   U.email , "
              + "   T.roleCode) "
              + "from TenantUserEntity T left join UserEntity U on T.userId = U.id  "
              + "WHERE U.roleCode != 'ROLE_SYS_ADMIN' "
              + "   and T.tenantId=:tenantId "
              + "   and (U.username LIKE %:keyword% "
              + "OR U.account LIKE %:keyword% "
              + "OR U.phone LIKE %:keyword% "
              + "OR U.email LIKE %:keyword%) order by T.createDateTime desc ")
  Page<TurQueryTenantUserRes> searchTenantUser(
      @Param("tenantId") String tenantId,
      @Param("keyword") String searchKeyWord,
      Pageable pageable);

  long countByTenantId(String tenantId);
}
