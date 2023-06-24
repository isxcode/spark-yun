package com.isxcode.star.backend.module.user;

import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_users"})
public interface UserRepository extends JpaRepository<UserEntity, String> {

  /** 分页搜索查询所有用户. */
  @Query(
      "SELECT U FROM UserEntity U "
          + "WHERE U.roleCode != 'ROLE_SYS_ADMIN' and "
          + "(U.username LIKE %:keyword% "
          + "OR U.account LIKE %:keyword% "
          + "OR U.email LIKE %:keyword% "
          + "OR U.phone LIKE %:keyword% "
          + "OR U.remark LIKE %:keyword%) order by U.createDateTime desc ")
  Page<UserEntity> searchAllUser(@Param("keyword") String searchKeyWord, Pageable pageable);

  /** 分页搜索查询所有有效用户. */
  @Query(
      "SELECT U FROM UserEntity U "
          + "WHERE U.roleCode != 'ROLE_SYS_ADMIN' "
          + "AND U.status = 'ENABLE' "
          + "AND (U.username LIKE %:keyword%"
          + " OR U.account LIKE %:keyword% "
          + "OR U.email LIKE %:keyword% "
          + "OR U.phone LIKE %:keyword% "
          + "OR U.remark LIKE %:keyword%) order by U.createDateTime desc ")
  Page<UserEntity> searchAllEnableUser(@Param("keyword") String searchKeyWord, Pageable pageable);

  Optional<UserEntity> findByAccount(String account);

  Optional<UserEntity> findByPhone(String phone);

  Optional<UserEntity> findByEmail(String email);

  //  @Override
  //  @Cacheable(key = "#id")
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
