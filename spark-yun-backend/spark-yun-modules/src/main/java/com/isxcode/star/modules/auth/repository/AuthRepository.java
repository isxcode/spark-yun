package com.isxcode.star.modules.auth.repository;

import java.util.List;
import java.util.Optional;

import com.isxcode.star.api.main.constants.ModuleVipCode;
import com.isxcode.star.modules.auth.entity.AuthEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleVipCode.VIP_SSO_AUTH})
public interface AuthRepository extends JpaRepository<AuthEntity, String> {

    @Query("select A from AuthEntity A where A.name LIKE %:keyword% or A.remark LIKE %:keyword% order by A.createDateTime desc")
    Page<AuthEntity> pageSsoAuth(@Param("keyword") String searchKeyWord, Pageable pageable);

    Optional<AuthEntity> findByName(String name);

    Optional<AuthEntity> findByClientId(String clientId);

    List<AuthEntity> findAllByStatus(String status);
}
