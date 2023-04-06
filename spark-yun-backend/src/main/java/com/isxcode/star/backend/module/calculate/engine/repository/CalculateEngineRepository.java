package com.isxcode.star.backend.module.calculate.engine.repository;

import com.isxcode.star.backend.module.calculate.engine.entity.CalculateEngineEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 计算引擎模块. */
@Repository
@CacheConfig(cacheNames = {"SY_CALCULATE_ENGINE"})
public interface CalculateEngineRepository extends JpaRepository<CalculateEngineEntity, String> {}
