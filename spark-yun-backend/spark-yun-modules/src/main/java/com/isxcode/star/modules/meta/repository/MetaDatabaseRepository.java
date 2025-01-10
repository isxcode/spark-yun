package com.isxcode.star.modules.meta.repository;

import com.isxcode.star.api.main.constants.ModuleVipCode;
import com.isxcode.star.modules.meta.entity.MetaDatabaseEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {ModuleVipCode.VIP_META})
public interface MetaDatabaseRepository extends JpaRepository<MetaDatabaseEntity, String> {

    @Query("SELECT M FROM MetaDatabaseEntity M WHERE M.dbName LIKE %:keyword% OR M.dbComment LIKE %:keyword% order by M.createDateTime desc,M.dbName asc")
    Page<MetaDatabaseEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);

    Optional<MetaDatabaseEntity> findByDatasourceId(String datasourceId);
}
