package com.isxcode.star.backend.module.workflow.instance;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {"sy_workflow"})
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstanceEntity, String> {

  @CachePut(key = "#workflowInstanceId")
  default String setWorkflowLog(String workflowInstanceId, String runLog) {

    return runLog;
  }

  @Cacheable(key = "#workflowInstanceId")
  default String getWorkflowLog(String workflowInstanceId) {

    return "";
  }

  @CacheEvict(key = "#workflowInstanceId")
  default void deleteWorkflowLog(String workflowInstanceId) {}
}
