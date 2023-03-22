package com.isxcode.star.backend.module.user.service;

import com.isxcode.star.backend.module.user.entity.UserEntity;
import com.isxcode.star.backend.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
 * 解藕出来的基础逻辑
 *
 * @ispong
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  /*
   * 判断用户是否存在
   *
   * @param account 用户账号
   * @return true为存在 false为不存在
   * @ispong
   */
  public Boolean userExist(String account) {

    return userRepository.findFirstByAccount(account) != null;
  }

  /*
   * 添加用户
   *
   * @ispong
   */
  public void addUser(UserEntity user) {

    userRepository.save(user);
  }
}
