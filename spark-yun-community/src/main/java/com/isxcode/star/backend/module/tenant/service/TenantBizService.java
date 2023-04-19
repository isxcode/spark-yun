package com.isxcode.star.backend.module.tenant.service;

import com.isxcode.star.api.pojos.tenant.req.TetAddTenantReq;
import com.isxcode.star.api.pojos.tenant.req.TetAddUserReq;
import com.isxcode.star.api.pojos.tenant.req.TetUpdateTenantReq;
import com.isxcode.star.api.pojos.tenant.req.TetUpdateUserReq;
import com.isxcode.star.backend.module.datasource.mapper.DatasourceMapper;
import com.isxcode.star.backend.module.datasource.repository.DatasourceRepository;
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

  private final DatasourceRepository datasourceRepository;

  private final DatasourceMapper datasourceMapper;

  public void addUser(TetAddUserReq usrAddUserReq) {

    // 判断手机号 邮箱 账号是否已经存在


    // 将req转entity


    // 数据持久化
  }

  public void updateUser(TetUpdateUserReq tetUpdateUserReq) {


  }

  public void disableUser(String userId) {

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
