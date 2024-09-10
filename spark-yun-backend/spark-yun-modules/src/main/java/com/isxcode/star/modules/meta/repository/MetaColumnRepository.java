package com.isxcode.star.modules.meta.repository;

import com.isxcode.star.api.main.constants.ModuleVipCode;
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

    @Query("SELECT M FROM MetaColumnEntity M WHERE  M.columnName LIKE %:keyword% OR M.columnComment LIKE %:keyword% order by M.createDateTime desc")
    Page<MetaColumnEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);

    List<MetaColumnEntity> queryAllByDatasourceIdAndTableName(String datasourceId, String tableName);
}
