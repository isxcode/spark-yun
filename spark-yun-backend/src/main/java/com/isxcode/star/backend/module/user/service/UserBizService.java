package com.isxcode.star.backend.module.user.service;

import com.isxcode.star.api.pojos.user.req.AddUserReq;
import com.isxcode.star.api.pojos.user.req.GetUserReq;
import com.isxcode.star.backend.module.user.entity.UserEntity;
import com.isxcode.star.backend.module.user.mapper.UserMapper;
import com.isxcode.star.backend.module.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
public class UserBizService {

  private final UserService userService;

  private final UserRepository userRepository;

  private final UserMapper userMapper;

  /** 添加用户. */
  public void addUser(AddUserReq addUserReq) {

    // 如果用户存在，则排除异常
    if (userService.userExist(addUserReq.getAccount())) {
      throw new RuntimeException("用户已存在");
    }

    // req 转 entity
    UserEntity user = userMapper.addUserReqToUserEntity(addUserReq);

    // 数据持久化
    userService.addUser(user);
  }

  public List<UserEntity> queryUser() {

    return userRepository.findAll();
  }

  /** 获取用户信息. */
  public UserEntity getUser(GetUserReq getUserReq) {

    Optional<UserEntity> optionalUser = userRepository.findById(getUserReq.getId());
    if (optionalUser.isPresent()) {
      return optionalUser.get();
    }
    throw new RuntimeException("用户不存在");
  }
}
