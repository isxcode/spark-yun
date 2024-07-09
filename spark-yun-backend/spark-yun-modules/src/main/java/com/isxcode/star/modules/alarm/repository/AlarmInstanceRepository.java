package com.isxcode.star.modules.alarm.repository;

import com.isxcode.star.modules.alarm.entity.AlarmInstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmInstanceRepository extends JpaRepository<AlarmInstanceEntity, String> {

}
