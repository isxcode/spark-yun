package com.isxcode.spark.modules.secret.repository;

import java.util.Optional;

import com.isxcode.spark.api.main.constants.ModuleVipCode;
import com.isxcode.spark.modules.secret.entity.SecretKeyEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleVipCode.VIP_API})
public interface SecretKeyRepository extends JpaRepository<SecretKeyEntity, String> {

    @Query("select S from SecretKeyEntity S where S.keyName LIKE %:keyword% or S.remark LIKE %:keyword% order by S.createDateTime desc")
    Page<SecretKeyEntity> pageSecret(@Param("keyword") String searchKeyWord, Pageable pageable);

    Optional<SecretKeyEntity> findByKeyName(String keyName);
}
