package com.isxcode.star.backend.module.workflow.repository;

import com.isxcode.star.backend.module.workflow.entity.WorkflowEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_engines"})
public interface WorkflowRepository extends JpaRepository<WorkflowEntity, String> {}
