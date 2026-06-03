package com.isxcode.spark.security.main;

import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.security.user.TenantRepository;
import com.isxcode.spark.security.user.TenantUserRepository;
import com.isxcode.spark.security.user.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
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

    public UserDetailsService userDetailsServiceBean() {

        return new UserDetailsServiceImpl(userRepository, tenantUserRepository, tenantRepository);
    }

    public AuthenticationManager authenticationManagerBean() {

        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        authenticationProviders.add(new AuthenticationProviderImpl(userDetailsServiceBean()));
        return new AuthenticationManagerImpl(authenticationProviders);
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {

        DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 需要token才可以访问的地址
        List<String> excludePaths = new ArrayList<>();
        excludePaths.addAll(isxAppProperties.getAdminUrl());
        excludePaths.addAll(isxAppProperties.getAnonymousUrl());

        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        http.sessionManagement(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(
            authorize -> authorize.requestMatchers(isxAppProperties.getAdminUrl().toArray(new String[0]))
                .hasRole("ADMIN").requestMatchers(isxAppProperties.getAnonymousRoleUrl().toArray(new String[0]))
                .hasRole("ANONYMOUS").requestMatchers(isxAppProperties.getAnonymousUrl().toArray(new String[0]))
                .permitAll().anyRequest().authenticated());
        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManagerBean(), excludePaths, isxAppProperties),
            UsernamePasswordAuthenticationFilter.class);
        http.formLogin(Customizer.withDefaults());
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
