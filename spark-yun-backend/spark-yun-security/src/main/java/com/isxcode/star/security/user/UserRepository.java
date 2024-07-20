package com.isxcode.star.security.user;

import java.util.List;
import java.util.Optional;

import com.isxcode.star.api.main.constants.ModuleCode;
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

@Repository
@CacheConfig(cacheNames = {ModuleCode.USER})
public interface UserRepository extends JpaRepository<UserEntity, String> {

    @Query("SELECT U FROM UserEntity U " + "WHERE U.roleCode != 'ROLE_SYS_ADMIN' and " + "(U.username LIKE %:keyword% "
        + "OR U.account LIKE %:keyword% " + "OR U.email LIKE %:keyword% " + "OR U.phone LIKE %:keyword% "
        + "OR U.remark LIKE %:keyword%) order by U.createDateTime desc ")
    Page<UserEntity> searchAllUser(@Param("keyword") String searchKeyWord, Pageable pageable);

    @Query("SELECT U FROM UserEntity U " + "WHERE U.roleCode != 'ROLE_SYS_ADMIN' " + "AND U.status = 'ENABLE' "
        + "AND (U.username LIKE %:keyword%" + " OR U.account LIKE %:keyword% " + "OR U.email LIKE %:keyword% "
        + "OR U.phone LIKE %:keyword% " + "OR U.remark LIKE %:keyword%) order by U.createDateTime desc ")
    Page<UserEntity> searchAllEnableUser(@Param("keyword") String searchKeyWord, Pageable pageable);

    Optional<UserEntity> findByAccount(String account);

    Optional<UserEntity> findByPhone(String phone);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findById(String id);

    @Override
    @Cacheable(key = "'users'")
    List<UserEntity> findAll();

    @Caching(evict = {@CacheEvict(key = "'users'")}, put = {@CachePut(key = "#userEntity.id")})
    @Override
    UserEntity save(UserEntity userEntity);
}
