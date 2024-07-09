package com.isxcode.star.modules.workflow.repository;

import com.isxcode.star.modules.workflow.entity.WorkflowVersionEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {"sy_workflow"})
public interface WorkflowVersionRepository extends JpaRepository<WorkflowVersionEntity, String> {
}
