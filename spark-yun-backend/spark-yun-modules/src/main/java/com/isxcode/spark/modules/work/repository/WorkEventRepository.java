package com.isxcode.spark.modules.work.repository;

import com.isxcode.spark.api.main.constants.ModuleCode;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleCode.WORK})
public interface WorkEventRepository extends JpaRepository<WorkEventEntity, String> {

    boolean existsByIdAndEventProcess(String id, Integer eventProcess);

    default void deleteByIdAndFlush(String id) {
        deleteById(id);
        flush();
    }
}
