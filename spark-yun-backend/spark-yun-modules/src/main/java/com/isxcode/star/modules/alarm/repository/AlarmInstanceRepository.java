package com.isxcode.star.modules.alarm.repository;

import com.isxcode.star.modules.alarm.entity.AlarmInstanceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmInstanceRepository extends JpaRepository<AlarmInstanceEntity, String> {

    @Query("SELECT A FROM AlarmInstanceEntity A order by A.createDateTime desc ")
    Page<AlarmInstanceEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);
}
