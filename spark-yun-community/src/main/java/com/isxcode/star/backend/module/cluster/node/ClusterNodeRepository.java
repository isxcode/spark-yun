package com.isxcode.star.backend.module.cluster.node;

import java.util.List;
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
public interface ClusterNodeRepository extends JpaRepository<ClusterNodeEntity, String> {

  Page<ClusterNodeEntity> findAllByClusterId(String clusterId, Pageable pageable);

  List<ClusterNodeEntity> findAllByClusterId(String clusterId);

  List<ClusterNodeEntity> findAllByClusterIdAndStatus(String clusterId, String status);

  @Query(
      "SELECT E FROM ClusterNodeEntity E"
          + " WHERE E.clusterId = :engineId  "
          + "and ( E.name LIKE %:keyword% "
          + "OR E.remark LIKE %:keyword% "
          + "OR E.host LIKE %:keyword%) order by E.createDateTime desc ")
  Page<ClusterNodeEntity> searchAll(
      @Param("keyword") String searchKeyWord,
      @Param("engineId") String engineId,
      Pageable pageable);
}
