package com.isxcode.spark.modules.model.repository;

import com.isxcode.spark.modules.model.entity.ColumnFormatEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {"SY_COLUMN_FORMAT"})
public interface ColumnFormatRepository extends JpaRepository<ColumnFormatEntity, String> {

    @Query("SELECT C FROM ColumnFormatEntity C WHERE C.id = :keyword OR C.name LIKE %:keyword% OR C.remark LIKE %:keyword% order by C.createDateTime desc ")
    Page<ColumnFormatEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);

    boolean existsByName(String name);

    Optional<ColumnFormatEntity> findByName(String name);
}
