package com.isxcode.star.backend.module.tenant.service;

import com.isxcode.star.api.constants.Roles;
import com.isxcode.star.api.constants.TenantStatus;
import com.isxcode.star.api.exception.SparkYunException;
import com.isxcode.star.api.pojos.tenant.req.TetAddTenantReq;
import com.isxcode.star.api.pojos.tenant.req.TetUpdateTenantBySystemAdminReq;
import com.isxcode.star.api.pojos.tenant.req.TetUpdateTenantByTenantAdminReq;
import com.isxcode.star.api.pojos.tenant.res.TetQueryUserTenantRes;
import com.isxcode.star.backend.module.tenant.entity.TenantEntity;
import com.isxcode.star.backend.module.tenant.mapper.TenantMapper;
import com.isxcode.star.backend.module.tenant.repository.TenantRepository;
import com.isxcode.star.backend.module.tenant.user.entity.TenantUserEntity;
import com.isxcode.star.backend.module.tenant.user.repository.TenantUserRepository;
import com.isxcode.star.backend.module.user.entity.UserEntity;
import com.isxcode.star.backend.module.user.repository.UserRepository;
import com.isxcode.star.backend.module.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.isxcode.star.backend.config.WebSecurityConfig.JPA_TENANT_MODE;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;


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
    Optional<TenantEntity> tenantEntityOptional = tenantRepository.findByName(tetAddTenantReq.getName());
    if (tenantEntityOptional.isPresent()) {
      throw new SparkYunException("租户名称重复");
    }

    // 判断管理员是否存在
    Optional<UserEntity> userEntityOptional = userRepository.findById(tetAddTenantReq.getAdminUserId());
    if (!userEntityOptional.isPresent()) {
      throw new SparkYunException("用户不存在");
    }

    // 持久化租户
    TenantEntity tenantEntity = tenantRepository.save(tenantMapper.tetAddTenantReqToTenantEntity(tetAddTenantReq));

    // 初始化租户管理员
    TenantUserEntity tenantUserEntity = TenantUserEntity.builder()
      .userId(tetAddTenantReq.getAdminUserId())
      .tenantId(tenantEntity.getId())
      .roleCode(Roles.TENANT_ADMIN)
      .status(TenantStatus.ENABLE)
      .build();

    // 持久化租户管理员关系
    tenantUserRepository.save(tenantUserEntity);
  }

  public List<TetQueryUserTenantRes> queryUserTenant() {

    // 判断用户是否有租户
    List<TenantUserEntity> tenantUserEntities = tenantUserRepository.findAllByUserId(USER_ID.get());
    if (tenantUserEntities.isEmpty()) {
      throw new SparkYunException("请管理员添加进入租户");
    }

    // 获取租户id的list
    List<String> tenantIds = tenantUserEntities.stream().map(TenantUserEntity::getTenantId).collect(Collectors.toList());

    // 查询用户最近一次租户
    UserEntity userEntity = userRepository.findById(USER_ID.get()).get();
    if (!tenantIds.contains(userEntity.getCurrentTenantId())) {
      userEntity.setCurrentTenantId(tenantIds.get(0));
      // 更新用户最近一次租户
      userRepository.save(userEntity);
    }

    // 查询租户信息
    List<TenantEntity> tenantEntityList = tenantRepository.findAllById(tenantIds);

    // TenantEntity To TetQueryUserTenantRes
    List<TetQueryUserTenantRes> userTenantResList = tenantMapper.tenantEntityToTetQueryUserTenantResList(tenantEntityList);

    // 标记当前租户
    userTenantResList.forEach(e -> {
      if (userEntity.getCurrentTenantId().equals(e.getId())) {
        e.setCurrentTenant(true);
      }
    });

    return userTenantResList;
  }

  public void updateTenantByTenantAdmin(TetUpdateTenantBySystemAdminReq tetUpdateTenantBySystemAdminReq) {

    // 判断租户是否存在
    Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(tetUpdateTenantBySystemAdminReq.getId());
    if (!tenantEntityOptional.isPresent()) {
      throw new SparkYunException("租户不存在");
    }

    // TetUpdateTenantBySystemAdminReq To TenantEntity
    TenantEntity tenantEntity = tenantMapper.tetUpdateTenantBySystemAdminReqToTenantEntity(tetUpdateTenantBySystemAdminReq, tenantEntityOptional.get());

    // 持久化对象
    tenantRepository.save(tenantEntity);
  }

  public void TetUpdateTenantByTenantAdminReq(TetUpdateTenantByTenantAdminReq tetUpdateTenantByTenantAdminReq) {

    // 判断租户是否存在
    Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(tetUpdateTenantByTenantAdminReq.getId());
    if (!tenantEntityOptional.isPresent()) {
      throw new SparkYunException("租户不存在");
    }

    // TetUpdateTenantByTenantAdminReq To TenantEntity
    TenantEntity tenantEntity = tenantMapper.tetUpdateTenantByTenantAdminReqToTenantEntity(tetUpdateTenantByTenantAdminReq, tenantEntityOptional.get());

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

    // 持久化
    tenantRepository.save(tenantEntity);
  }

}
