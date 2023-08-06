package com.isxcode.star.modules.workflow.repository;

import com.isxcode.star.modules.workflow.entity.WorkflowFavourEntity;
import java.util.Optional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_workflow_favour"})
public interface WorkflowFavourRepository extends JpaRepository<WorkflowFavourEntity, String> {

  Optional<WorkflowFavourEntity> findByWorkflowIdAndUserId(String workflowId, String userId);
}
