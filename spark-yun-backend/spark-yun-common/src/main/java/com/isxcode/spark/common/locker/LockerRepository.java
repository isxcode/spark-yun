package com.isxcode.spark.common.locker;

import java.util.List;
import java.util.Optional;

import com.isxcode.spark.api.main.constants.ModuleCode;
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

    @Query(
        value = "select L.box from LockerEntity L where L.id = (select min(L2.id) from LockerEntity L2 where L2.name =:name )")
    String getMinBox(@Param("name") String name);

    List<LockerEntity> findAllByName(String name);

    Optional<LockerEntity> findByName(String name);

    Optional<LockerEntity> findByBox(String box);

    Boolean existsByName(String name);

    void deleteByName(String name);

    Optional<LockerEntity> findByNameAndBox(String name, String box);
}
