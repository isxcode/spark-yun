package com.isxcode.star.modules.real.repository;

import com.isxcode.star.modules.real.entity.RealEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {"SY_REAL"})
public interface RealRepository extends JpaRepository<RealEntity, String> {

    @Query("SELECT R FROM RealEntity R WHERE R.name LIKE %:keyword% OR R.remark LIKE %:keyword% order by R.createDateTime desc ")
    Page<RealEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);
}
