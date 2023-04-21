package com.isxcode.star.backend.module.tenant.service;

import com.isxcode.star.api.pojos.tenant.req.TetAddTenantReq;
import com.isxcode.star.api.pojos.tenant.req.TetUpdateTenantReq;
import com.isxcode.star.backend.security.module.user.mapper.UserMapper;
import com.isxcode.star.backend.security.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static java.sql.DriverManager.getConnection;

/** 数据源模块service. */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TenantBizService {

  private final UserRepository userRepository;

  private final UserMapper userMapper;

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
