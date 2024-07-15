package com.isxcode.star.modules.workflow.repository;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.modules.workflow.entity.WorkflowConfigEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleCode.WORKFLOW})
public interface WorkflowConfigRepository extends JpaRepository<WorkflowConfigEntity, String> {
}
