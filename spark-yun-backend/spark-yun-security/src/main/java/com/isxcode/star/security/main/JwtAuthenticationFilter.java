package com.isxcode.star.security.main;

import static com.isxcode.star.security.main.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.security.main.WebSecurityConfig.USER_ID;

import com.isxcode.star.backend.api.base.constants.SecurityConstants;
import com.isxcode.star.backend.api.base.properties.SparkYunProperties;
import com.isxcode.star.common.utils.jwt.JwtUtils;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/** jwt拦截接口. */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final AuthenticationManager authenticationManager;

  private final List<String> excludeUrlPaths;

  private final SparkYunProperties sparkYunProperties;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

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
      userUuid =
          JwtUtils.decrypt(
              sparkYunProperties.getJwtKey(),
              authorization,
              sparkYunProperties.getAesSlat(),
              String.class);
      USER_ID.set(userUuid);
    } catch (Exception e) {
      request
          .getRequestDispatcher(SecurityConstants.TOKEN_IS_INVALID_PATH)
          .forward(request, response);
      return;
    }

    // 通过用户id，给用户授权
    try {
      authenticationManager.authenticate(new AuthenticationToken(userUuid, tenantId));
    } catch (Exception e) {
      log.error(e.getMessage());
      request.getRequestDispatcher(SecurityConstants.AUTH_ERROR_PATH).forward(request, response);
      return;
    }

    doFilter(request, response, filterChain);
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {

    return excludeUrlPaths.stream()
        .anyMatch(p -> new AntPathMatcher().match(p, request.getServletPath()));
  }
}
