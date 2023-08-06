package com.isxcode.star.security.main;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * 认证的token对象
 *
 * @author isxcode
 * @since 0.0.1
 */
public class AuthenticationToken extends AbstractAuthenticationToken {

  private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

  private final Object principal;

  private Object credentials;

  public AuthenticationToken(Object principal) {

    super(null);
    this.principal = principal;
    //        this.credentials = "";
    setAuthenticated(false);
  }

  public AuthenticationToken(Object principal, Object credentials) {

    super(null);
    this.principal = principal;
    this.credentials = credentials;
    setAuthenticated(false);
  }

  public AuthenticationToken(
      Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {

    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    super.setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {

    return credentials;
  }

  @Override
  public Object getPrincipal() {

    return principal;
  }
}
