package com.isxcode.spark.modules.file.repository;

import com.isxcode.spark.api.main.constants.ModuleCode;
import com.isxcode.spark.modules.file.entity.LibPackageEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {ModuleCode.FILE})
public interface LibPackageRepository extends JpaRepository<LibPackageEntity, String> {

    Optional<LibPackageEntity> findByName(String libPackageName);

    @Query("SELECT F FROM FileEntity F WHERE (:type is null OR F.fileType = :type OR :type='') and ( F.fileName LIKE %:keyword% OR F.remark LIKE %:keyword% ) order by F.createDateTime desc ")
    Page<LibPackageEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);
}
