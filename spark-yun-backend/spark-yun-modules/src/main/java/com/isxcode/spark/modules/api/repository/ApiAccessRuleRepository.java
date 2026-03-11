package com.isxcode.spark.modules.api.repository;

import com.isxcode.spark.api.main.constants.ModuleVipCode;
import com.isxcode.spark.modules.api.entity.ApiAccessRuleEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleVipCode.VIP_API})
public interface ApiAccessRuleRepository extends JpaRepository<ApiAccessRuleEntity, String> {

    @Query("select A from ApiAccessRuleEntity A where A.name LIKE %:keyword% or A.ruleType LIKE %:keyword% or A.ipAddress LIKE %:keyword% or A.remark LIKE %:keyword% order by A.createDateTime desc")
    Page<ApiAccessRuleEntity> pageAccessRule(@Param("keyword") String searchKeyWord, Pageable pageable);
}
