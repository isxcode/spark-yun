package com.isxcode.spark.modules.workflow.repository;

import com.isxcode.spark.api.main.constants.ModuleCode;
import com.isxcode.spark.modules.workflow.entity.WorkflowConfigEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleCode.WORKFLOW})
public interface WorkflowConfigRepository extends JpaRepository<WorkflowConfigEntity, String> {
}
