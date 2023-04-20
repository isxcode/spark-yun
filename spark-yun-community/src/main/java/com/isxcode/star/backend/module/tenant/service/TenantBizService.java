package com.isxcode.star.backend.module.tenant.service;

import com.isxcode.star.api.constants.Roles;
import com.isxcode.star.api.constants.UserStatus;
import com.isxcode.star.api.exception.SparkYunException;
import com.isxcode.star.api.pojos.tenant.req.TetAddTenantReq;
import com.isxcode.star.api.pojos.tenant.req.TetAddUserReq;
import com.isxcode.star.api.pojos.tenant.req.TetUpdateTenantReq;
import com.isxcode.star.api.pojos.tenant.req.TetUpdateUserReq;
import com.isxcode.star.backend.module.datasource.mapper.DatasourceMapper;
import com.isxcode.star.backend.module.datasource.repository.DatasourceRepository;
import com.isxcode.star.backend.security.module.user.entity.UserEntity;
import com.isxcode.star.backend.security.module.user.mapper.UserMapper;
import com.isxcode.star.backend.security.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Optional;

import static java.sql.DriverManager.getConnection;

/** 数据源模块service. */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TenantBizService {

  private final UserRepository userRepository;

  private final UserMapper userMapper;

  /**
   * 创建用户.
   */
  public void addUser(TetAddUserReq tetAddUserReq) {

    // 判断账号是否存在
    Optional<UserEntity> userEntityOptional = userRepository.findByAccount(tetAddUserReq.getAccount());
    if (userEntityOptional.isPresent()) {
      throw new SparkYunException("用户已存在");
    }

    // 判断手机号是否存在
    if (!Strings.isEmpty(tetAddUserReq.getPhone())) {
      Optional<UserEntity> byPhoneOptional = userRepository.findByPhone(tetAddUserReq.getPhone());
      if (byPhoneOptional.isPresent()) {
        throw new SparkYunException("手机号已存在");
      }
    }

    // 判断邮箱是否存在
    if (!Strings.isEmpty(tetAddUserReq.getEmail())) {
      Optional<UserEntity> byPhoneOptional = userRepository.findByEmail(tetAddUserReq.getEmail());
      if (byPhoneOptional.isPresent()) {
        throw new SparkYunException("邮箱已存在");
      }
    }

    // TetAddUserReq To UserEntity
    UserEntity userEntity = userMapper.tetAddUserReqToUserEntity(tetAddUserReq);

    // 默认启动
    userEntity.setStatus(UserStatus.ENABLE);
    userEntity.setUserRole(Roles.NORMAL_MEMBER);

    // 数据持久化
    userRepository.save(userEntity);
  }

  public void updateUser(TetUpdateUserReq tetUpdateUserReq) {

    // 判断用户是否存在
    Optional<UserEntity> userEntityOptional = userRepository.findById(tetUpdateUserReq.getId());
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    // TetUpdateUserReq To UserEntity
    UserEntity userEntity = userMapper.tetUpdateUserReqToUserEntity(tetUpdateUserReq, userEntityOptional.get());

    userRepository.save(userEntity);
  }

  public void disableUser(String userId) {

  }

  public void deleteUser(String userId) {

    userRepository.deleteById(userId);
  }


  public void enableUser(String userId) {

  }

  public void addTenant(TetAddTenantReq tetAddTenantReq) {

  }

  public void updateTenant(TetUpdateTenantReq tetUpdateTenantReq) {

  }

  public void enableTenant(String tenantId) {

  }

  public void disableTenant(String tenantId) {

  }

  public void checkTenant(String tenantId) {

  }

}
