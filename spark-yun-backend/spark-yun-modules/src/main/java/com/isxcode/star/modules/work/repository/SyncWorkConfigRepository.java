package com.isxcode.star.modules.work.repository;

import com.isxcode.star.modules.work.entity.SyncWorkConfigEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_sync_work"})
public interface SyncWorkConfigRepository extends JpaRepository<SyncWorkConfigEntity, String> {

  SyncWorkConfigEntity findByWorkId(String workId);
}
