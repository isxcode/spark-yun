package com.isxcode.star.backend.module.tenant.service;

import com.isxcode.star.api.constants.Roles;
import com.isxcode.star.api.constants.UserStatus;
import com.isxcode.star.api.exception.SparkYunException;
import com.isxcode.star.api.pojos.tenant.req.TetAddTenantReq;
import com.isxcode.star.api.pojos.tenant.req.TetUpdateTenantReq;
import com.isxcode.star.backend.module.tenant.entity.TenantEntity;
import com.isxcode.star.backend.module.tenant.mapper.TenantMapper;
import com.isxcode.star.backend.module.tenant.repository.TenantRepository;
import com.isxcode.star.backend.module.tenant.user.entity.TenantUserEntity;
import com.isxcode.star.backend.module.tenant.user.repository.TenantUserRepository;
import com.isxcode.star.backend.security.module.user.entity.UserEntity;
import com.isxcode.star.backend.security.module.user.mapper.UserMapper;
import com.isxcode.star.backend.security.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private final TenantRepository tenantRepository;

  private final UserRepository userRepository;

  private final TenantUserRepository tenantUserRepository;

  private final TenantMapper tenantMapper;

  public void addTenant(TetAddTenantReq tetAddTenantReq) {

    // 判断名称是否存在
    Optional<TenantEntity> tenantEntityOptional = tenantRepository.findByName(tetAddTenantReq.getName());
    if (tenantEntityOptional.isPresent()) {
      throw new SparkYunException("租户名称重复");
    }

    // 判断管理员是否存在
    Optional<UserEntity> userEntityOptional = userRepository.findById(tetAddTenantReq.getAdminUserId());
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    // 持久化租户
    TenantEntity tenantEntity = tenantRepository.save(tenantMapper.tetAddTenantReqToTenantEntity(tetAddTenantReq));

    // 初始化租户管理员
    TenantUserEntity tenantUserEntity = TenantUserEntity.builder()
      .userId(tetAddTenantReq.getAdminUserId())
      .tenantId(tenantEntity.getId())
      .roleCode(Roles.TENANT_ADMIN)
      .status(UserStatus.ENABLE)
      .build();

    // 持久化租户管理员关系
    tenantUserRepository.save(tenantUserEntity);
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
