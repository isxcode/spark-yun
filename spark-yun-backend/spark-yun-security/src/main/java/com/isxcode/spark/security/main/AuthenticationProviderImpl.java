package com.isxcode.spark.security.main;

import com.isxcode.spark.common.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 权限核心,身份认证,权限赋值 Note: 授权一定要加 ROLE_
 *
 * <p>
 * 注解使用 Example: ROLE_USER # @Secured({"ROLE_USER"}) # @PreAuthorize("hasRole('USER')")
 *
 * @author isxcode
 * @since 0.0.1
 */
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // 获取用户详细信息
        String tenantId = authentication.getCredentials() == null ? null : authentication.getCredentials().toString();
        UserDetails userDetail;
        if (userDetailsService instanceof UserDetailsServiceImpl userDetailsServiceImpl) {
            userDetail = userDetailsServiceImpl.loadUserByUsername(authentication.getPrincipal().toString(), tenantId);
        } else {
            userDetail = userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
        }

        // 用户赋权
        AuthenticationToken authenticationToken = new AuthenticationToken(
            new CurrentUser(userDetail.getUsername(), tenantId), userDetail.getPassword(), userDetail.getAuthorities());
        authenticationToken.setDetails(userDetail);

        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return AuthenticationToken.class.isAssignableFrom(authentication);
    }
}
