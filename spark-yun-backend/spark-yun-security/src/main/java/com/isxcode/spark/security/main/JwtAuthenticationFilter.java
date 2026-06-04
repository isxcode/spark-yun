package com.isxcode.spark.security.main;

import static com.isxcode.spark.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.spark.common.config.CommonConfig.USER_ID;

import com.isxcode.spark.backend.api.base.constants.SecurityConstants;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.utils.jwt.JwtUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, List<String> excludeUrlPaths,
        IsxAppProperties isxAppProperties) {

        this.authenticationManager = authenticationManager;
        this.excludeUrlPaths = initExcludeUrlPaths(excludeUrlPaths);
        this.isxAppProperties = isxAppProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // 获取用户token
            String authorization = request.getHeader(SecurityConstants.HEADER_AUTHORIZATION);
            if (authorization == null) {
                request.getRequestDispatcher(SecurityConstants.TOKEN_IS_NULL_PATH).forward(request, response);
                return;
            }

            // 获取用户的租户id
            String tenantId = request.getHeader(SecurityConstants.HEADER_TENANT_ID);
            if (tenantId != null) {
                TENANT_ID.set(tenantId);
            }

            // 验证jwt, 获取用户id
            String userUuid;
            try {
                userUuid = JwtUtils.decrypt(isxAppProperties.getJwtKey(), authorization, isxAppProperties.getAesSlat(),
                    String.class);
                USER_ID.set(userUuid);
            } catch (Exception e) {
                log.debug(e.getMessage(), e);
                request.getRequestDispatcher(SecurityConstants.TOKEN_IS_INVALID_PATH).forward(request, response);
                return;
            }

            // 通过用户id，给用户授权
            try {
                Authentication authentication =
                    authenticationManager.authenticate(new AuthenticationToken(userUuid, tenantId));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (IsxAppException isxAppException) {
                log.debug(isxAppException.getMessage(), isxAppException);
                request.getRequestDispatcher(SecurityConstants.TOKEN_IS_INVALID_PATH).forward(request, response);
                return;
            } catch (Exception e) {
                log.debug(e.getMessage(), e);
                request.getRequestDispatcher(SecurityConstants.AUTH_ERROR_PATH).forward(request, response);
                return;
            }

            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
            USER_ID.remove();
            TENANT_ID.remove();
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {

        return excludeUrlPaths.stream().anyMatch(p -> antPathMatcher.match(p, request.getServletPath()));
    }

    private List<String> initExcludeUrlPaths(List<String> excludeUrlPaths) {

        List<String> paths = new ArrayList<>();
        if (excludeUrlPaths != null) {
            paths.addAll(excludeUrlPaths);
        }
        paths.add(SecurityConstants.TOKEN_IS_NULL_PATH);
        paths.add(SecurityConstants.TOKEN_IS_INVALID_PATH);
        paths.add(SecurityConstants.AUTH_ERROR_PATH);
        return List.copyOf(paths);
    }
}
