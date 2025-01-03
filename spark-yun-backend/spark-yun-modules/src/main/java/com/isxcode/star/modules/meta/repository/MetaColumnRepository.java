package com.isxcode.star.modules.meta.repository;

import com.isxcode.star.api.main.constants.ModuleVipCode;
import com.isxcode.star.api.meta.pojos.ao.MetaColumnAo;
import com.isxcode.star.modules.meta.entity.MetaColumnEntity;
import com.isxcode.star.modules.meta.entity.MetaColumnId;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@CacheConfig(cacheNames = {ModuleVipCode.VIP_META})
public interface MetaColumnRepository extends JpaRepository<MetaColumnEntity, MetaColumnId> {

    void deleteAllByDatasourceIdAndTableName(String datasourceId, String tableName);

    @Query("SELECT new com.isxcode.star.api.meta.pojos.ao.MetaColumnAo(M.datasourceId,M.tableName,M.columnName,M.columnComment,M.columnType,MI.customComment,M.lastModifiedDateTime) FROM MetaColumnEntity M left join MetaColumnInfoEntity MI on M.datasourceId=MI.datasourceId and M.tableName = MI.tableName and M.columnName=MI.columnName WHERE M.tenantId=:tenantId AND (M.columnName LIKE %:keyword% OR M.columnComment LIKE %:keyword% OR M.tableName LIKE %:keyword%  ) order by M.createDateTime desc")
    Page<MetaColumnAo> searchAll(@Param("tenantId") String tenantId, @Param("keyword") String searchKeyWord,
        Pageable pageable);

    List<MetaColumnEntity> queryAllByDatasourceIdAndTableName(String datasourceId, String tableName);
}
