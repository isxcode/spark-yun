package com.isxcode.star.modules.view.repository;

import com.isxcode.star.modules.view.entity.ViewCardEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {"SY_VIEW_CARD"})
public interface ViewCardRepository extends JpaRepository<ViewCardEntity, String> {

    Optional<ViewCardEntity> findByName(String name);

    @Query("select V from ViewCardEntity V where V.name LIKE %:keyword% or V.remark LIKE %:keyword% order by V.createDateTime desc")
    Page<ViewCardEntity> pageViewCard(@Param("keyword") String searchKeyWord, Pageable pageable);

    Optional<Object> findVieFwCaFrdEntityByName(String name);
}
