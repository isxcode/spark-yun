package com.isxcode.spark.modules.meta.repository;

import com.isxcode.spark.api.main.constants.ModuleVipCode;
import com.isxcode.spark.api.meta.ao.MetaColumnAo;
import com.isxcode.spark.modules.meta.entity.MetaColumnEntity;
import com.isxcode.spark.modules.meta.entity.MetaColumnId;
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
@CacheConfig(cacheNames = {ModuleVipCode.VIP_META})
public interface MetaColumnRepository extends JpaRepository<MetaColumnEntity, MetaColumnId> {

    void deleteAllByDatasourceIdAndTableName(String datasourceId, String tableName);

    @Query("SELECT new com.isxcode.spark.api.meta.ao.MetaColumnAo(M.datasourceId,M.tableName,M.columnName,M.columnComment,M.columnType,MI.customComment,M.lastModifiedDateTime) FROM MetaColumnEntity M left join MetaColumnInfoEntity MI on M.datasourceId=MI.datasourceId and M.tableName = MI.tableName and M.columnName=MI.columnName WHERE M.tenantId=:tenantId AND (M.columnName LIKE %:keyword% OR (MI.customComment is null and M.columnComment LIKE %:keyword% ) OR M.tableName LIKE %:keyword% OR MI.customComment LIKE %:keyword%  ) order by M.datasourceId asc,M.tableName asc, M.createDateTime desc")
    Page<MetaColumnAo> searchAll(@Param("tenantId") String tenantId, @Param("keyword") String searchKeyWord,
        Pageable pageable);

    @Query("SELECT new com.isxcode.spark.api.meta.ao.MetaColumnAo(M.datasourceId,M.tableName,M.columnName,M.columnComment,M.columnType,MI.customComment,M.lastModifiedDateTime) FROM MetaColumnEntity M left join MetaColumnInfoEntity MI on M.datasourceId=MI.datasourceId and M.tableName = MI.tableName and M.columnName=MI.columnName WHERE M.tenantId=:tenantId AND M.datasourceId=:datasourceId AND M.tableName = :tableName")
    List<MetaColumnAo> queryAllByDatasourceIdAndTableName(@Param("tenantId") String tenantId, String datasourceId,
        String tableName);

    Optional<MetaColumnEntity> findByDatasourceIdAndTableNameAndColumnName(String datasourceId, String tableName,
        String columnName);
}
