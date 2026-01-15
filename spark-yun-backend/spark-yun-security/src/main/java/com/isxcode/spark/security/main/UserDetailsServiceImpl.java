package com.isxcode.spark.security.main;

import static com.isxcode.spark.common.config.CommonConfig.TENANT_ID;

import com.isxcode.spark.api.tenant.constants.TenantStatus;
import com.isxcode.spark.api.user.constants.RoleType;
import com.isxcode.spark.api.user.constants.UserStatus;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.security.user.*;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private final TenantUserRepository tenantUserRepository;

    private final TenantRepository tenantRepository;

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

        // 不在有效期异常
        if (userInfo.getValidStartDateTime() != null && userInfo.getValidEndDateTime() != null) {
            if (LocalDateTime.now().isBefore(userInfo.getValidStartDateTime())
                || LocalDateTime.now().isAfter(userInfo.getValidEndDateTime())) {
                throw new IsxAppException("用户账号不在有效期内");
            }
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

                // 判断租户是否存在
                Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(memberInfo.getTenantId());
                if (!tenantEntityOptional.isPresent()) {
                    throw new IsxAppException("当前租户不可用");
                }

                // 判断租户是否禁用
                TenantEntity tenant = tenantEntityOptional.get();
                if (TenantStatus.DISABLE.equals(tenant.getStatus())) {
                    throw new IsxAppException("当前租户已被禁用");
                }

                // 判断租户是否在有效期内
                if (tenant.getValidStartDateTime() != null && tenant.getValidEndDateTime() != null) {
                    if (LocalDateTime.now().isBefore(tenant.getValidStartDateTime())
                        || LocalDateTime.now().isAfter(tenant.getValidEndDateTime())) {
                        throw new IsxAppException("当前租户不在有效期内");
                    }
                }

                // 封装成员权限
                authority = authority + "," + memberInfo.getRoleCode();
            } else {
                throw new IsxAppException("tenantId 为空异常");
            }
        }

        // 返回用户信息
        return User.withUsername(userId).password(userInfo.getPasswd())
            .authorities(AuthorityUtils.commaSeparatedStringToAuthorityList(authority)).build();
    }
}
