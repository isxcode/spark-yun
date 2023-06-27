package com.isxcode.star.backend.module.user.jwt;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = {"sy_jwt"})
public class UserJwtCache {

  @CachePut(key = "#userJwtEntity.userId")
  public UserJwtEntity setJwt(UserJwtEntity userJwtEntity) {
    return userJwtEntity;
  }

  @CacheEvict(key = "#userId")
  public void clearJwt(String userId) {}

  @Cacheable(key = "#userId")
  public UserJwtEntity getJwt(String userId) {
    return new UserJwtEntity();
  }
}
