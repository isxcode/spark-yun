package com.isxcode.star.modules.alarm.repository;

import com.isxcode.star.modules.alarm.entity.AlarmEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, String> {

    @Query("SELECT A FROM AlarmEntity A WHERE A.name LIKE %:keyword% OR A.remark LIKE %:keyword% order by A.createDateTime desc ")
    Page<AlarmEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);

    Optional<AlarmEntity> findByName(String name);
}
