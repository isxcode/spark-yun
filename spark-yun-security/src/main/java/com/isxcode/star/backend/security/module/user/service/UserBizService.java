package com.isxcode.star.backend.security.module.user.service;

import com.isxcode.star.api.pojos.user.req.UsrLoginReq;
import com.isxcode.star.api.pojos.user.req.UsrUpdateUserReq;
import com.isxcode.star.api.pojos.user.res.UsrLoginRes;
import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.backend.security.module.user.entity.UserEntity;
import com.isxcode.star.backend.security.module.user.repository.UserRepository;
import com.isxcode.star.api.utils.JwtUtils;
import com.isxcode.star.api.exception.SparkYunException;
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

  private final SparkYunProperties sparkYunProperties;

  /**
   * 用户登录.
   */
  public UsrLoginRes login(UsrLoginReq usrLoginReq) {

    Optional<UserEntity> userEntityOptional = userRepository.findByAccount(usrLoginReq.getAccount());
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }
    UserEntity userEntity = userEntityOptional.get();

    // 密码匹配
    if (usrLoginReq.getPasswd().equals(userEntity.getPasswd())) {

      // 登录成功
      String jwtToken = JwtUtils.encrypt(sparkYunProperties.getAesSlat(), userEntity.getId(), sparkYunProperties.getJwtKey(), sparkYunProperties.getExpirationMin());

      return new UsrLoginRes(userEntity.getUsername(), jwtToken);
    } else {
      throw new SparkYunException("账号或者密码不正确");
    }
  }

  public void updateUser(UsrUpdateUserReq usrUpdateUserReq) {


  }

  public void logout() {

    System.out.println("用户退出登录");
  }


}
