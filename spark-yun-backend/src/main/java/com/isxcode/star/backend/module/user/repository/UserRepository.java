package com.isxcode.star.backend.module.user.repository;

import com.isxcode.star.backend.module.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * 只负责数据库查询逻辑
 *
 * @ispong
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    UserEntity findFirstByAccount(String account);
}
