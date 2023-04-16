package com.isxcode.star.backend.config;

import com.isxcode.star.api.properties.SparkYunProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final SparkYunProperties sparkYunProperties;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(Customizer.withDefaults());
        http.csrf().disable();
        http.headers().cacheControl();
        http.headers().frameOptions().disable();
        http.sessionManagement().disable();

      http.authorizeRequests()
        .antMatchers(sparkYunProperties.getAdminUrl().toArray(new String[0]))
        .hasRole("ADMIN");


      http.authorizeRequests()
        .antMatchers(sparkYunProperties.getAnonymousUrl().toArray(new String[0]))
        .permitAll();

        http.formLogin();

        return http.build();
    }

  /**
   * Note: 启用spring-security后需要使用特别的跨域配置
   *
   * @since 0.0.1
   */
  @Bean
  CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
    configuration.setAllowedMethods(Collections.singletonList("*"));
    configuration.setAllowedHeaders(Collections.singletonList("*"));
    configuration.setExposedHeaders(Collections.singletonList("Content-Disposition"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
