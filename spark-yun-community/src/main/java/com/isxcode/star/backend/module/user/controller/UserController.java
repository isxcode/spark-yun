package com.isxcode.star.backend.module.user.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.constants.Roles;
import com.isxcode.star.api.pojos.user.req.UsrAddUserReq;
import com.isxcode.star.api.pojos.user.req.UsrLoginReq;
import com.isxcode.star.api.pojos.user.req.UsrQueryAllEnableUsersReq;
import com.isxcode.star.api.pojos.user.req.UsrQueryAllUsersReq;
import com.isxcode.star.api.pojos.user.req.UsrUpdateUserReq;
import com.isxcode.star.api.pojos.user.res.UsrLoginRes;
import com.isxcode.star.api.pojos.user.res.UsrQueryAllEnableUsersRes;
import com.isxcode.star.api.pojos.user.res.UsrQueryAllUsersRes;
import com.isxcode.star.backend.module.user.service.UserBizService;
import com.isxcode.star.api.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;

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

@Tag(name = "用户模块")
@RestController
@RequestMapping(ModulePrefix.USER)
@RequiredArgsConstructor
public class UserController {

  private final UserBizService userBizService;

  @Operation(summary = "用户登录接口")
  @PostMapping("/open/login")
  @SuccessResponse("登录成功")
  public UsrLoginRes login(@Valid @RequestBody UsrLoginReq usrLoginReq) {

    return userBizService.login(usrLoginReq);
  }

  @Operation(summary = "用户退出接口")
  @GetMapping("/logout")
  @SuccessResponse("退出成功")
  public void logout() {

    userBizService.logout();
  }

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "创建用户接口")
  @PostMapping("/addUser")
  @SuccessResponse("创建成功")
  public void addUser(@Valid @RequestBody UsrAddUserReq usrAddUserReq) {

    userBizService.addUser(usrAddUserReq);
  }

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "更新用户接口")
  @PostMapping("/updateUser")
  @SuccessResponse("更新成功")
  public void updateUser(@Valid @RequestBody UsrUpdateUserReq usrUpdateUserReq) {

    userBizService.updateUser(usrUpdateUserReq);
  }

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "禁用用户接口")
  @GetMapping("/disableUser")
  @SuccessResponse("禁用成功")
  public void disableUser(@Schema(description = "用户唯一id", example = "sy_b0288cadb2ab4325ae519ff329a95cda") @RequestParam String userId) {

    userBizService.disableUser(userId);
  }

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "启用用户接口")
  @GetMapping("/enableUser")
  @SuccessResponse("启用成功")
  public void enableUser(@Schema(description = "用户唯一id", example = "sy_b0288cadb2ab4325ae519ff329a95cda") @RequestParam String userId) {

    userBizService.enableUser(userId);
  }

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "删除用户接口")
  @GetMapping("/deleteUser")
  @SuccessResponse("删除成功")
  public void deleteUser(@Schema(description = "用户唯一id", example = "sy_ff3c1b52f8b34c45ab2cf24b6bccd480") @RequestParam String userId) {

    userBizService.deleteUser(userId);
  }

  @Secured({Roles.SYS_ADMIN})
  @Operation(summary = "查询所有用户接口")
  @PostMapping("/queryAllUsers")
  @SuccessResponse("查询成功")
  public Page<UsrQueryAllUsersRes> queryAllUsers(@Valid @RequestBody UsrQueryAllUsersReq usrQueryAllUsersReq) {

    return userBizService.queryAllUsers(usrQueryAllUsersReq);
  }

  @Secured({Roles.SYS_ADMIN, Roles.TENANT_ADMIN})
  @Operation(summary = "查询所有启用用户接口")
  @PostMapping("/queryAllEnableUsers")
  @SuccessResponse("查询成功")
  public Page<UsrQueryAllEnableUsersRes> queryAllEnableUsers(@Valid @RequestBody UsrQueryAllEnableUsersReq usrQueryAllEnableUsersReq) {

    return userBizService.queryAllEnableUsers(usrQueryAllEnableUsersReq);
  }
}
