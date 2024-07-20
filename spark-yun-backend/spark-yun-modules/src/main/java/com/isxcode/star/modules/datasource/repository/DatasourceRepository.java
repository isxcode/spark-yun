package com.isxcode.star.modules.datasource.repository;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
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
@CacheConfig(cacheNames = {ModuleCode.DATASOURCE})
public interface DatasourceRepository extends JpaRepository<DatasourceEntity, String> {

    @Query("SELECT D FROM DatasourceEntity D " + "WHERE ( D.name LIKE %:keyword% " + "OR D.remark LIKE %:keyword% "
        + "OR D.dbType LIKE %:keyword% " + "OR D.username LIKE %:keyword% "
        + "OR D.jdbcUrl LIKE %:keyword% ) and (:datasourceType is null or D.dbType = :datasourceType) order by D.createDateTime desc ")
    Page<DatasourceEntity> searchAll(@Param("keyword") String searchKeyWord,
        @Param("datasourceType") String datasourceType, Pageable pageable);

    List<DatasourceEntity> findAllByDriverId(String driverId);

    Optional<DatasourceEntity> findByName(String name);

    long countByStatus(String status);
}
