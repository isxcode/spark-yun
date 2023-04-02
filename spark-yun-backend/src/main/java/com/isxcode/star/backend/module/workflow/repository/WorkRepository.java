package com.isxcode.star.backend.module.workflow.repository;

import com.isxcode.star.backend.module.workflow.entity.WorkEntity;
import java.util.List;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_work"})
public interface WorkRepository extends JpaRepository<WorkEntity, String> {

  List<WorkEntity> findAllByWorkflowId(String workflowId);
}
