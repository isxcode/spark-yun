package com.isxcode.star.modules.user.controller;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.api.user.pojos.req.*;
import com.isxcode.star.api.user.pojos.res.GetUserRes;
import com.isxcode.star.api.user.pojos.res.LoginRes;
import com.isxcode.star.api.user.pojos.res.PageEnableUserRes;
import com.isxcode.star.api.user.pojos.res.PageUserRes;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.user.service.UserBizService;
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

@Tag(name = "用户模块")
@RestController
@RequestMapping(ModuleCode.USER)
@RequiredArgsConstructor
public class UserController {

	private final UserBizService userBizService;

	@Operation(summary = "用户登录接口")
	@PostMapping("/open/login")
	@SuccessResponse("登录成功")
	public LoginRes login(@Valid @RequestBody LoginReq loginReq) {

		return userBizService.login(loginReq);
	}

	@Operation(summary = "用户退出接口")
	@PostMapping("/logout")
	@SuccessResponse("退出成功")
	public void logout() {

		userBizService.logout();
	}

	@Secured({RoleType.SYS_ADMIN})
	@Operation(summary = "获取用户信息接口")
	@PostMapping("/getUser")
	@SuccessResponse("获取成功")
	public GetUserRes getUser() {

		return userBizService.getUser();
	}

	@Secured({RoleType.SYS_ADMIN})
	@Operation(summary = "创建用户接口")
	@PostMapping("/addUser")
	@SuccessResponse("创建成功")
	public void addUser(@Valid @RequestBody AddUserReq addUserReq) {

		userBizService.addUser(addUserReq);
	}

	@Secured({RoleType.SYS_ADMIN})
	@Operation(summary = "更新用户接口")
	@PostMapping("/updateUser")
	@SuccessResponse("更新成功")
	public void updateUser(@Valid @RequestBody UpdateUserReq updateUserReq) {

		userBizService.updateUser(updateUserReq);
	}

  @Secured({RoleType.SYS_ADMIN })
  @Operation(summary = "更新用户信息")
  @PostMapping("/updateUserInfo")
  @SuccessResponse("信息更新成功")
  public void updateUserInfo(@Valid @RequestBody UpdateUserInfoReq updateUserInfoReq) {
    userBizService.updateUserInfo(updateUserInfoReq);
  }

	@Secured({RoleType.SYS_ADMIN})
	@Operation(summary = "禁用用户接口")
	@PostMapping("/disableUser")
	@SuccessResponse("禁用成功")
	public void disableUser(@Valid @RequestBody DisableUserReq disableUserReq) {

		userBizService.disableUser(disableUserReq);
	}

	@Secured({RoleType.SYS_ADMIN})
	@Operation(summary = "启用用户接口")
	@PostMapping("/enableUser")
	@SuccessResponse("启用成功")
	public void enableUser(@Valid @RequestBody EnableUserReq enableUserReq) {

		userBizService.enableUser(enableUserReq);
	}

	@Secured({RoleType.SYS_ADMIN})
	@Operation(summary = "删除用户接口")
	@PostMapping("/deleteUser")
	@SuccessResponse("删除成功")
	public void deleteUser(@Valid @RequestBody DeleteUserReq deleteUserReq) {

		userBizService.deleteUser(deleteUserReq);
	}

	@Secured({RoleType.SYS_ADMIN})
	@Operation(summary = "查询所有用户接口")
	@PostMapping("/pageUser")
	@SuccessResponse("查询成功")
	public Page<PageUserRes> pageUser(@Valid @RequestBody PageUserReq pageUserReq) {

		return userBizService.pageUser(pageUserReq);
	}

	@Secured({RoleType.SYS_ADMIN, RoleType.TENANT_ADMIN})
	@Operation(summary = "查询所有启用用户接口")
	@PostMapping("/pageEnableUser")
	@SuccessResponse("查询成功")
	public Page<PageEnableUserRes> pageEnableUser(@Valid @RequestBody PageEnableUserReq pageEnableUserReq) {

		return userBizService.pageEnableUser(pageEnableUserReq);
	}
}
