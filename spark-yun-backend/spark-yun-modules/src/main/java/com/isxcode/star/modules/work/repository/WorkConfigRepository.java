package com.isxcode.star.modules.work.repository;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleCode.WORK})
public interface WorkConfigRepository extends JpaRepository<WorkConfigEntity, String> {
}
