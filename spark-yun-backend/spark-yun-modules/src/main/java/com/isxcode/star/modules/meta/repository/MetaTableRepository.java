package com.isxcode.star.modules.meta.repository;

import com.isxcode.star.api.meta.ao.MetaTableAo;
import com.isxcode.star.api.main.constants.ModuleVipCode;
import com.isxcode.star.modules.meta.entity.MetaTableEntity;
import com.isxcode.star.modules.meta.entity.MetaTableId;
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
public interface MetaTableRepository extends JpaRepository<MetaTableEntity, MetaTableId> {

    @Query("SELECT new com.isxcode.star.api.meta.ao.MetaTableAo(M.datasourceId,M.tableName,M.tableComment,MT.customComment,M.lastModifiedDateTime) FROM MetaTableEntity M left join MetaTableInfoEntity MT on M.datasourceId=MT.datasourceId and M.tableName = MT.tableName WHERE (:datasourceId is null OR M.datasourceId = :datasourceId OR :datasourceId='') and ( M.tableName LIKE %:keyword% OR (MT.customComment is null and M.tableComment LIKE %:keyword%) OR MT.customComment LIKE %:keyword% ) and M.tenantId=:tenantId order by M.tableName asc, M.createDateTime desc")
    Page<MetaTableAo> searchAll(@Param("tenantId") String tenantId, @Param("keyword") String searchKeyWord,
        @Param("datasourceId") String datasourceId, Pageable pageable);

    void deleteAllByDatasourceIdAndTableNameIn(String datasourceId, List<String> tableName);

    void deleteAllByDatasourceId(String datasourceId);

    Optional<MetaTableEntity> findByDatasourceIdAndTableName(String datasourceId, String tableName);
}
