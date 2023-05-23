package com.isxcode.star.backend.config;

import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class JpaAuditorConfig implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {

    return Optional.ofNullable(USER_ID.get());
  }
}
