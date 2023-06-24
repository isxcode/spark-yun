package com.isxcode.star.backend.module.tenant.user;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

import com.isxcode.star.api.constants.user.RoleType;
import com.isxcode.star.api.constants.user.UserStatus;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.pojos.tenant.user.req.TurAddTenantUserReq;
import com.isxcode.star.api.pojos.tenant.user.req.TurQueryTenantUserReq;
import com.isxcode.star.api.pojos.tenant.user.res.TurQueryTenantUserRes;
import com.isxcode.star.backend.module.user.UserEntity;
import com.isxcode.star.backend.module.user.UserRepository;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/** 数据源模块service. */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TenantUserBizService {

  private final UserRepository userRepository;

  private final TenantUserRepository tenantUserRepository;

  private final TenantUserMapper tenantUserMapper;

  public void addTenantUser(TurAddTenantUserReq turAddTenantUserReq) {

    // 判断对象用户是否合法
    Optional<UserEntity> userEntityOptional =
        userRepository.findById(turAddTenantUserReq.getUserId());
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }
    UserEntity userEntity = userEntityOptional.get();

    // 判断该用户是否已经是成员
    Optional<TenantUserEntity> tenantUserEntityOptional =
        tenantUserRepository.findByTenantIdAndUserId(
            TENANT_ID.get(), turAddTenantUserReq.getUserId());
    if (tenantUserEntityOptional.isPresent()) {
      throw new SparkYunException("该成员已经是项目成员");
    }

    // 初始化租户用户
    TenantUserEntity tenantUserEntity =
        TenantUserEntity.builder()
            .tenantId(TENANT_ID.get())
            .userId(turAddTenantUserReq.getUserId())
            .status(UserStatus.ENABLE)
            .build();

    // 初始化用户权限
    if (turAddTenantUserReq.getIsTenantAdmin()) {
      tenantUserEntity.setRoleCode(RoleType.TENANT_ADMIN);
    } else {
      tenantUserEntity.setRoleCode(RoleType.TENANT_MEMBER);
    }

    // 判断用户当前是否有租户
    if (Strings.isEmpty(userEntity.getCurrentTenantId())) {
      userEntity.setCurrentTenantId(TENANT_ID.get());
      userRepository.save(userEntity);
    }

    // 持久化数据
    tenantUserRepository.save(tenantUserEntity);
  }

  public Page<TurQueryTenantUserRes> queryTenantUser(TurQueryTenantUserReq turAddTenantUserReq) {

    Page<Map> tenantUserEntities =
        tenantUserRepository.searchTenantUser(
            TENANT_ID.get(),
            turAddTenantUserReq.getSearchKeyWord(),
            PageRequest.of(turAddTenantUserReq.getPage(), turAddTenantUserReq.getPageSize()));

    return tenantUserMapper.turTenantUserDtoToTurQueryTenantUserResPage(tenantUserEntities);
  }

  public void removeTenantUser(String tenantUserId) {

    // 查询用户是否在租户中
    Optional<TenantUserEntity> tenantUserEntityOptional =
        tenantUserRepository.findById(tenantUserId);
    if (!tenantUserEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    // 不可以删除自己
    if (USER_ID.get().equals(tenantUserEntityOptional.get().getUserId())) {
      throw new SparkYunException("不可以移除自己");
    }

    // 删除租户用户
    tenantUserRepository.deleteById(tenantUserEntityOptional.get().getId());
  }

  public void setTenantAdmin(String tenantUserId) {

    // 查询用户是否在租户中
    Optional<TenantUserEntity> tenantUserEntityOptional =
        tenantUserRepository.findById(tenantUserId);
    if (!tenantUserEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    // 设置为租户管理员权限
    TenantUserEntity tenantUserEntity = tenantUserEntityOptional.get();
    tenantUserEntity.setRoleCode(RoleType.TENANT_ADMIN);

    // 持久化
    tenantUserRepository.save(tenantUserEntity);
  }

  public void removeTenantAdmin(String tenantUserId) {

    // 查询用户是否在租户中
    Optional<TenantUserEntity> tenantUserEntityOptional =
        tenantUserRepository.findById(tenantUserId);
    if (!tenantUserEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    // 管理员不可以移除自己
    if (RoleType.TENANT_ADMIN.equals(tenantUserEntityOptional.get().getRoleCode())
        && USER_ID.get().equals(tenantUserEntityOptional.get().getUserId())) {
      throw new SparkYunException("不可以取消自己的管理员权限");
    }

    // 设置为租户管理员权限
    TenantUserEntity tenantUserEntity = tenantUserEntityOptional.get();
    tenantUserEntity.setRoleCode(RoleType.TENANT_MEMBER);

    // 持久化
    tenantUserRepository.save(tenantUserEntity);
  }
}
