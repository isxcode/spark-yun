package com.isxcode.star.backend.module.work.file;

import java.util.List;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_work_file"})
public interface WorkFileRepository extends JpaRepository<WorkFileEntity, String> {

  @Query(
      value = "SELECT w.* FROM SY_WORK_FILE w WHERE 1=1 "
              + "AND w.work_id = ?2 "
              + "AND (?1 is null or w.file_type LIKE CONCAT('%',?1,'%')) "
              + "order by w.create_date_time desc ",
      nativeQuery = true)
  List<WorkFileEntity> searchAllByWorkId(
      @Param("type") String type, @Param("workId") String workId);
}
