package com.isxcode.star.common.userlog;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {"sy_users"})
public interface UserActionRepository extends JpaRepository<UserActionEntity, String> {}
