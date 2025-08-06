package com.isxcode.spark.modules.form.repository;

import com.isxcode.spark.modules.form.entity.FormLinkEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@CacheConfig(cacheNames = {"SY_FORM"})
public interface FormLinkRepository extends JpaRepository<FormLinkEntity, String> {

    List<FormLinkEntity> findAllByInvalidDateTimeBefore(LocalDateTime localDateTime);
}
