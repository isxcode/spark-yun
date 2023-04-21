package com.isxcode.star.backend.config;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class JpaTenantInterceptor implements StatementInspector {

  @Override
  public String inspect(String sql) {

    return sql.replace("${tenantId}", String.valueOf(Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getCredentials())));
  }
}
