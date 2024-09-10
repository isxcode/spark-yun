package com.isxcode.star.modules.form.repository;

import com.isxcode.star.modules.form.entity.FormEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {"SY_FORM"})
public interface FormRepository extends JpaRepository<FormEntity, String> {

    @Query("select F from FormEntity F where F.name LIKE %:keyword% or F.remark LIKE %:keyword% order by F.createDateTime desc")
    Page<FormEntity> pageForm(@Param("keyword") String searchKeyWord, Pageable pageable);

    Optional<FormEntity> findByName(String name);
}
