package com.isxcode.spark.modules.meta.repository;

import com.isxcode.spark.api.main.constants.ModuleVipCode;
import com.isxcode.spark.api.meta.ao.MetaColumnLinageAo;
import com.isxcode.spark.api.meta.ao.MetaDbLinageAo;
import com.isxcode.spark.api.meta.ao.MetaTableLinageAo;
import com.isxcode.spark.modules.meta.entity.MetaColumnLineageEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@CacheConfig(cacheNames = {ModuleVipCode.VIP_META})
public interface MetaColumnLineageRepository extends JpaRepository<MetaColumnLineageEntity, String> {

    List<MetaColumnLineageEntity> findAllByWorkId(String workId);

    @Query("select new com.isxcode.spark.api.meta.ao.MetaDbLinageAo(M.fromDbId, M.toDbId) from MetaColumnLineageEntity M where M.toDbId = :toDbId and M.tenantId = :tenantId group by M.fromDbId, M.toDbId")
    List<MetaDbLinageAo> getDbParentLineage(@Param("tenantId") String tenantId, String toDbId);

    @Query("select new com.isxcode.spark.api.meta.ao.MetaDbLinageAo(M.fromDbId, M.toDbId) from MetaColumnLineageEntity M where M.fromDbId = :fromDbId and M.tenantId = :tenantId group by M.fromDbId, M.toDbId")
    List<MetaDbLinageAo> getDbSonLineage(@Param("tenantId") String tenantId, String fromDbId);

    @Query("select new com.isxcode.spark.api.meta.ao.MetaTableLinageAo(M.fromDbId, M.fromTableName, M.toDbId, M.toTableName) from MetaColumnLineageEntity M where M.toDbId = :toDbId and M.toTableName = :toTableName and M.tenantId = :tenantId group by M.fromDbId, M.toDbId, M.fromTableName,M.toTableName")
    List<MetaTableLinageAo> getTableParentLineage(@Param("tenantId") String tenantId, String toDbId,
        String toTableName);

    @Query("select new com.isxcode.spark.api.meta.ao.MetaTableLinageAo(M.fromDbId, M.fromTableName, M.toDbId,M.toTableName) from MetaColumnLineageEntity M where M.fromDbId = :fromDbId and M.fromTableName = :fromTableName and M.tenantId = :tenantId group by M.fromDbId, M.toDbId, M.fromTableName,M.toTableName")
    List<MetaTableLinageAo> getTableSonLineage(@Param("tenantId") String tenantId, String fromDbId,
        String fromTableName);

    @Query("select new com.isxcode.spark.api.meta.ao.MetaColumnLinageAo(M.id, M.fromDbId, M.fromTableName,M.fromColumnName, M.toDbId, M.toTableName,M.fromColumnName,D.dbType,D.name,M.remark,M.workVersionId,M.workId,W.name) from MetaColumnLineageEntity M left join DatasourceEntity D on M.fromDbId = D.id left join WorkEntity W on M.workId = W.id where M.toDbId = :toDbId and M.toTableName = :toTableName and M.toColumnName = :toColumnName and M.tenantId = :tenantId ")
    List<MetaColumnLinageAo> getColumnParentLineage(@Param("tenantId") String tenantId, String toDbId,
        String toTableName, String toColumnName);

    @Query("select new com.isxcode.spark.api.meta.ao.MetaColumnLinageAo(M.id, M.fromDbId, M.fromTableName,M.fromColumnName, M.toDbId, M.toTableName,M.fromColumnName,D.dbType,D.name,M.remark,M.workVersionId,M.workId,W.name) from MetaColumnLineageEntity M left join DatasourceEntity D on M.toDbId = D.id left join WorkEntity W on M.workId = W.id where M.fromDbId = :fromDbId and M.fromTableName = :fromTableName and M.fromColumnName = :fromColumnName and M.tenantId = :tenantId ")
    List<MetaColumnLinageAo> getColumnSonLineage(@Param("tenantId") String tenantId, String fromDbId,
        String fromTableName, String fromColumnName);
}
