package com.isxcode.star.modules.tenant.controller;

import com.isxcode.star.api.tenant.pojos.req.TurAddTenantUserReq;
import com.isxcode.star.api.tenant.pojos.req.TurQueryTenantUserReq;
import com.isxcode.star.api.tenant.pojos.res.TurQueryTenantUserRes;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.backend.api.base.constants.ModulePrefix;
import com.isxcode.star.backend.api.base.constants.SecurityConstants;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.tenant.service.TenantUserBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "租户用户模块")
@RestController
@RequestMapping(ModulePrefix.TENANT_USER)
@RequiredArgsConstructor
public class TenantUserController {

  private final TenantUserBizService tenantUserBizService;

  @Secured({RoleType.SYS_ADMIN, RoleType.TENANT_ADMIN})
  @Operation(summary = "添加用户接口")
  @PostMapping("/addTenantUser")
  @SuccessResponse("添加成功")
  @Parameter(
      name = "Tenant",
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void addTenantUser(@Valid @RequestBody TurAddTenantUserReq turAddTenantUserReq) {

    tenantUserBizService.addTenantUser(turAddTenantUserReq);
  }

  @Secured({RoleType.SYS_ADMIN, RoleType.TENANT_ADMIN})
  @Operation(summary = "查询租户用户列表接口")
  @PostMapping("/queryTenantUser")
  @SuccessResponse("查询成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public Page<TurQueryTenantUserRes> queryTenantUser(
      @Valid @RequestBody TurQueryTenantUserReq turQueryTenantUserReq) {

    return tenantUserBizService.queryTenantUser(turQueryTenantUserReq);
  }

  @Secured({RoleType.SYS_ADMIN, RoleType.TENANT_ADMIN})
  @Operation(summary = "移除用户接口")
  @GetMapping("/removeTenantUser")
  @SuccessResponse("移除成功")
  @Parameter(
      name = "Tenant",
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void removeTenantUser(
      @Schema(description = "关系唯一id", example = "sy_ff3c1b52f8b34c45ab2cf24b6bccd480") @RequestParam
          String tenantUserId) {

    tenantUserBizService.removeTenantUser(tenantUserId);
  }

  @Secured({RoleType.SYS_ADMIN, RoleType.TENANT_ADMIN})
  @Operation(summary = "设置为租户管理员接口")
  @GetMapping("/setTenantAdmin")
  @SuccessResponse("设置成功")
  @Parameter(
      name = "Tenant",
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void setTenantAdmin(
      @Schema(description = "关系id", example = "sy_ff3c1b52f8b34c45ab2cf24b6bccd480") @RequestParam
          String tenantUserId) {

    tenantUserBizService.setTenantAdmin(tenantUserId);
  }

  @Secured({RoleType.SYS_ADMIN, RoleType.TENANT_ADMIN})
  @Operation(summary = "取消设置为租户管理员接口")
  @GetMapping("/removeTenantAdmin")
  @SuccessResponse("设置成功")
  @Parameter(
      name = "Tenant",
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void removeTenantAdmin(
      @Schema(description = "关系id", example = "sy_ff3c1b52f8b34c45ab2cf24b6bccd480") @RequestParam
          String tenantUserId) {

    tenantUserBizService.removeTenantAdmin(tenantUserId);
  }
}
