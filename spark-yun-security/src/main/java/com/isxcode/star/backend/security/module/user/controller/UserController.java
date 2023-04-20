package com.isxcode.star.backend.security.module.user.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.user.req.UsrLoginReq;
import com.isxcode.star.api.pojos.user.req.UsrUpdateUserReq;
import com.isxcode.star.api.pojos.user.res.UsrLoginRes;
import com.isxcode.star.backend.security.module.user.service.UserBizService;
import com.isxcode.star.api.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @Operation(summary = "更新信息")
  @PostMapping("/updateUser")
  @SuccessResponse("更新成功")
  public void updateUser(@Valid @RequestBody UsrUpdateUserReq usrUpdateUserReq) {

    userBizService.updateUser(usrUpdateUserReq);

  }

}
