package com.isxcode.star.modules.meta.repository;

import com.isxcode.star.api.main.constants.ModuleVipCode;
import com.isxcode.star.modules.meta.entity.MetaColumnId;
import com.isxcode.star.modules.meta.entity.MetaColumnInfoEntity;
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
