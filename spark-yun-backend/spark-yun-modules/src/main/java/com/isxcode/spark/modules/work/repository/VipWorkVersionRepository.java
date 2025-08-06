package com.isxcode.spark.modules.work.repository;

import com.isxcode.spark.api.main.constants.ModuleVipCode;
import com.isxcode.spark.modules.work.entity.VipWorkVersionEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleVipCode.VIP_WORK})
public interface VipWorkVersionRepository extends JpaRepository<VipWorkVersionEntity, String> {
}
