package com.isxcode.star.modules.work.repository;

import com.isxcode.star.modules.work.entity.UdfEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"SY_WORK_UDF"})
public interface UdfRepository extends JpaRepository<UdfEntity, String> {
  @Query("SELECT u FROM UdfEntity u " + "WHERE " + "(u.funcName LIKE %:searchKeyWord% "
    + "OR u.className LIKE %:searchKeyWord% "
    + "OR u.type LIKE %:searchKeyWord% "
    + "OR u.resultType LIKE %:searchKeyWord%) order by u.createDateTime desc")
  Page<UdfEntity> pageSearch(@Param("searchKeyWord") String searchKeyWord, Pageable pageable);

  //todo 增加租户过滤
  List<UdfEntity> findAllByStatus(Boolean status);

}
