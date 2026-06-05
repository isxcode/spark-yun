package com.isxcode.spark.common.locker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.isxcode.spark.api.main.constants.ModuleCode;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleCode.WORK})
public interface LockerRepository extends JpaRepository<LockerEntity, Integer> {

    List<LockerEntity> findAllByName(String name);

    Optional<LockerEntity> findByName(String name);

    Optional<LockerEntity> findByBox(String box);

    Boolean existsByName(String name);

    void deleteByName(String name);

    Optional<LockerEntity> findByNameAndBox(String name, String box);

    void deleteAllByOwner(String owner);

    void deleteAllByExpireTimeBefore(LocalDateTime expireTime);
}
