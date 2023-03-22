package com.isxcode.star.backend.module.user.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.user.req.AddUserReq;
import com.isxcode.star.backend.module.user.service.UserBizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/*
 * 只负责接口入口
 *
 * @ispong
 */
@RestController
@RequestMapping(ModulePrefix.USER)
@RequiredArgsConstructor
public class UserController {

    private final UserBizService userBizService;

    @PostMapping("/addUser")
    public void addUser(@Valid @RequestBody AddUserReq addUserReq) {

        userBizService.addUser(addUserReq);
    }

    @PostMapping("/queryUser")
    public void queryUser() {

    }

    @PostMapping("/delUser")
    public void delUser() {

    }

    @PostMapping("/updateUser")
    public void updateUser() {

    }

    @PostMapping("/getUser")
    public void getUser() {

    }

    @PostMapping("/updateUserStatus")
    public void updateUserStatus() {

    }

    @PostMapping("/login")
    public void login() {

    }

}
