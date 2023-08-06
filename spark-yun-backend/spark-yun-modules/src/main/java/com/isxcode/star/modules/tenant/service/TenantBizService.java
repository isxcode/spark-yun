package com.isxcode.star.modules.tenant.service;

import static com.isxcode.star.security.main.WebSecurityConfig.JPA_TENANT_MODE;
import static com.isxcode.star.security.main.WebSecurityConfig.USER_ID;

import com.isxcode.star.api.tenant.constants.TenantStatus;
import com.isxcode.star.api.tenant.pojos.req.TetAddTenantReq;
import com.isxcode.star.api.tenant.pojos.req.TetQueryTenantReq;
import com.isxcode.star.api.tenant.pojos.req.TetUpdateTenantBySystemAdminReq;
import com.isxcode.star.api.tenant.pojos.req.TetUpdateTenantByTenantAdminReq;
import com.isxcode.star.api.tenant.pojos.res.TetGetTenantRes;
import com.isxcode.star.api.tenant.pojos.res.TetQueryTenantRes;
import com.isxcode.star.api.tenant.pojos.res.TetQueryUserTenantRes;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.backend.api.base.exceptions.SparkYunException;
import com.isxcode.star.modules.tenant.entity.TenantEntity;
import com.isxcode.star.modules.tenant.mapper.TenantMapper;
import com.isxcode.star.modules.tenant.repository.TenantRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowRepository;
import com.isxcode.star.security.user.TenantUserEntity;
import com.isxcode.star.security.user.TenantUserRepository;
import com.isxcode.star.security.user.UserEntity;
import com.isxcode.star.security.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
public class TenantBizService {

  private final TenantRepository tenantRepository;

  private final UserRepository userRepository;

  private final TenantUserRepository tenantUserRepository;

  private final TenantMapper tenantMapper;

  private final WorkflowRepository workflowRepository;

  public void addTenant(TetAddTenantReq tetAddTenantReq) {

    // 判断名称是否存在
    Optional<TenantEntity> tenantEntityOptional =
        tenantRepository.findByName(tetAddTenantReq.getName());
    if (tenantEntityOptional.isPresent()) {
      throw new SparkYunException("租户名称重复");
    }

    // 判断管理员是否存在
    Optional<UserEntity> userEntityOptional =
        userRepository.findById(tetAddTenantReq.getAdminUserId());
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }
    UserEntity userEntity = userEntityOptional.get();

    TenantEntity tenant = tenantMapper.tetAddTenantReqToTenantEntity(tetAddTenantReq);
    if (tetAddTenantReq.getMaxMemberNum() != null) {
      tenant.setMaxMemberNum(Long.parseLong(String.valueOf(tetAddTenantReq.getMaxMemberNum())));
    }
    if (tetAddTenantReq.getMaxWorkflowNum() != null) {
      tenant.setMaxWorkflowNum(Long.parseLong(String.valueOf(tetAddTenantReq.getMaxWorkflowNum())));
    }

    // 持久化租户
    TenantEntity tenantEntity = tenantRepository.save(tenant);

    // 初始化租户管理员
    TenantUserEntity tenantUserEntity =
        TenantUserEntity.builder()
            .userId(tetAddTenantReq.getAdminUserId())
            .tenantId(tenantEntity.getId())
            .roleCode(RoleType.TENANT_ADMIN)
            .status(TenantStatus.ENABLE)
            .build();

    // 判断管理员是否绑定新租户
    if (Strings.isEmpty(userEntity.getCurrentTenantId())) {
      userEntity.setCurrentTenantId(tenantEntity.getId());
      userRepository.save(userEntity);
    }

    // 持久化租户管理员关系
    tenantUserRepository.save(tenantUserEntity);
  }

  public List<TetQueryUserTenantRes> queryUserTenant() {

    List<String> tenantIds;
    if ("admin_id".equals(USER_ID.get())) {
      List<TenantEntity> allTenantList = tenantRepository.findAll();
      tenantIds = allTenantList.stream().map(TenantEntity::getId).collect(Collectors.toList());
    } else {
      List<TenantUserEntity> tenantUserEntities =
          tenantUserRepository.findAllByUserId(USER_ID.get());
      tenantIds =
          tenantUserEntities.stream()
              .map(TenantUserEntity::getTenantId)
              .collect(Collectors.toList());
      if (tenantUserEntities.isEmpty()) {
        throw new SparkYunException("请管理员添加进入租户");
      }
    }

    // 查询用户最近一次租户
    UserEntity userEntity = userRepository.findById(USER_ID.get()).get();
    if (!tenantIds.isEmpty() && !tenantIds.contains(userEntity.getCurrentTenantId())) {
      userEntity.setCurrentTenantId(tenantIds.get(0));
      // 更新用户最近一次租户
      userRepository.save(userEntity);
    }

    // 查询租户信息
    List<TenantEntity> tenantEntityList = tenantRepository.findAllById(tenantIds);

    // TenantEntity To TetQueryUserTenantRes
    List<TetQueryUserTenantRes> userTenantResList =
        tenantMapper.tenantEntityToTetQueryUserTenantResList(tenantEntityList);

    // 标记当前租户
    userTenantResList.forEach(
        e -> {
          if (userEntity.getCurrentTenantId().equals(e.getId())) {
            e.setCurrentTenant(true);
          }
        });

    return userTenantResList;
  }

  public Page<TetQueryTenantRes> queryTenants(TetQueryTenantReq tetQueryTenantReq) {

    Page<TenantEntity> tenantEntityPage =
        tenantRepository.searchAll(
            tetQueryTenantReq.getSearchKeyWord(),
            PageRequest.of(tetQueryTenantReq.getPage(), tetQueryTenantReq.getPageSize()));

    return tenantMapper.tenantEntityToTetQueryTenantResPage(tenantEntityPage);
  }

  public void updateTenantByTenantAdmin(
      TetUpdateTenantBySystemAdminReq tetUpdateTenantBySystemAdminReq) {

    // 判断租户是否存在
    Optional<TenantEntity> tenantEntityOptional =
        tenantRepository.findById(tetUpdateTenantBySystemAdminReq.getId());
    if (!tenantEntityOptional.isPresent()) {
      throw new SparkYunException("租户不存在");
    }

    // TetUpdateTenantBySystemAdminReq To TenantEntity
    TenantEntity tenantEntity =
        tenantMapper.tetUpdateTenantBySystemAdminReqToTenantEntity(
            tetUpdateTenantBySystemAdminReq, tenantEntityOptional.get());

    // 持久化对象
    tenantRepository.save(tenantEntity);
  }

  public void tetUpdateTenantByTenantAdminReq(
      TetUpdateTenantByTenantAdminReq tetUpdateTenantByTenantAdminReq) {

    // 判断租户是否存在
    Optional<TenantEntity> tenantEntityOptional =
        tenantRepository.findById(tetUpdateTenantByTenantAdminReq.getId());
    if (!tenantEntityOptional.isPresent()) {
      throw new SparkYunException("租户不存在");
    }

    // TetUpdateTenantByTenantAdminReq To TenantEntity
    TenantEntity tenantEntity =
        tenantMapper.tetUpdateTenantByTenantAdminReqToTenantEntity(
            tetUpdateTenantByTenantAdminReq, tenantEntityOptional.get());

    // 持久化对象
    tenantRepository.save(tenantEntity);
  }

  public void enableTenant(String tenantId) {

    // 判断租户是否存在
    Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(tenantId);
    if (!tenantEntityOptional.isPresent()) {
      throw new SparkYunException("租户不存在");
    }

    // 设置为启用
    TenantEntity tenantEntity = tenantEntityOptional.get();
    tenantEntity.setStatus(TenantStatus.ENABLE);

    // 持久化
    tenantRepository.save(tenantEntity);
  }

  public void disableTenant(String tenantId) {

    // 判断租户是否存在
    Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(tenantId);
    if (!tenantEntityOptional.isPresent()) {
      throw new SparkYunException("租户不存在");
    }

    // 设置为启用
    TenantEntity tenantEntity = tenantEntityOptional.get();
    tenantEntity.setStatus(TenantStatus.DISABLE);

    // 持久化
    tenantRepository.save(tenantEntity);
  }

  public void deleteTenant(String tenantId) {

    tenantRepository.deleteById(tenantId);
  }

  public void checkTenant(String tenantId) {

    // 判断租户是否存在
    Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(tenantId);
    if (!tenantEntityOptional.isPresent()) {
      throw new SparkYunException("租户不存在");
    }
    TenantEntity tenantEntity = tenantEntityOptional.get();

    // 统计作业流数量
    JPA_TENANT_MODE.set(false);
    long usedWorkflowNum = workflowRepository.countByTenantId(tenantId);
    tenantEntity.setUsedWorkflowNum(usedWorkflowNum);
    JPA_TENANT_MODE.set(true);

    // 统计成员数量
    long memberNum = tenantUserRepository.countByTenantId(tenantId);
    tenantEntity.setUsedMemberNum(memberNum);
    tenantEntity.setCheckDateTime(LocalDateTime.now());

    // 持久化
    tenantRepository.save(tenantEntity);
  }

  public void chooseTenant(String tenantId) {

    Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(tenantId);
    if (!tenantEntityOptional.isPresent()) {
      throw new SparkYunException("租户不存在");
    }

    Optional<UserEntity> userEntityOptional = userRepository.findById(USER_ID.get());
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    UserEntity userEntity = userEntityOptional.get();
    userEntity.setCurrentTenantId(tenantId);
    userRepository.save(userEntity);
  }

  public TetGetTenantRes getTenant(String tenantId) {

    Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(tenantId);
    if (!tenantEntityOptional.isPresent()) {
      throw new SparkYunException("租户不存在");
    }
    TenantEntity tenantEntity = tenantEntityOptional.get();

    Optional<UserEntity> userEntityOptional = userRepository.findById(USER_ID.get());
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }
    UserEntity userEntity = userEntityOptional.get();

    // 如果是管理员直接返回
    if (RoleType.SYS_ADMIN.equals(userEntity.getRoleCode())) {
      return TetGetTenantRes.builder()
          .id(tenantEntity.getId())
          .name(tenantEntity.getName())
          .build();
    }

    // 判断用户是否在租户中
    Optional<TenantUserEntity> tenantUserEntityOptional =
        tenantUserRepository.findByTenantIdAndUserId(tenantId, USER_ID.get());
    if (!tenantUserEntityOptional.isPresent()) {
      throw new SparkYunException("不在租户中");
    }
    return TetGetTenantRes.builder().id(tenantEntity.getId()).name(tenantEntity.getName()).build();
  }
}
