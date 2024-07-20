package com.isxcode.star.modules.file.repository;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.modules.file.entity.FileEntity;
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
public interface FileRepository extends JpaRepository<FileEntity, String> {

    Optional<FileEntity> findByFileName(String fileName);

    @Query("SELECT F FROM FileEntity F WHERE (:type is null OR F.fileType = :type OR :type='') and ( F.fileName LIKE %:keyword% OR F.remark LIKE %:keyword% ) order by F.createDateTime desc ")
    Page<FileEntity> searchAll(@Param("keyword") String searchKeyWord, @Param("type") String type, Pageable pageable);
}
