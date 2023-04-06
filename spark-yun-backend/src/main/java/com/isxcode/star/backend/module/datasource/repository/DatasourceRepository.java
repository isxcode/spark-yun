package com.isxcode.star.backend.module.datasource.repository;

import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 数据源模块. */
@Repository
@CacheConfig(cacheNames = {"SY_DATASOURCE"})
public interface DatasourceRepository extends JpaRepository<DatasourceEntity, String> {}
