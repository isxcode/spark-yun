package com.isxcode.spark.common.jpa;

import com.isxcode.spark.common.security.ContextHolder;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class JpaAuditorConfig implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        return Optional.ofNullable(ContextHolder.getUserId());
    }
}
