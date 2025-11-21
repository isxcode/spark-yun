package com.isxcode.spark.security.main;

import static com.isxcode.spark.common.config.CommonConfig.TENANT_ID;

import com.isxcode.spark.api.tenant.constants.TenantStatus;
import com.isxcode.spark.api.user.constants.RoleType;
import com.isxcode.spark.api.user.constants.UserStatus;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.security.user.TenantUserEntity;
import com.isxcode.spark.security.user.TenantUserRepository;
import com.isxcode.spark.security.user.UserEntity;
import com.isxcode.spark.security.user.UserRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private final TenantUserRepository tenantUserRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        // 返回匿名者用户对象
        if ("sy_anonymous".equals(userId)) {
            return User.withUsername(userId).password("")
                .authorities(AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ANONYMOUS")).build();
        }

        // 判断用户是否存在
        UserEntity userInfo = userRepository.findById(userId).orElseThrow(() -> new IsxAppException("用户不存在"));

        // 用户被禁用了，直接退出
        if (UserStatus.DISABLE.equals(userInfo.getStatus())) {
            throw new IsxAppException("用户被禁用，请联系管理员!");
        }

        // 获取用户系统权限
        String authority = userInfo.getRoleCode();

        // 如果不是系统管理员，且没有TENANT_ID则直接报错，缺少tenantId
        if (!RoleType.SYS_ADMIN.equals(authority) && Strings.isEmpty(TENANT_ID.get())) {
            throw new IsxAppException("缺少tenantId");
        }

        // 获取用户租户权限
        if (!RoleType.SYS_ADMIN.equals(authority)) {
            if (!Strings.isEmpty(TENANT_ID.get()) && !"undefined".equals(TENANT_ID.get())) {

                // 获取成员信息
                TenantUserEntity memberInfo = tenantUserRepository.findByTenantIdAndUserId(TENANT_ID.get(), userId)
                    .orElseThrow(() -> new IsxAppException("用户不在租户中，请联系管理员!"));

                // 判断成员是否被禁用
                if (TenantStatus.DISABLE.equals(memberInfo.getStatus())) {
                    throw new IsxAppException("用户被租户禁用，请联系管理员!");
                }

                // 封装成员权限
                authority = authority + "," + memberInfo.getRoleCode();
            }
        }

        // 返回用户信息
        return User.withUsername(userId).password(userInfo.getPasswd())
            .authorities(AuthorityUtils.commaSeparatedStringToAuthorityList(authority)).build();
    }
}
