package com.isxcode.spark.modules.user.service;

import static com.isxcode.spark.common.config.CommonConfig.USER_ID;

import cn.hutool.crypto.SecureUtil;
import com.isxcode.spark.api.tenant.constants.TenantStatus;
import com.isxcode.spark.api.user.constants.RoleType;
import com.isxcode.spark.api.user.constants.UserStatus;
import com.isxcode.spark.api.user.req.*;
import com.isxcode.spark.api.user.res.*;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.utils.jwt.JwtUtils;
import com.isxcode.spark.modules.tenant.entity.TenantEntity;
import com.isxcode.spark.modules.tenant.repository.TenantRepository;
import com.isxcode.spark.modules.user.mapper.UserMapper;
import com.isxcode.spark.security.user.TenantUserEntity;
import com.isxcode.spark.security.user.TenantUserRepository;
import com.isxcode.spark.security.user.UserEntity;
import com.isxcode.spark.security.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserBizService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final IsxAppProperties isxAppProperties;

    private final TenantRepository tenantRepository;

    private final TenantUserRepository tenantUserRepository;

    public LoginRes login(LoginReq usrLoginReq) {

        // 判断用户是否存在
        Optional<UserEntity> userEntityOptional = userRepository.findByAccount(usrLoginReq.getAccount());
        if (!userEntityOptional.isPresent()) {
            throw new IsxAppException("账号或者密码不正确");
        }
        UserEntity userEntity = userEntityOptional.get();

        // 判断用户是否禁用
        if (UserStatus.DISABLE.equals(userEntity.getStatus())) {
            throw new IsxAppException("账号已被禁用，请联系管理员");
        }

        // 如果是系统管理员，首次登录，插入配置的密码并保存
        if (RoleType.SYS_ADMIN.equals(userEntity.getRoleCode()) && Strings.isEmpty(userEntity.getPasswd())) {
            userEntity.setPasswd(SecureUtil.md5(isxAppProperties.getAdminPasswd()));
            userRepository.save(userEntity);
        }

        // 判断密码是否合法
        if (!SecureUtil.md5(usrLoginReq.getPasswd()).equals(userEntity.getPasswd())) {
            throw new IsxAppException("账号或者密码不正确");
        }

        // 生成token
        String jwtToken = JwtUtils.encrypt(isxAppProperties.getAesSlat(), userEntity.getId(),
            isxAppProperties.getJwtKey(), isxAppProperties.getExpirationMin());

        // 如果是系统管理员直接返回
        if (RoleType.SYS_ADMIN.equals(userEntity.getRoleCode())) {
            return LoginRes.builder().tenantId(userEntity.getCurrentTenantId()).username(userEntity.getUsername())
                .phone(userEntity.getPhone()).email(userEntity.getEmail()).remark(userEntity.getRemark())
                .token(jwtToken).role(userEntity.getRoleCode()).build();
        }

        // 如果用户不在任何一个租户报错
        List<TenantUserEntity> tenantUserEntities = tenantUserRepository.findAllByUserId(userEntity.getId());
        if (tenantUserEntities.isEmpty()) {
            throw new IsxAppException("无可用租户，请联系管理员");
        }

        // 如果用户没有任何启动租户报错
        List<String> tenantId =
            tenantUserEntities.stream().map(TenantUserEntity::getTenantId).collect(Collectors.toList());
        List<TenantEntity> enableTenants = tenantRepository.findAllByIdInAndStatus(tenantId, TenantStatus.ENABLE);
        if (enableTenants.isEmpty()) {
            throw new IsxAppException("无启用租户，请联系管理员");
        }

        // 如果用户当前租户id启动则返回当前租户，没有则随机挑一个
        List<String> enableTenantIds = enableTenants.stream().map(TenantEntity::getId).collect(Collectors.toList());
        String currentTenantId;
        if (!Strings.isEmpty(userEntity.getCurrentTenantId())
            && enableTenantIds.contains(userEntity.getCurrentTenantId())) {
            currentTenantId = userEntity.getCurrentTenantId();
        } else {
            currentTenantId = enableTenants.get(0).getId();
        }

        userEntity.setCurrentTenantId(currentTenantId);
        userRepository.save(userEntity);

        // 返回用户在租户中的角色
        Optional<TenantUserEntity> tenantUserEntityOptional =
            tenantUserRepository.findByTenantIdAndUserId(currentTenantId, userEntity.getId());
        if (!tenantUserEntityOptional.isPresent()) {
            throw new IsxAppException("无可用租户，请联系管理员");
        }

        // 生成token并返回
        return new LoginRes(userEntity.getUsername(), userEntity.getPhone(), userEntity.getEmail(),
            userEntity.getRemark(), jwtToken, currentTenantId, tenantUserEntityOptional.get().getRoleCode());
    }

    public GetUserRes getUser() {

        // 判断用户是否存在
        Optional<UserEntity> userEntityOptional = userRepository.findById(USER_ID.get());
        if (!userEntityOptional.isPresent()) {
            throw new IsxAppException("账号或者密码不正确");
        }
        UserEntity userEntity = userEntityOptional.get();

        // 判断用户是否禁用
        if (UserStatus.DISABLE.equals(userEntity.getStatus())) {
            throw new IsxAppException("账号已被禁用，请联系管理员");
        }

        // 生成token
        String jwtToken = JwtUtils.encrypt(isxAppProperties.getAesSlat(), userEntity.getId(),
            isxAppProperties.getJwtKey(), isxAppProperties.getExpirationMin());

        // 如果是系统管理员直接返回
        if (RoleType.SYS_ADMIN.equals(userEntity.getRoleCode())) {
            return GetUserRes.builder().tenantId(userEntity.getCurrentTenantId()).username(userEntity.getUsername())
                .token(jwtToken).role(userEntity.getRoleCode()).build();
        }

        // 获取用户最近一次租户信息
        if (Strings.isEmpty(userEntity.getCurrentTenantId())) {
            throw new IsxAppException("无可用租户，请联系管理员");
        }
        Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(userEntity.getCurrentTenantId());

        // 如果租户不存在,则随机选择一个
        String currentTenantId;
        if (!tenantEntityOptional.isPresent()) {
            List<TenantUserEntity> tenantUserEntities = tenantUserRepository.findAllByUserId(userEntity.getId());
            if (tenantUserEntities.isEmpty()) {
                throw new IsxAppException("无可用租户，请联系管理员");
            }
            currentTenantId = tenantUserEntities.get(0).getTenantId();
            userEntity.setCurrentTenantId(currentTenantId);
            userRepository.save(userEntity);
        } else {
            currentTenantId = tenantEntityOptional.get().getId();
        }

        // 返回用户在租户中的角色
        Optional<TenantUserEntity> tenantUserEntityOptional =
            tenantUserRepository.findByTenantIdAndUserId(currentTenantId, userEntity.getId());
        if (!tenantUserEntityOptional.isPresent()) {
            throw new IsxAppException("无可用租户，请联系管理员");
        }

        // 生成token并返回
        return new GetUserRes(userEntity.getUsername(), userEntity.getPhone(), userEntity.getEmail(),
            userEntity.getRemark(), jwtToken, currentTenantId, tenantUserEntityOptional.get().getRoleCode());
    }

    public void logout() {

        System.out.println("用户退出登录");
    }

    public void addUser(AddUserReq usrAddUserReq) {

        // 判断账号是否存在
        Optional<UserEntity> userEntityOptional = userRepository.findByAccount(usrAddUserReq.getAccount());
        if (userEntityOptional.isPresent()) {
            throw new IsxAppException("用户已存在");
        }

        // 判断手机号是否存在
        if (!Strings.isEmpty(usrAddUserReq.getPhone())) {
            Optional<UserEntity> byPhoneOptional = userRepository.findByPhone(usrAddUserReq.getPhone());
            if (byPhoneOptional.isPresent()) {
                throw new IsxAppException("手机号已存在");
            }
        }

        // 判断邮箱是否存在
        if (!Strings.isEmpty(usrAddUserReq.getEmail())) {
            Optional<UserEntity> byPhoneOptional = userRepository.findByEmail(usrAddUserReq.getEmail());
            if (byPhoneOptional.isPresent()) {
                throw new IsxAppException("邮箱已存在");
            }
        }

        // UsrAddUserReq To UserEntity
        UserEntity userEntity = userMapper.addUserReqToUserEntity(usrAddUserReq);
        userEntity.setStatus(UserStatus.ENABLE);
        userEntity.setRoleCode(RoleType.NORMAL_MEMBER);
        userEntity.setPasswd(SecureUtil.md5(userEntity.getPasswd()));

        // 数据持久化
        userRepository.save(userEntity);
    }

    public void updateUser(UpdateUserReq usrUpdateUserReq) {

        // 判断用户是否存在
        Optional<UserEntity> userEntityOptional = userRepository.findById(usrUpdateUserReq.getId());
        if (!userEntityOptional.isPresent()) {
            throw new IsxAppException("用户不存在");
        }

        UserEntity userEntity = userMapper.updateUserReqToUserEntity(usrUpdateUserReq, userEntityOptional.get());

        userRepository.save(userEntity);
    }

    public void disableUser(DisableUserReq disableUserReq) {

        Optional<UserEntity> userEntityOptional = userRepository.findById(disableUserReq.getUserId());
        if (!userEntityOptional.isPresent()) {
            throw new IsxAppException("用户不存在");
        }

        UserEntity userEntity = userEntityOptional.get();
        userEntity.setStatus(UserStatus.DISABLE);
        userRepository.save(userEntity);
    }

    public void enableUser(EnableUserReq enableUserReq) {

        Optional<UserEntity> userEntityOptional = userRepository.findById(enableUserReq.getUserId());
        if (!userEntityOptional.isPresent()) {
            throw new IsxAppException("用户不存在");
        }

        UserEntity userEntity = userEntityOptional.get();
        userEntity.setStatus(UserStatus.ENABLE);
        userRepository.save(userEntity);
    }

    public void deleteUser(DeleteUserReq deleteUserReq) {

        Optional<UserEntity> userEntityOptional = userRepository.findById(deleteUserReq.getUserId());
        if (!userEntityOptional.isPresent()) {
            throw new IsxAppException("用户不存在");
        }

        userRepository.deleteById(deleteUserReq.getUserId());
    }

    public Page<PageUserRes> pageUser(PageUserReq usrQueryAllUsersReq) {

        Page<UserEntity> userEntitiesPage = userRepository.searchAllUser(usrQueryAllUsersReq.getSearchKeyWord(),
            PageRequest.of(usrQueryAllUsersReq.getPage(), usrQueryAllUsersReq.getPageSize()));

        return userEntitiesPage.map(userMapper::userEntityToUsrQueryAllUsersRes);
    }

    public Page<PageEnableUserRes> pageEnableUser(PageEnableUserReq usrQueryAllEnableUsersReq) {

        Page<UserEntity> userEntitiesPage =
            userRepository.searchAllEnableUser(usrQueryAllEnableUsersReq.getSearchKeyWord(),
                PageRequest.of(usrQueryAllEnableUsersReq.getPage(), usrQueryAllEnableUsersReq.getPageSize()));

        return userMapper.userEntityToUsrQueryAllEnableUsersResPage(userEntitiesPage);
    }

    public void updateUserInfo(UpdateUserInfoReq updateUserInfoReq) {

        Optional<UserEntity> userEntityOptional = userRepository.findById(USER_ID.get());

        if (!userEntityOptional.isPresent()) {
            throw new IsxAppException("用户不存在");
        }

        UserEntity userEntity = userMapper.updateUserInfoToUserEntity(updateUserInfoReq, userEntityOptional.get());

        userRepository.save(userEntity);
    }

    public GetAnonymousTokenRes getAnonymousToken(GetAnonymousTokenReq getAnonymousTokenReq) {

        String jwtToken = JwtUtils.encrypt(isxAppProperties.getAesSlat(), "sy_anonymous", isxAppProperties.getJwtKey(),
            getAnonymousTokenReq.getValidDay() * 24 * 60);

        return GetAnonymousTokenRes.builder().token(jwtToken).build();
    }
}
