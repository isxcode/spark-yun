package com.isxcode.star.backend.module.work.repository;

import com.isxcode.star.backend.module.work.entity.WorkEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_work"})
public interface WorkRepository extends JpaRepository<WorkEntity, String> {

  List<WorkEntity> findAllByWorkflowId(String workflowId);

  Page<WorkEntity> findAllByWorkflowId(String workflowId, Pageable pageable);

  @Query("SELECT w FROM WorkEntity w WHERE w.workflowId = :workflowId AND (w.name LIKE %:keyword% OR w.commentInfo LIKE %:keyword% OR w.workType LIKE %:keyword%)")
  Page<WorkEntity> searchAllByWorkflowId(@Param("keyword") String searchKeyWord, @Param("workflowId") String workflowId, Pageable pageable);
}
