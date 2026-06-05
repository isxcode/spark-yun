package com.isxcode.spark.security.main;

import com.isxcode.spark.backend.api.base.constants.SecurityConstants;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.security.ContextHolder;
import com.isxcode.spark.common.security.CurrentUser;
import com.isxcode.spark.common.utils.jwt.JwtUtils;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * jwt拦截接口.
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    private final List<String> excludeUrlPaths;

    private final IsxAppProperties isxAppProperties;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, List<String> excludeUrlPaths,
        IsxAppProperties isxAppProperties, AuthenticationEntryPoint authenticationEntryPoint) {

        this.authenticationManager = authenticationManager;
        this.excludeUrlPaths = excludeUrlPaths == null ? List.of() : List.copyOf(excludeUrlPaths);
        this.isxAppProperties = isxAppProperties;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // 获取用户token
            String authorization = request.getHeader(SecurityConstants.HEADER_AUTHORIZATION);
            if (authorization == null) {
                authenticationEntryPoint.commence(request, response,
                    new InsufficientAuthenticationException("token丢失"));
                return;
            }

            // 验证jwt, 获取用户id和租户id
            CurrentUser currentUser;
            try {
                currentUser = JwtUtils.decrypt(isxAppProperties.getJwtKey(), authorization,
                    isxAppProperties.getAesSlat(), CurrentUser.class);
                ContextHolder.setCurrentUser(currentUser.userId(), currentUser.tenantId());
            } catch (Exception e) {
                log.debug(e.getMessage(), e);
                authenticationEntryPoint.commence(request, response, new BadCredentialsException("token不合法", e));
                return;
            }

            // 通过用户id，给用户授权
            try {
                Authentication authentication = authenticationManager
                    .authenticate(new AuthenticationToken(currentUser.userId(), currentUser.tenantId()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (IsxAppException isxAppException) {
                log.debug(isxAppException.getMessage(), isxAppException);
                authenticationEntryPoint.commence(request, response,
                    new BadCredentialsException(isxAppException.getMessage(), isxAppException));
                return;
            } catch (Exception e) {
                log.debug(e.getMessage(), e);
                authenticationEntryPoint.commence(request, response, new BadCredentialsException("认证失败", e));
                return;
            }

            filterChain.doFilter(request, response);
        } finally {
            ContextHolder.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {

        return excludeUrlPaths.stream().anyMatch(p -> antPathMatcher.match(p, request.getServletPath()));
    }
}
