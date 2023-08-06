package com.isxcode.star.modules.user.controller;

import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.api.user.pojos.req.UsrAddUserReq;
import com.isxcode.star.api.user.pojos.req.UsrLoginReq;
import com.isxcode.star.api.user.pojos.req.UsrQueryAllEnableUsersReq;
import com.isxcode.star.api.user.pojos.req.UsrQueryAllUsersReq;
import com.isxcode.star.api.user.pojos.req.UsrUpdateUserReq;
import com.isxcode.star.api.user.pojos.res.UsrLoginRes;
import com.isxcode.star.api.user.pojos.res.UsrQueryAllEnableUsersRes;
import com.isxcode.star.api.user.pojos.res.UsrQueryAllUsersRes;
import com.isxcode.star.backend.api.base.constants.ModulePrefix;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.user.service.UserBizService;
import com.isxcode.star.modules.userlog.UserLog;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "用户模块")
@RestController
@RequestMapping(ModulePrefix.USER)
@RequiredArgsConstructor
public class UserController {

  private final UserBizService userBizService;

  @UserLog
  @Operation(summary = "用户登录接口")
  @PostMapping("/open/login")
  @SuccessResponse("登录成功")
  public UsrLoginRes login(@Valid @RequestBody UsrLoginReq usrLoginReq) {

    return userBizService.login(usrLoginReq);
  }

  @UserLog
  @Operation(summary = "用户退出接口")
  @GetMapping("/logout")
  @SuccessResponse("退出成功")
  public void logout() {

    userBizService.logout();
  }

  @UserLog
  @Secured({RoleType.SYS_ADMIN})
  @Operation(summary = "获取用户信息接口")
  @GetMapping("/getUser")
  @SuccessResponse("获取成功")
  public UsrLoginRes getUser() {

    return userBizService.getUser();
  }

  @UserLog
  @Secured({RoleType.SYS_ADMIN})
  @Operation(summary = "创建用户接口")
  @PostMapping("/addUser")
  @SuccessResponse("创建成功")
  public void addUser(@Valid @RequestBody UsrAddUserReq usrAddUserReq) {

    userBizService.addUser(usrAddUserReq);
  }

  @UserLog
  @Secured({RoleType.SYS_ADMIN})
  @Operation(summary = "更新用户接口")
  @PostMapping("/updateUser")
  @SuccessResponse("更新成功")
  public void updateUser(@Valid @RequestBody UsrUpdateUserReq usrUpdateUserReq) {

    userBizService.updateUser(usrUpdateUserReq);
  }

  @UserLog
  @Secured({RoleType.SYS_ADMIN})
  @Operation(summary = "禁用用户接口")
  @GetMapping("/disableUser")
  @SuccessResponse("禁用成功")
  public void disableUser(
      @Schema(description = "用户唯一id", example = "sy_b0288cadb2ab4325ae519ff329a95cda") @RequestParam
          String userId) {

    userBizService.disableUser(userId);
  }

  @UserLog
  @Secured({RoleType.SYS_ADMIN})
  @Operation(summary = "启用用户接口")
  @GetMapping("/enableUser")
  @SuccessResponse("启用成功")
  public void enableUser(
      @Schema(description = "用户唯一id", example = "sy_b0288cadb2ab4325ae519ff329a95cda") @RequestParam
          String userId) {

    userBizService.enableUser(userId);
  }

  @UserLog
  @Secured({RoleType.SYS_ADMIN})
  @Operation(summary = "删除用户接口")
  @GetMapping("/deleteUser")
  @SuccessResponse("删除成功")
  public void deleteUser(
      @Schema(description = "用户唯一id", example = "sy_ff3c1b52f8b34c45ab2cf24b6bccd480") @RequestParam
          String userId) {

    userBizService.deleteUser(userId);
  }

  @UserLog
  @Secured({RoleType.SYS_ADMIN})
  @Operation(summary = "查询所有用户接口")
  @PostMapping("/queryAllUsers")
  @SuccessResponse("查询成功")
  public Page<UsrQueryAllUsersRes> queryAllUsers(
      @Valid @RequestBody UsrQueryAllUsersReq usrQueryAllUsersReq) {

    return userBizService.queryAllUsers(usrQueryAllUsersReq);
  }

  @UserLog
  @Secured({RoleType.SYS_ADMIN, RoleType.TENANT_ADMIN})
  @Operation(summary = "查询所有启用用户接口")
  @PostMapping("/queryAllEnableUsers")
  @SuccessResponse("查询成功")
  public Page<UsrQueryAllEnableUsersRes> queryAllEnableUsers(
      @Valid @RequestBody UsrQueryAllEnableUsersReq usrQueryAllEnableUsersReq) {

    return userBizService.queryAllEnableUsers(usrQueryAllEnableUsersReq);
  }
}
