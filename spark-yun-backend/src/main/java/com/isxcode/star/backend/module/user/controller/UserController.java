package com.isxcode.star.backend.module.user.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.user.req.AddUserReq;
import com.isxcode.star.api.pojos.user.req.GetUserReq;
import com.isxcode.star.api.pojos.user.req.LoginReq;
import com.isxcode.star.api.pojos.user.res.LoginRes;
import com.isxcode.star.backend.module.user.entity.UserEntity;
import com.isxcode.star.backend.module.user.service.UserBizService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 只负责用户接口入口. */
@RestController
@RequestMapping(ModulePrefix.USER)
@RequiredArgsConstructor
public class UserController {

  private final UserBizService userBizService;

  @PostMapping("/addUser")
  public void addUser(@Valid @RequestBody AddUserReq addUserReq) {

    userBizService.addUser(addUserReq);
  }

  @GetMapping("/queryUser")
  public List<UserEntity> queryUser() {
    return userBizService.queryUser();
  }

  @PostMapping("/getUser")
  public UserEntity getUser(@Valid @RequestBody GetUserReq getUserReq) {
    return userBizService.getUser(getUserReq);
  }

  @PostMapping("/delUser")
  public void delUser() {}

  @PostMapping("/updateUser")
  public void updateUser() {}

  @PostMapping("/updateUserStatus")
  public void updateUserStatus() {}

  @PostMapping("/login")
  public LoginRes login(@RequestBody LoginReq loginReq) {

    if (loginReq.getAccount().equals("ispong") && loginReq.getPassword().equals("ispong123")) {
      return new LoginRes(true, "登录成功");
    }
    return new LoginRes(false, "登录失败");
  }
}
