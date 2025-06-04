package com.isxcode.star.modules.model.repository;

import com.isxcode.star.api.model.ao.DataModelColumnAo;
import com.isxcode.star.modules.model.entity.DataModelColumnEntity;
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
@CacheConfig(cacheNames = {"SY_DATA_MODEL_COLUMN"})
public interface DataModelColumnRepository extends JpaRepository<DataModelColumnEntity, String> {

    @Query("SELECT new com.isxcode.star.api.model.ao.DataModelColumnAo (D.id,D.name,D.columnName,D.remark,C.name,C.columnTypeCode,C.columnType,C.isNull,C.isDuplicate,C.isPartition,C.isPrimary,C.defaultValue  ) FROM DataModelColumnEntity D left join ColumnFormatEntity C on D.columnFormatId = C.id WHERE D.modelId = :modelId AND (D.id = :keyword OR D.name LIKE %:keyword% OR D.remark LIKE %:keyword%) order by D.columnIndex asc ")
    Page<DataModelColumnAo> searchAll(@Param("keyword") String searchKeyWord, @Param("modelId") String modelId,
        Pageable pageable);

    boolean existsByModelIdAndName(String modelId, String name);

    Optional<DataModelColumnEntity> findByNameAndModelId(String name, String modelId);

    List<DataModelColumnEntity> findAllByModelId(String modelId);

    long countByModelId(String modelId);
}
