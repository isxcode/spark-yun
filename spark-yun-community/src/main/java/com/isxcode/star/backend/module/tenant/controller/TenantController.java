package com.isxcode.star.backend.module.tenant.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.constants.Roles;
import com.isxcode.star.api.pojos.tenant.req.TetAddTenantReq;
import com.isxcode.star.api.pojos.tenant.req.TetQueryTenantReq;
import com.isxcode.star.api.pojos.tenant.req.TetUpdateTenantBySystemAdminReq;
import com.isxcode.star.api.pojos.tenant.req.TetUpdateTenantByTenantAdminReq;
import com.isxcode.star.api.pojos.tenant.res.TetQueryTenantRes;
import com.isxcode.star.api.pojos.tenant.res.TetQueryUserTenantRes;
import com.isxcode.star.api.response.SuccessResponse;
import com.isxcode.star.backend.module.tenant.service.TenantBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "租户模块")
@RestController
@RequestMapping(ModulePrefix.TENANT)
@RequiredArgsConstructor
public class TenantController {

  private final TenantBizService tenantBizService;

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "创建租户接口")
  @PostMapping("/addTenant")
  @SuccessResponse("创建成功")
  public void addTenant(@Valid @RequestBody TetAddTenantReq tetAddTenantReq) {

    tenantBizService.addTenant(tetAddTenantReq);
  }

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "查询租户列表接口")
  @PostMapping("/queryTenant")
  @SuccessResponse("查询成功")
  public Page<TetQueryTenantRes> queryTenant(@Valid @RequestBody TetQueryTenantReq tetQueryTenantReq) {

    return tenantBizService.queryTenants(tetQueryTenantReq);
  }

  @Operation(summary = "查询用户租户列表接口")
  @PostMapping("/queryUserTenant")
  @SuccessResponse("查询成功")
  public List<TetQueryUserTenantRes> queryUserTenant() {
    return tenantBizService.queryUserTenant();
  }

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "系统管理员更新租户接口")
  @PostMapping("/updateTenantBySystemAdmin")
  @SuccessResponse("更新成功")
  public void updateTenantBySystemAdmin(@Valid @RequestBody TetUpdateTenantBySystemAdminReq tetUpdateTenantBySystemAdminReq) {

    tenantBizService.updateTenantByTenantAdmin(tetUpdateTenantBySystemAdminReq);
  }

  @Secured({Roles.TENANT_ADMIN})
  @Operation(summary = "租户管理员更新租户接口")
  @PostMapping("/updateTenantByTenantAdmin")
  @SuccessResponse("更新成功")
  public void updateTenantByTenantAdmin(@Valid @RequestBody TetUpdateTenantByTenantAdminReq tetUpdateTenantByTenantAdminReq) {

    tenantBizService.TetUpdateTenantByTenantAdminReq(tetUpdateTenantByTenantAdminReq);
  }

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "启动租户接口")
  @GetMapping("/enableTenant")
  @SuccessResponse("启用成功")
  public void enableTenant(@Schema(description = "租户唯一id", example = "sy_344c3d583fa344f7a2403b19c5a654dc") @RequestParam String tenantId) {

    tenantBizService.enableTenant(tenantId);
  }

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "禁用租户接口")
  @GetMapping("/disableTenant")
  @SuccessResponse("禁用成功")
  public void disableTenant(@Schema(description = "租户唯一id", example = "sy_344c3d583fa344f7a2403b19c5a654dc") @RequestParam String tenantId) {

    tenantBizService.disableTenant(tenantId);
  }

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "检测租户信息接口")
  @GetMapping("/checkTenant")
  @SuccessResponse("检测完成")
  public void checkTenant(@Schema(description = "租户唯一id", example = "sy_344c3d583fa344f7a2403b19c5a654dc") @RequestParam String tenantId) {

    tenantBizService.checkTenant(tenantId);
  }

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "删除租户接口")
  @GetMapping("/deleteTenant")
  @SuccessResponse("删除成功")
  public void deleteTenant(@Schema(description = "租户唯一id", example = "sy_344c3d583fa344f7a2403b19c5a654dc") @RequestParam String tenantId) {

    tenantBizService.deleteTenant(tenantId);
  }
}

