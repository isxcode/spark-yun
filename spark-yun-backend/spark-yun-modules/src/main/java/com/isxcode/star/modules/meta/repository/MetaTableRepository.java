package com.isxcode.star.modules.meta.repository;

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

    @Query("SELECT M FROM MetaTableEntity M WHERE (:datasourceId is null OR M.datasourceId = :datasourceId OR :datasourceId='') and ( M.tableName LIKE %:keyword% OR M.tableComment LIKE %:keyword% ) order by  M.createDateTime desc")
    Page<MetaTableEntity> searchAll(@Param("keyword") String searchKeyWord, @Param("datasourceId") String datasourceId,
        Pageable pageable);

    void deleteAllByDatasourceIdAndTableNameIn(String datasourceId, List<String> tableName);

    void deleteAllByDatasourceId(String datasourceId);

    Optional<MetaTableEntity> findByDatasourceIdAndTableName(String datasourceId, String tableName);
}
