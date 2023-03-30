package com.isxcode.star.backend.module.node.repository;

import com.isxcode.star.backend.module.node.entity.NodeEntity;
import java.util.List;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_engines"})
public interface NodeRepository extends JpaRepository<NodeEntity, String> {

  List<NodeEntity> findAllByEngineId(String engineId);
}
