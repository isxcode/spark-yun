package com.isxcode.star.backend.module.engine.node.repository;

import com.isxcode.star.backend.module.engine.node.entity.EngineNodeEntity;
import java.util.List;

import com.isxcode.star.backend.module.workflow.entity.WorkflowEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"SY_ENGINE_NODE"})
public interface EngineNodeRepository extends JpaRepository<EngineNodeEntity, String> {

  Page<EngineNodeEntity> findAllByCalculateEngineId(String engineId, Pageable pageable);

  List<EngineNodeEntity> findAllByCalculateEngineId(String engineId);

  List<EngineNodeEntity> findAllByCalculateEngineIdAndStatus(String engineId, String status);

  @Query("SELECT E FROM EngineNodeEntity E WHERE E.calculateEngineId = :engineId  and ( E.name LIKE %:keyword% OR E.commentInfo LIKE %:keyword% OR E.host LIKE %:keyword%)")
  Page<EngineNodeEntity> searchAll(@Param("keyword") String searchKeyWord, @Param("engineId") String engineId, Pageable pageable);
}
