package com.isxcode.star.backend.module.user.service;

import com.isxcode.star.api.constants.Roles;
import com.isxcode.star.api.constants.UserStatus;
import com.isxcode.star.api.exception.SparkYunException;
import com.isxcode.star.api.pojos.user.req.UsrAddUserReq;
import com.isxcode.star.api.pojos.user.req.UsrLoginReq;
import com.isxcode.star.api.pojos.user.req.UsrQueryAllEnableUsersReq;
import com.isxcode.star.api.pojos.user.req.UsrQueryAllUsersReq;
import com.isxcode.star.api.pojos.user.req.UsrUpdateUserReq;
import com.isxcode.star.api.pojos.user.res.UsrLoginRes;
import com.isxcode.star.api.pojos.user.res.UsrQueryAllEnableUsersRes;
import com.isxcode.star.api.pojos.user.res.UsrQueryAllUsersRes;
import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.api.utils.JwtUtils;
import com.isxcode.star.backend.module.tenant.entity.TenantEntity;
import com.isxcode.star.backend.module.tenant.repository.TenantRepository;
import com.isxcode.star.backend.module.tenant.user.entity.TenantUserEntity;
import com.isxcode.star.backend.module.tenant.user.repository.TenantUserRepository;
import com.isxcode.star.backend.module.user.entity.UserEntity;
import com.isxcode.star.backend.module.user.mapper.UserMapper;
import com.isxcode.star.backend.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/** 用户模块. */
@Service
@RequiredArgsConstructor
@Transactional
public class UserBizService {

  private final UserRepository userRepository;

  private final UserMapper userMapper;

  private final SparkYunProperties sparkYunProperties;

  private final TenantRepository tenantRepository;

  private final TenantUserRepository tenantUserRepository;

  /**
   * 用户登录.
   */
  public UsrLoginRes login(UsrLoginReq usrLoginReq) {

    // 判断用户是否存在
    Optional<UserEntity> userEntityOptional = userRepository.findByAccount(usrLoginReq.getAccount());
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }
    UserEntity userEntity = userEntityOptional.get();

    // 判断用户是否禁用
    if (UserStatus.DISABLE.equals(userEntity.getStatus())) {
      throw new SparkYunException("该用户已被禁用");
    }

    // 如果是系统管理员，首次登录，插入配置的密码并保存
    if (Roles.SYS_ADMIN.equals(userEntity.getRoleCode()) && Strings.isEmpty(userEntity.getPasswd())) {
      userEntity.setPasswd(sparkYunProperties.getAdminPasswd());
      userRepository.save(userEntity);
    }

    // 判断密码是否合法
    if (!usrLoginReq.getPasswd().equals(userEntity.getPasswd())) {
      throw new SparkYunException("账号或者密码不正确");
    }

    // 生成token
    String jwtToken = JwtUtils.encrypt(sparkYunProperties.getAesSlat(), userEntity.getId(), sparkYunProperties.getJwtKey(), sparkYunProperties.getExpirationMin());

    // 如果是系统管理员直接返回
    if (Roles.SYS_ADMIN.equals(userEntity.getRoleCode())) {
      return UsrLoginRes.builder().tenantId(userEntity.getCurrentTenantId()).username(userEntity.getUsername()).token(jwtToken).role(userEntity.getRoleCode()).build();
    }

    // 获取用户最近一次租户信息
    if (Strings.isEmpty(userEntity.getCurrentTenantId())) {
      throw new SparkYunException("申请管理员，添加到租户");
    }
    Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(userEntity.getCurrentTenantId());

    // 如果租户不存在,则随机选择一个
    String currentTenantId;
    if (!tenantEntityOptional.isPresent()) {
      List<TenantUserEntity> tenantUserEntities = tenantUserRepository.findAllByUserId(userEntity.getId());
      if (tenantUserEntities.isEmpty()) {
        throw new SparkYunException("申请管理员，添加到租户");
      }
      currentTenantId = tenantUserEntities.get(0).getTenantId();
      userEntity.setCurrentTenantId(currentTenantId);
      userRepository.save(userEntity);
    } else {
      currentTenantId = tenantEntityOptional.get().getId();
    }

    // 返回用户在租户中的角色
    Optional<TenantUserEntity> tenantUserEntityOptional = tenantUserRepository.findByTenantIdAndUserId(currentTenantId, userEntity.getId());
    if (!tenantUserEntityOptional.isPresent()) {
      throw new SparkYunException("申请管理员，添加到租户");
    }

    // 生成token并返回
    return new UsrLoginRes(userEntity.getUsername(), jwtToken, currentTenantId, tenantUserEntityOptional.get().getRoleCode());
  }

  public void logout() {

    System.out.println("用户退出登录");
  }

  public void addUser() {

  }

  /**
   * 创建用户.
   */
  public void addUser(UsrAddUserReq usrAddUserReq) {

    // 判断账号是否存在
    Optional<UserEntity> userEntityOptional = userRepository.findByAccount(usrAddUserReq.getAccount());
    if (userEntityOptional.isPresent()) {
      throw new SparkYunException("用户已存在");
    }

    // 判断手机号是否存在
    if (!Strings.isEmpty(usrAddUserReq.getPhone())) {
      Optional<UserEntity> byPhoneOptional = userRepository.findByPhone(usrAddUserReq.getPhone());
      if (byPhoneOptional.isPresent()) {
        throw new SparkYunException("手机号已存在");
      }
    }

    // 判断邮箱是否存在
    if (!Strings.isEmpty(usrAddUserReq.getEmail())) {
      Optional<UserEntity> byPhoneOptional = userRepository.findByEmail(usrAddUserReq.getEmail());
      if (byPhoneOptional.isPresent()) {
        throw new SparkYunException("邮箱已存在");
      }
    }

    // UsrAddUserReq To UserEntity
    UserEntity userEntity = userMapper.usrAddUserReqToUserEntity(usrAddUserReq);

    // 数据持久化
    userRepository.save(userEntity);
  }

  public void updateUser(UsrUpdateUserReq usrUpdateUserReq) {

    // 判断用户是否存在
    Optional<UserEntity> userEntityOptional = userRepository.findById(usrUpdateUserReq.getId());
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    // UsrUpdateUserReq To UserEntity
    UserEntity userEntity = userMapper.usrUpdateUserReqToUserEntity(usrUpdateUserReq, userEntityOptional.get());

    userRepository.save(userEntity);
  }

  public void disableUser(String userId) {

    Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    UserEntity userEntity = userEntityOptional.get();
    userEntity.setStatus(UserStatus.DISABLE);
    userRepository.save(userEntity);
  }

  public void enableUser(String userId) {

    Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    UserEntity userEntity = userEntityOptional.get();
    userEntity.setStatus(UserStatus.ENABLE);
    userRepository.save(userEntity);
  }

  public void deleteUser(String userId) {

    Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    userRepository.deleteById(userId);
  }

  public Page<UsrQueryAllUsersRes> queryAllUsers(UsrQueryAllUsersReq usrQueryAllUsersReq) {

    Page<UserEntity> userEntitiesPage = userRepository.searchAllUser(usrQueryAllUsersReq.getSearchKeyWord(),
      PageRequest.of(usrQueryAllUsersReq.getPage(), usrQueryAllUsersReq.getPageSize()));

    return userMapper.userEntityToUsrQueryAllUsersResPage(userEntitiesPage);
  }

  public Page<UsrQueryAllEnableUsersRes> queryAllEnableUsers(UsrQueryAllEnableUsersReq usrQueryAllEnableUsersReq) {

    Page<UserEntity> userEntitiesPage =
      userRepository.searchAllEnableUser(usrQueryAllEnableUsersReq.getSearchKeyWord(),
        PageRequest.of(usrQueryAllEnableUsersReq.getPage(), usrQueryAllEnableUsersReq.getPageSize()));

    return userMapper.userEntityToUsrQueryAllEnableUsersResPage(userEntitiesPage);
  }
}
