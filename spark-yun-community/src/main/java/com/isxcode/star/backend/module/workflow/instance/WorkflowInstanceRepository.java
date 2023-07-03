package com.isxcode.star.backend.module.workflow.instance;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {"sy_workflow"})
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstanceEntity, String> {
}
