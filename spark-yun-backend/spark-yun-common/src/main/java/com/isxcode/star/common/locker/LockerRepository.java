package com.isxcode.star.common.locker;

import java.util.List;

import com.isxcode.star.api.main.constants.ModuleCode;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleCode.WORK})
public interface LockerRepository extends JpaRepository<LockerEntity, Integer> {

    @Query(value = "select min(L.id) from LockerEntity L where L.name = :name")
    Integer getMinId(@Param("name") String name);

    List<LockerEntity> findAllByName(String name);
}
