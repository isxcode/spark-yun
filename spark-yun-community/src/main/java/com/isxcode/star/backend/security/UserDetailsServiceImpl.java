package com.isxcode.star.backend.security;

import com.isxcode.star.api.constants.Roles;
import com.isxcode.star.api.exception.SparkYunException;
import com.isxcode.star.backend.module.tenant.user.entity.TenantUserEntity;
import com.isxcode.star.backend.module.tenant.user.repository.TenantUserRepository;
import com.isxcode.star.backend.module.user.entity.UserEntity;
import com.isxcode.star.backend.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.isxcode.star.backend.config.WebSecurityConfig.JPA_TENANT_MODE;
import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;

@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  private final TenantUserRepository tenantUserRepository;

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

    // 获取用户信息
    JPA_TENANT_MODE.set(false);
    Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
    JPA_TENANT_MODE.set(true);

    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    // 获取用户系统权限
    String authority = userEntityOptional.get().getRoleCode();

    // 如果不是系统管理员，且没有TENANT_ID则直接报错，缺少tenantId
    if (!Roles.SYS_ADMIN.equals(authority) && Strings.isEmpty(TENANT_ID.get())) {
      throw new SparkYunException("缺少tenantId");
    }

    // 获取用户租户权限
    if (!Roles.SYS_ADMIN.equals(authority)) {
      if (!Strings.isEmpty(TENANT_ID.get()) && !"undefined".equals(TENANT_ID.get())) {
        Optional<TenantUserEntity> tenantUserEntityOptional = tenantUserRepository.findByTenantIdAndUserId(TENANT_ID.get(), userId);
        if (!tenantUserEntityOptional.isPresent()) {
          throw new SparkYunException("用户不在租户中");
        }
        authority = authority + "," + tenantUserEntityOptional.get().getRoleCode();
      }
    }

    // 返回用户信息
    return User.withUsername(userId)
      .password(userEntityOptional.get().getPasswd())
      .authorities(AuthorityUtils.commaSeparatedStringToAuthorityList(authority))
      .build();
  }
}
