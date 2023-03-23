package com.isxcode.star.backend.module.user.service;

import com.isxcode.star.api.pojos.user.req.AddUserReq;
import com.isxcode.star.backend.module.user.entity.UserEntity;
import com.isxcode.star.backend.module.user.mapper.UserMapper;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
public class UserBizService {

  private final UserService userService;

  private final UserMapper userMapper;

  /** 添加用户. */
  public void addUser(AddUserReq addUserReq) {

    // 如果用户存在，则排除异常
    if (userService.userExist(addUserReq.getAccount())) {
      throw new RuntimeException("用户已存在");
    }

    // req 转 entity
    UserEntity user = userMapper.addUserReqToUserEntity(addUserReq);
    user.setId(UUID.randomUUID().toString());

    // 数据持久化
    userService.addUser(user);
  }
}
