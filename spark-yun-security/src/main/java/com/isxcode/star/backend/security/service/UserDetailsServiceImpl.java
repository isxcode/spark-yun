package com.isxcode.star.backend.security.service;

import com.isxcode.star.api.exception.SparkYunException;
import com.isxcode.star.backend.security.module.user.entity.UserEntity;
import com.isxcode.star.backend.security.module.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {

    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

    // 获取用户认证信息
    Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    // 获取用户的权限
    String authority = Strings.join(new ArrayList<>(), ',');

    // 返回用户信息
    return User.withUsername(userId)
      .password(userEntityOptional.get().getPasswd())
      .authorities(AuthorityUtils.commaSeparatedStringToAuthorityList(authority))
      .build();
  }
}
