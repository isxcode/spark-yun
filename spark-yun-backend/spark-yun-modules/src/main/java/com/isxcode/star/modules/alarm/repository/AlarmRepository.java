package com.isxcode.star.modules.alarm.repository;

import com.isxcode.star.modules.alarm.entity.AlarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, String> {

}
