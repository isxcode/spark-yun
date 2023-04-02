package com.isxcode.star.backend.module.workflow.repository;

import com.isxcode.star.backend.module.workflow.entity.WorkConfigEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_work"})
public interface WorkConfigRepository extends JpaRepository<WorkConfigEntity, String> {}
