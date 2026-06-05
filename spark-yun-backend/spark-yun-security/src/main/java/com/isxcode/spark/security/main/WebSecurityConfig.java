package com.isxcode.spark.security.main;

import com.isxcode.spark.api.user.constants.RoleType;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.security.user.TenantRepository;
import com.isxcode.spark.security.user.TenantUserRepository;
import com.isxcode.spark.security.user.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

    private final IsxAppProperties isxAppProperties;

    private final UserRepository userRepository;

    private final TenantUserRepository tenantUserRepository;

    private final TenantRepository tenantRepository;

    @Bean
    public UserDetailsService userDetailsServiceBean() {

        return new UserDetailsServiceImpl(userRepository, tenantUserRepository, tenantRepository);
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {

        return new AuthenticationProviderImpl(userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public RestSecurityExceptionHandler restSecurityExceptionHandler() {

        return new RestSecurityExceptionHandler();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager,
        AuthenticationEntryPoint authenticationEntryPoint) {

        return new JwtAuthenticationFilter(authenticationManager, openUrlPatterns(), isxAppProperties,
            authenticationEntryPoint);
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {

        DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter,
        AuthenticationEntryPoint authenticationEntryPoint, AccessDeniedHandler accessDeniedHandler) throws Exception {

        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler));
        http.authorizeHttpRequests(
            authorize -> authorize.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                .requestMatchers(toPatterns(openUrlPatterns())).permitAll()
                .requestMatchers(toPatterns(isxAppProperties.getAdminRoleUrl())).hasAuthority(RoleType.SYS_ADMIN)
                .requestMatchers(toPatterns(isxAppProperties.getAnonymousRoleUrl()))
                .hasAuthority(RoleType.ROLE_ANONYMOUS).anyRequest().authenticated());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
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

    private String[] toPatterns(List<String> patterns) {

        return patterns == null ? new String[0] : patterns.toArray(new String[0]);
    }

    private List<String> openUrlPatterns() {

        List<String> patterns = new ArrayList<>();
        if (isxAppProperties.getOpenUrl() != null) {
            patterns.addAll(isxAppProperties.getOpenUrl());
        }
        return List.copyOf(patterns);
    }
}
