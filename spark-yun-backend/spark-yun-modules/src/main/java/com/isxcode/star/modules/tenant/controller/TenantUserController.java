package com.isxcode.star.modules.tenant.controller;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.tenant.req.*;
import com.isxcode.star.api.tenant.res.PageTenantUserRes;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.tenant.service.biz.TenantUserBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "租户用户模块")
@RequestMapping(ModuleCode.TENANT_USER)
@RestController
@RequiredArgsConstructor
public class TenantUserController {

    private final TenantUserBizService tenantUserBizService;

    @Secured({RoleType.SYS_ADMIN, RoleType.TENANT_ADMIN})
    @Operation(summary = "添加用户接口")
    @PostMapping("/addTenantUser")
    @SuccessResponse("添加成功")
    public void addTenantUser(@Valid @RequestBody AddTenantUserReq addTenantUserReq) {

        tenantUserBizService.addTenantUser(addTenantUserReq);
    }

    @Secured({RoleType.SYS_ADMIN, RoleType.TENANT_ADMIN, RoleType.NORMAL_MEMBER})
    @Operation(summary = "查询租户用户列表接口")
    @PostMapping("/pageTenantUser")
    @SuccessResponse("查询成功")
    public Page<PageTenantUserRes> pageTenantUser(@Valid @RequestBody PageTenantUserReq pageTenantUserReq) {

        return tenantUserBizService.pageTenantUser(pageTenantUserReq);
    }

    @Secured({RoleType.SYS_ADMIN, RoleType.TENANT_ADMIN})
    @Operation(summary = "移除用户接口")
    @PostMapping("/removeTenantUser")
    @SuccessResponse("移除成功")
    public void removeTenantUser(@Valid @RequestBody RemoveTenantUserReq removeTenantUserReq) {

        tenantUserBizService.removeTenantUser(removeTenantUserReq);
    }

    @Secured({RoleType.SYS_ADMIN, RoleType.TENANT_ADMIN})
    @Operation(summary = "设置为租户管理员接口")
    @PostMapping("/setTenantAdmin")
    @SuccessResponse("设置成功")
    public void setTenantAdmin(@Valid @RequestBody SetTenantAdminReq setTenantAdminReq) {

        tenantUserBizService.setTenantAdmin(setTenantAdminReq);
    }

    @Secured({RoleType.SYS_ADMIN, RoleType.TENANT_ADMIN})
    @Operation(summary = "取消设置为租户管理员接口")
    @PostMapping("/removeTenantAdmin")
    @SuccessResponse("设置成功")
    public void removeTenantAdmin(@Valid @RequestBody RemoveTenantAdminReq removeTenantAdminReq) {

        tenantUserBizService.removeTenantAdmin(removeTenantAdminReq);
    }
}
