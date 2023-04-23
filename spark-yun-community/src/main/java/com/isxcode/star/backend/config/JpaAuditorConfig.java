package com.isxcode.star.backend.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

@Component
public class JpaAuditorConfig implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {

    return Optional.ofNullable(USER_ID.get());
  }
}
