package com.isxcode.star.modules.tenant.controller;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.tenant.req.*;
import com.isxcode.star.api.tenant.res.GetTenantRes;
import com.isxcode.star.api.tenant.res.PageTenantRes;
import com.isxcode.star.api.tenant.res.QueryUserTenantRes;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.tenant.service.biz.TenantBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "租户模块")
@RequestMapping(ModuleCode.TENANT)
@RestController
@RequiredArgsConstructor
public class TenantController {

    private final TenantBizService tenantBizService;

    @Secured({RoleType.SYS_ADMIN})
    @Operation(summary = "创建租户接口")
    @PostMapping("/addTenant")
    @SuccessResponse("创建成功")
    public void addTenant(@Valid @RequestBody AddTenantReq addTenantReq) {

        tenantBizService.addTenant(addTenantReq);
    }

    @Operation(summary = "查询租户列表接口")
    @PostMapping("/pageTenant")
    @SuccessResponse("查询成功")
    public Page<PageTenantRes> pageTenant(@Valid @RequestBody PageTenantReq pageTenantReq) {

        return tenantBizService.pageTenant(pageTenantReq);
    }

    @Operation(summary = "查询用户租户列表接口")
    @PostMapping("/queryUserTenant")
    @SuccessResponse("查询成功")
    public List<QueryUserTenantRes> queryUserTenant() {

        return tenantBizService.queryUserTenant();
    }

    @Secured({RoleType.SYS_ADMIN})
    @Operation(summary = "系统管理员更新租户接口")
    @PostMapping("/updateTenantForSystemAdmin")
    @SuccessResponse("更新成功")
    public void updateTenantForSystemAdmin(
        @Valid @RequestBody UpdateTenantForSystemAdminReq updateTenantForSystemAdminReq) {

        tenantBizService.updateTenantForSystemAdmin(updateTenantForSystemAdminReq);
    }

    @Secured({RoleType.TENANT_ADMIN})
    @Operation(summary = "租户管理员更新租户接口")
    @PostMapping("/updateTenantForTenantAdmin")
    @SuccessResponse("更新成功")
    public void updateTenantForTenantAdmin(
        @Valid @RequestBody UpdateTenantForTenantAdminReq updateTenantForTenantAdminReq) {

        tenantBizService.updateTenantForTenantAdmin(updateTenantForTenantAdminReq);
    }

    @Secured({RoleType.SYS_ADMIN})
    @Operation(summary = "启动租户接口")
    @PostMapping("/enableTenant")
    @SuccessResponse("启用成功")
    public void enableTenant(@Valid @RequestBody EnableTenantReq enableTenantReq) {

        tenantBizService.enableTenant(enableTenantReq);
    }

    @Secured({RoleType.SYS_ADMIN})
    @Operation(summary = "禁用租户接口")
    @PostMapping("/disableTenant")
    @SuccessResponse("禁用成功")
    public void disableTenant(@Valid @RequestBody DisableTenantReq disableTenantReq) {

        tenantBizService.disableTenant(disableTenantReq);
    }

    @Secured({RoleType.SYS_ADMIN})
    @Operation(summary = "检测租户信息接口")
    @PostMapping("/checkTenant")
    @SuccessResponse("检测完成")
    public void checkTenant(@Valid @RequestBody CheckTenantReq checkTenantReq) {

        tenantBizService.checkTenant(checkTenantReq);
    }

    @Secured({RoleType.SYS_ADMIN})
    @Operation(summary = "删除租户接口")
    @PostMapping("/deleteTenant")
    @SuccessResponse("删除成功")
    public void deleteTenant(@Valid @RequestBody DeleteTenantReq deleteTenantReq) {

        tenantBizService.deleteTenant(deleteTenantReq);
    }

    @Operation(summary = "选择租户接口")
    @PostMapping("/chooseTenant")
    @SuccessResponse("切换成功")
    public void chooseTenant(@Valid @RequestBody ChooseTenantReq chooseTenantReq) {

        tenantBizService.chooseTenant(chooseTenantReq);
    }

    @Operation(summary = "获取租户信息接口")
    @PostMapping("/getTenant")
    @SuccessResponse("获取成功")
    public GetTenantRes getTenant(@Valid @RequestBody GetTenantReq getTenantReq) {

        return tenantBizService.getTenant(getTenantReq);
    }
}
