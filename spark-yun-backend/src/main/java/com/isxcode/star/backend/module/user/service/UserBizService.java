package com.isxcode.star.backend.module.user.service;

import com.isxcode.star.api.pojos.user.req.UsrLoginReq;
import com.isxcode.star.api.pojos.user.res.UsrLoginRes;
import com.isxcode.star.backend.module.user.entity.UserEntity;
import com.isxcode.star.backend.module.user.repository.UserRepository;
import com.isxcode.star.common.exception.SparkYunException;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 用户模块. */
@Service
@RequiredArgsConstructor
@Transactional
public class UserBizService {

  private final UserRepository userRepository;

  /** 用户登录. */
  public UsrLoginRes login(UsrLoginReq usrLoginReq) {

    Optional<UserEntity> userEntityOptional =
        userRepository.findByAccount(usrLoginReq.getAccount());
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }
    UserEntity userEntity = userEntityOptional.get();

    // 密码匹配
    if (usrLoginReq.getPasswd().equals(userEntity.getPasswd())) {
      return new UsrLoginRes(userEntity.getUsername());
    } else {
      throw new SparkYunException("账号或者密码不正确");
    }
  }
}
