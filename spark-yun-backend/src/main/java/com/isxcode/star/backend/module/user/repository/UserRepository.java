package com.isxcode.star.backend.module.user.repository;

import com.isxcode.star.backend.module.user.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_users"})
public interface UserRepository extends JpaRepository<UserEntity, String> {

  UserEntity findFirstByAccount(String account);

  @Override
  @Cacheable(key = "#id")
  Optional<UserEntity> findById(String id);

  @Override
  @Cacheable(key = "'users'")
  List<UserEntity> findAll();

  @Caching(
      evict = {@CacheEvict(key = "'users'")},
      put = {@CachePut(key = "#userEntity.id")})
  @Override
  UserEntity save(UserEntity userEntity);

  @CacheEvict(allEntries = true)
  void deleteByAccount(String account);
}
