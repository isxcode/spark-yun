package com.isxcode.star.backend.module.user.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.user.req.UsrLoginReq;
import com.isxcode.star.api.pojos.user.res.UsrLoginRes;
import com.isxcode.star.backend.module.user.service.UserBizService;
import com.isxcode.star.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
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

  @Operation(description = "用户登录接口")
  @PostMapping("/login")
  @SuccessResponse("登录成功")
  public UsrLoginRes login(@ParameterObject @Valid @RequestBody UsrLoginReq usrLoginReq) {

    return userBizService.login(usrLoginReq);
  }
}
