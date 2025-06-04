package com.isxcode.star.modules.model.repository;

import com.isxcode.star.modules.model.entity.DataModelEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {"SY_DATA_MODEL"})
public interface DataModelRepository extends JpaRepository<DataModelEntity, String> {

    @Query("SELECT D FROM DataModelEntity D WHERE D.id = :keyword OR D.name LIKE %:keyword% OR D.remark LIKE %:keyword% order by D.createDateTime desc ")
    Page<DataModelEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);

    @Query("SELECT D FROM DataModelEntity D WHERE D.layerId = :layerId AND (D.id = :keyword OR D.name LIKE %:keyword% OR D.remark LIKE %:keyword%) order by D.createDateTime desc ")
    Page<DataModelEntity> searchAllByLayerId(@Param("keyword") String searchKeyWord, @Param("layerId") String layerId,
        Pageable pageable);

    boolean existsByName(String name);

    Optional<DataModelEntity> findByName(String name);
}
