package com.isxcode.star.backend.module.tenant.user.service;

import com.isxcode.star.api.pojos.tenant.user.req.TurAddTenantUserReq;
import com.isxcode.star.api.pojos.tenant.user.req.TurRemoveTenantAdminReq;
import com.isxcode.star.api.pojos.tenant.user.req.TurRemoveTenantUserReq;
import com.isxcode.star.api.pojos.tenant.user.req.TurSetTenantAdminReq;
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
public class TenantUserBizService {


  public void addTenantUser(TurAddTenantUserReq turAddTenantUserReq) {

  }

  public void removeTenantUser(TurRemoveTenantUserReq turRemoveTenantUserReq) {

  }

  public void setTenantAdmin(TurSetTenantAdminReq turSetTenantAdminReq) {

  }

  public void removeTenantAdmin(TurRemoveTenantAdminReq turRemoveTenantAdminReq) {

  }
}
