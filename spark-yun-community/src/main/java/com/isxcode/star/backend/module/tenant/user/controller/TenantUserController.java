package com.isxcode.star.backend.module.tenant.user.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.tenant.user.req.TurAddTenantUserReq;
import com.isxcode.star.api.pojos.tenant.user.req.TurRemoveTenantAdminReq;
import com.isxcode.star.api.pojos.tenant.user.req.TurRemoveTenantUserReq;
import com.isxcode.star.api.pojos.tenant.user.req.TurSetTenantAdminReq;
import com.isxcode.star.backend.module.tenant.user.service.TenantUserBizService;
import com.isxcode.star.api.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "租户模块")
@RestController
@RequestMapping(ModulePrefix.TENANT_USER)
@RequiredArgsConstructor
public class TenantUserController {

  private final TenantUserBizService tenantUserBizService;

  @Operation(summary = "添加用户接口")
  @PostMapping("/addTenantUser")
  @SuccessResponse("添加成功")
  public void addTenantUser(@Valid @RequestBody TurAddTenantUserReq turAddTenantUserReq) {

    tenantUserBizService.addTenantUser(turAddTenantUserReq);
  }

  @Operation(summary = "移除用户接口")
  @PostMapping("/removeTenantUser")
  @SuccessResponse("移除成功")
  public void removeTenantUser(@Valid @RequestBody TurRemoveTenantUserReq turRemoveTenantUserReq) {

    tenantUserBizService.removeTenantUser(turRemoveTenantUserReq);
  }

  @Operation(summary = "设置为管理员接口")
  @PostMapping("/setTenantAdmin")
  @SuccessResponse("设置成功")
  public void setTenantAdmin(@Valid @RequestBody TurSetTenantAdminReq turSetTenantAdminReq) {

    tenantUserBizService.setTenantAdmin(turSetTenantAdminReq);
  }

  @Operation(summary = "取消设置管理员接口")
  @PostMapping("/removeTenantAdmin")
  @SuccessResponse("取消成功")
  public void removeTenantAdmin(@Valid @RequestBody TurRemoveTenantAdminReq turRemoveTenantAdminReq) {

    tenantUserBizService.removeTenantAdmin(turRemoveTenantAdminReq);
  }
}

