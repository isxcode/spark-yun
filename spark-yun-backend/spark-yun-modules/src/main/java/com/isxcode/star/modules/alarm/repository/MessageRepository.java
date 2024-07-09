package com.isxcode.star.modules.alarm.repository;

import com.isxcode.star.modules.alarm.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, String> {

}
