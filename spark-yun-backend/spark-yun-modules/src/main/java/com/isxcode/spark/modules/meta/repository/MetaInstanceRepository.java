package com.isxcode.spark.modules.meta.repository;

import com.isxcode.spark.api.main.constants.ModuleVipCode;
import com.isxcode.spark.modules.meta.entity.MetaInstanceEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleVipCode.VIP_META})
public interface MetaInstanceRepository extends JpaRepository<MetaInstanceEntity, String> {

    @Query("SELECT M FROM MetaInstanceEntity M order by M.createDateTime desc")
    Page<MetaInstanceEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);
}
