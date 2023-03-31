package com.isxcode.star.backend.module.workflow.repository;

import com.isxcode.star.backend.module.workflow.entity.WorkEntity;
import com.isxcode.star.backend.module.workflow.entity.WorkflowEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_work"})
public interface WorkRepository extends JpaRepository<WorkEntity, String> {

  List<WorkEntity> findAllByWorkflowId(String workflowId);
}
