package com.isxcode.spark.modules.meta.repository;

import com.isxcode.spark.api.main.constants.ModuleVipCode;
import com.isxcode.spark.modules.meta.entity.MetaColumnId;
import com.isxcode.spark.modules.meta.entity.MetaColumnInfoEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {ModuleVipCode.VIP_META})
public interface MetaColumnInfoRepository extends JpaRepository<MetaColumnInfoEntity, MetaColumnId> {

    Optional<MetaColumnInfoEntity> findByDatasourceIdAndTableNameAndColumnName(String datasourceId, String tableName,
        String columnName);
}
