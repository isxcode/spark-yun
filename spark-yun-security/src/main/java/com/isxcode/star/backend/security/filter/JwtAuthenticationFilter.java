package com.isxcode.star.backend.security.filter;

import com.isxcode.star.api.constants.SecurityConstants;
import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.api.utils.JwtUtils;
import com.isxcode.star.backend.security.pojo.AuthenticationToken;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * jwt拦截接口.
 */
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

    // 验证请求头
    String authorization = request.getHeader(SecurityConstants.HEADER_AUTHORIZATION);
    if (authorization == null) {
      request.getRequestDispatcher(SecurityConstants.TOKEN_IS_NULL_PATH).forward(request, response);
      return;
    }

    // 验证jwt
    String userUuid;
    try {
      userUuid = JwtUtils.decrypt(sparkYunProperties.getJwtKey(), authorization, sparkYunProperties.getAesSlat(), String.class);
    } catch (Exception e) {
      request
        .getRequestDispatcher(SecurityConstants.TOKEN_IS_INVALID_PATH)
        .forward(request, response);
      return;
    }

    // 验证权限
    try {
      authenticationManager.authenticate(new AuthenticationToken(userUuid));
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
