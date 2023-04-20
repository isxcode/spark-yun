package com.isxcode.star.backend.security.config;

import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.backend.security.filter.JwtAuthenticationFilter;
import com.isxcode.star.backend.security.module.user.repository.UserRepository;
import com.isxcode.star.backend.security.service.AuthenticationManagerImpl;
import com.isxcode.star.backend.security.service.AuthenticationProviderImpl;
import com.isxcode.star.backend.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

  private final SparkYunProperties sparkYunProperties;

  private final UserRepository userRepository;

  public UserDetailsService userDetailsServiceBean() {

    return new UserDetailsServiceImpl(userRepository);
  }

  public AuthenticationManager authenticationManagerBean() {

    List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
    authenticationProviders.add(new AuthenticationProviderImpl(userDetailsServiceBean()));
    return new AuthenticationManagerImpl(authenticationProviders);
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.cors(Customizer.withDefaults());
    http.csrf().disable();
    http.headers().cacheControl();
    http.headers().frameOptions().disable();
    http.sessionManagement().disable();

    // 访问h2,swagger，druid界面需要的权限
    http.authorizeRequests()
      .antMatchers(sparkYunProperties.getAdminUrl().toArray(new String[0]))
      .hasRole("ADMIN");

    // 任何人都可以访问的权限
    http.authorizeRequests()
      .antMatchers(sparkYunProperties.getAnonymousUrl().toArray(new String[0]))
      .permitAll();

    // 需要token才可以访问的权限
    List<String> excludePaths = new ArrayList<>();
    excludePaths.addAll(sparkYunProperties.getAdminUrl());
    excludePaths.addAll(sparkYunProperties.getAnonymousUrl());

    // token
    http.addFilterBefore(new JwtAuthenticationFilter(authenticationManagerBean(), excludePaths, sparkYunProperties),
      UsernamePasswordAuthenticationFilter.class);
    http.authorizeRequests().antMatchers("/**").authenticated();

    http.formLogin();
    return http.build();
  }

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
