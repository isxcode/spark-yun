package com.isxcode.star.common.config;

import com.isxcode.star.common.annotations.successResponse.SuccessResponseAdvice;
import com.isxcode.star.common.exception.GlobalExceptionAdvice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class AdviceAutoConfiguration {

  private final MessageSource messageSource;

  @Bean
  GlobalExceptionAdvice initGlobalExceptionAdvice() {

    return new GlobalExceptionAdvice();
  }

  @Bean
  SuccessResponseAdvice initSuccessResponseAdvice() {

    return new SuccessResponseAdvice(messageSource);
  }
}
