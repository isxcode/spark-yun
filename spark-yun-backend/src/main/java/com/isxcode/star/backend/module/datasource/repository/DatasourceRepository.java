package com.isxcode.star.backend.module.datasource.repository;

import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_datasources"})
public interface DatasourceRepository extends JpaRepository<DatasourceEntity, String> {}
