package com.isxcode.spark.modules.meta.repository;

import com.isxcode.spark.api.main.constants.ModuleVipCode;
import com.isxcode.spark.api.meta.ao.MetaDbLinageAo;
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

}
