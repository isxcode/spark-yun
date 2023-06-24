package com.isxcode.star.backend.module.cluster;

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
public interface ClusterRepository extends JpaRepository<ClusterEntity, String> {

  @Query(
      "SELECT C FROM ClusterEntity C WHERE C.name LIKE %:keyword% OR C.remark LIKE %:keyword% order by C.createDateTime desc ")
  Page<ClusterEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);
}
