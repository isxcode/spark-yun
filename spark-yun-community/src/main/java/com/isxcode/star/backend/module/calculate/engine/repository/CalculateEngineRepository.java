package com.isxcode.star.backend.module.calculate.engine.repository;

import com.isxcode.star.backend.module.calculate.engine.entity.CalculateEngineEntity;
import com.isxcode.star.backend.module.workflow.entity.WorkflowEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 计算引擎模块. */
@Repository
@CacheConfig(cacheNames = {"SY_CALCULATE_ENGINE"})
public interface CalculateEngineRepository extends JpaRepository<CalculateEngineEntity, String> {

  @Query("SELECT C FROM CalculateEngineEntity C WHERE C.name LIKE %:keyword% OR C.remark LIKE %:keyword%")
  Page<CalculateEngineEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);
}
