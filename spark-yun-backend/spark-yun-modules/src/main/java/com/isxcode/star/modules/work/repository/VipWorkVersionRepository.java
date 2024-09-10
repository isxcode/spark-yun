package com.isxcode.star.modules.work.repository;

import com.isxcode.star.api.main.constants.ModuleVipCode;
import com.isxcode.star.modules.work.entity.VipWorkVersionEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleVipCode.VIP_WORK})
public interface VipWorkVersionRepository extends JpaRepository<VipWorkVersionEntity, String> {
}
