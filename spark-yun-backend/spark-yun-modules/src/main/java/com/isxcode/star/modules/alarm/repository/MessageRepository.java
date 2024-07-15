package com.isxcode.star.modules.alarm.repository;

import com.isxcode.star.modules.alarm.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, String> {

    @Query("SELECT M FROM MessageEntity M WHERE M.name LIKE %:keyword% OR M.remark LIKE %:keyword% order by M.createDateTime desc ")
    Page<MessageEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);

    Optional<MessageEntity> findByName(String name);
}
