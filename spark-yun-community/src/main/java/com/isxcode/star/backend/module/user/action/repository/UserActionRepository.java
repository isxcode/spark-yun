package com.isxcode.star.backend.module.user.action.repository;

import com.isxcode.star.backend.module.user.action.entity.UserActionEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {"sy_users"})
public interface UserActionRepository extends JpaRepository<UserActionEntity, String> {

}
