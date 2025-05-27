package com.isxcode.star.modules.layer.repository;

import com.isxcode.star.modules.layer.entity.LayerEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {"SY_TABLE_LAYER"})
public interface LayerRepository extends JpaRepository<LayerEntity, String> {

    @Query("SELECT L FROM LayerEntity L WHERE ((:parentLayerId is null AND L.parentLayerId is null) OR  L.parentLayerId = :parentLayerId ) AND (L.name LIKE %:keyword% OR L.remark LIKE %:keyword%)  order by L.createDateTime desc ")
    Page<LayerEntity> pageLayer(@Param("keyword") String searchKeyWord, @Param("parentLayerId") String parentLayerId,
        Pageable pageable);

    @Query("SELECT L FROM LayerEntity L WHERE L.id  LIKE %:keyword% OR L.name LIKE %:keyword% OR L.remark LIKE %:keyword% order by L.createDateTime desc ")
    Page<LayerEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);

    boolean existsByNameAndParentLayerId(String name, String parentLayerId);

    Optional<LayerEntity> findByNameAndParentLayerId(String name, String parentLayerId);

    List<LayerEntity> findAllByParentIdListLike(String parentIdList);
}
