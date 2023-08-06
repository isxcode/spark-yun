package com.isxcode.star.security.main;

import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * 权限认证管理器,负责管理权限处理器
 *
 * @author isxcode
 * @since 0.0.1
 */
public class AuthenticationManagerImpl implements AuthenticationManager {

  private final List<AuthenticationProvider> authenticationProviders;

  public AuthenticationManagerImpl(List<AuthenticationProvider> authenticationProviders) {

    this.authenticationProviders = authenticationProviders;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    for (AuthenticationProvider provider : authenticationProviders) {

      return provider.authenticate(authentication);
    }

    return authentication;
  }
}
