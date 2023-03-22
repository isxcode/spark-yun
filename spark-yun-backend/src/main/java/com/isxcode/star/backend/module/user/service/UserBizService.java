package com.isxcode.star.backend.module.user.service;

import com.isxcode.star.backend.module.user.entity.UserEntity;
import com.isxcode.star.api.pojos.user.req.AddUserReq;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/*
 * 对应接口的业务逻辑
 *
 * @ispong
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserBizService {

    private final UserService userService;

    public void addUser(AddUserReq addUserReq) {

        // 如果用户存在，则排除异常
        if (userService.userExist(addUserReq.getAccount())) {
            throw new RuntimeException("用户已存在");
        }

        // 数据持久化
//        userService.addUser(user);
    }

}
