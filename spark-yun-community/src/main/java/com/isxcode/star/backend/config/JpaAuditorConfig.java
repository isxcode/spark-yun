package com.isxcode.star.backend.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaAuditorConfig implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {

    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
  }
}
