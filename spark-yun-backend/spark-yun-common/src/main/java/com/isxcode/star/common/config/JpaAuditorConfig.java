package com.isxcode.star.common.config;

import static com.isxcode.star.common.config.CommonConfig.USER_ID;

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
