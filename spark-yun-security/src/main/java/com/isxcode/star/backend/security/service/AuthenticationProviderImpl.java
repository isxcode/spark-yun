package com.isxcode.star.backend.security.service;

import com.isxcode.star.api.exception.SparkYunException;
import com.isxcode.star.backend.security.pojo.AuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 权限核心,身份认证,权限赋值 Note: 授权一定要加 ROLE_
 *
 * <p>注解使用 Example: ROLE_USER # @Secured({"ROLE_USER"}) # @PreAuthorize("hasRole('USER')")
 *
 * @author isxcode
 * @since 0.0.1
 */
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UserDetails userDetail =
                userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
        if (userDetail == null) {
          throw new SparkYunException("用户不存在");
        }

        // 用户赋权
      AuthenticationToken authenticationToken =
        new AuthenticationToken(userDetail.getUsername(), userDetail.getPassword(), userDetail.getAuthorities());
        authenticationToken.setDetails(userDetail);

        // 上下文保存用户信息
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return false;
    }
}
