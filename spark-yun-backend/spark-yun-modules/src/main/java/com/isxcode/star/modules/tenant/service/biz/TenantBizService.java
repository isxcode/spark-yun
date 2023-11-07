package com.isxcode.star.modules.tenant.service.biz;

import static com.isxcode.star.common.config.CommonConfig.JPA_TENANT_MODE;
import static com.isxcode.star.common.config.CommonConfig.USER_ID;

import com.isxcode.star.api.tenant.constants.TenantStatus;
import com.isxcode.star.api.tenant.pojos.req.*;
import com.isxcode.star.api.tenant.pojos.res.GetTenantRes;
import com.isxcode.star.api.tenant.pojos.res.PageTenantRes;
import com.isxcode.star.api.tenant.pojos.res.QueryUserTenantRes;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TenantBizService {

	private final TenantRepository tenantRepository;

	private final UserRepository userRepository;

	private final TenantUserRepository tenantUserRepository;

	private final TenantMapper tenantMapper;

	private final WorkflowRepository workflowRepository;

	public void addTenant(AddTenantReq tetAddTenantReq) {

		// 判断名称是否存在
		Optional<TenantEntity> tenantEntityOptional = tenantRepository.findByName(tetAddTenantReq.getName());
		if (tenantEntityOptional.isPresent()) {
			throw new IsxAppException("租户名称重复");
		}

		// 判断管理员是否存在
		Optional<UserEntity> userEntityOptional = userRepository.findById(tetAddTenantReq.getAdminUserId());
		if (!userEntityOptional.isPresent()) {
			throw new IsxAppException("用户不存在");
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
		TenantUserEntity tenantUserEntity = TenantUserEntity.builder().userId(tetAddTenantReq.getAdminUserId())
				.tenantId(tenantEntity.getId()).roleCode(RoleType.TENANT_ADMIN).status(TenantStatus.ENABLE).build();

		// 判断管理员是否绑定新租户
		if (Strings.isEmpty(userEntity.getCurrentTenantId())) {
			userEntity.setCurrentTenantId(tenantEntity.getId());
			userRepository.save(userEntity);
		}

		// 持久化租户管理员关系
		tenantUserRepository.save(tenantUserEntity);
	}

	public List<QueryUserTenantRes> queryUserTenant() {

		List<String> tenantIds;
		if ("admin_id".equals(USER_ID.get())) {
			List<TenantEntity> allTenantList = tenantRepository.findAll();
			tenantIds = allTenantList.stream().map(TenantEntity::getId).collect(Collectors.toList());
		} else {
			List<TenantUserEntity> tenantUserEntities = tenantUserRepository.findAllByUserId(USER_ID.get());
			tenantIds = tenantUserEntities.stream().map(TenantUserEntity::getTenantId).collect(Collectors.toList());
			if (tenantUserEntities.isEmpty()) {
				throw new IsxAppException("请管理员添加进入租户");
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
		List<QueryUserTenantRes> userTenantResList = tenantMapper
				.tenantEntityToTetQueryUserTenantResList(tenantEntityList);

		// 标记当前租户
		userTenantResList.forEach(e -> {
			if (userEntity.getCurrentTenantId().equals(e.getId())) {
				e.setCurrentTenant(true);
			}
		});

		return userTenantResList;
	}

	public Page<PageTenantRes> pageTenant(PageTenantReq tetQueryTenantReq) {

		Page<TenantEntity> tenantEntityPage = tenantRepository.searchAll(tetQueryTenantReq.getSearchKeyWord(),
				PageRequest.of(tetQueryTenantReq.getPage(), tetQueryTenantReq.getPageSize()));

		return tenantEntityPage.map(tenantMapper::tenantEntityToTetQueryTenantRes);
	}

	public void updateTenantForSystemAdmin(UpdateTenantForSystemAdminReq tetUpdateTenantBySystemAdminReq) {

		// 判断租户是否存在
		Optional<TenantEntity> tenantEntityOptional = tenantRepository
				.findById(tetUpdateTenantBySystemAdminReq.getId());
		if (!tenantEntityOptional.isPresent()) {
			throw new IsxAppException("租户不存在");
		}

		// TetUpdateTenantBySystemAdminReq To TenantEntity
		TenantEntity tenantEntity = tenantMapper.tetUpdateTenantBySystemAdminReqToTenantEntity(
				tetUpdateTenantBySystemAdminReq, tenantEntityOptional.get());

		// 持久化对象
		tenantRepository.save(tenantEntity);
	}

	public void updateTenantForTenantAdmin(UpdateTenantForTenantAdminReq tetUpdateTenantByTenantAdminReq) {

		// 判断租户是否存在
		Optional<TenantEntity> tenantEntityOptional = tenantRepository
				.findById(tetUpdateTenantByTenantAdminReq.getId());
		if (!tenantEntityOptional.isPresent()) {
			throw new IsxAppException("租户不存在");
		}

		// TetUpdateTenantByTenantAdminReq To TenantEntity
		TenantEntity tenantEntity = tenantMapper.tetUpdateTenantByTenantAdminReqToTenantEntity(
				tetUpdateTenantByTenantAdminReq, tenantEntityOptional.get());

		// 持久化对象
		tenantRepository.save(tenantEntity);
	}

	public void enableTenant(EnableTenantReq enableTenantReq) {

		// 判断租户是否存在
		Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(enableTenantReq.getTenantId());
		if (!tenantEntityOptional.isPresent()) {
			throw new IsxAppException("租户不存在");
		}

		// 设置为启用
		TenantEntity tenantEntity = tenantEntityOptional.get();
		tenantEntity.setStatus(TenantStatus.ENABLE);

		// 持久化
		tenantRepository.save(tenantEntity);
	}

	public void disableTenant(DisableTenantReq disableTenantReq) {

		// 判断租户是否存在
		Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(disableTenantReq.getTenantId());
		if (!tenantEntityOptional.isPresent()) {
			throw new IsxAppException("租户不存在");
		}

		// 设置为启用
		TenantEntity tenantEntity = tenantEntityOptional.get();
		tenantEntity.setStatus(TenantStatus.DISABLE);

		// 持久化
		tenantRepository.save(tenantEntity);
	}

	public void deleteTenant(DeleteTenantReq deleteTenantReq) {

		tenantRepository.deleteById(deleteTenantReq.getTenantId());
	}

	public void checkTenant(CheckTenantReq checkTenantReq) {

		// 判断租户是否存在
		Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(checkTenantReq.getTenantId());
		if (!tenantEntityOptional.isPresent()) {
			throw new IsxAppException("租户不存在");
		}
		TenantEntity tenantEntity = tenantEntityOptional.get();

		// 统计作业流数量
		JPA_TENANT_MODE.set(false);
		long usedWorkflowNum = workflowRepository.countByTenantId(checkTenantReq.getTenantId());
		tenantEntity.setUsedWorkflowNum(usedWorkflowNum);
		JPA_TENANT_MODE.set(true);

		// 统计成员数量
		long memberNum = tenantUserRepository.countByTenantId(checkTenantReq.getTenantId());
		tenantEntity.setUsedMemberNum(memberNum);
		tenantEntity.setCheckDateTime(LocalDateTime.now());

		// 持久化
		tenantRepository.save(tenantEntity);
	}

	public void chooseTenant(ChooseTenantReq chooseTenantReq) {

		Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(chooseTenantReq.getTenantId());
		if (!tenantEntityOptional.isPresent()) {
			throw new IsxAppException("租户不存在");
		}

		Optional<UserEntity> userEntityOptional = userRepository.findById(USER_ID.get());
		if (!userEntityOptional.isPresent()) {
			throw new IsxAppException("用户不存在");
		}

		UserEntity userEntity = userEntityOptional.get();
		userEntity.setCurrentTenantId(chooseTenantReq.getTenantId());
		userRepository.save(userEntity);
	}

	public GetTenantRes getTenant(GetTenantReq getTenantReq) {

		Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(getTenantReq.getTenantId());
		if (!tenantEntityOptional.isPresent()) {
			throw new IsxAppException("租户不存在");
		}
		TenantEntity tenantEntity = tenantEntityOptional.get();

		Optional<UserEntity> userEntityOptional = userRepository.findById(USER_ID.get());
		if (!userEntityOptional.isPresent()) {
			throw new IsxAppException("用户不存在");
		}
		UserEntity userEntity = userEntityOptional.get();

		// 如果是管理员直接返回
		if (RoleType.SYS_ADMIN.equals(userEntity.getRoleCode())) {
			return GetTenantRes.builder().id(tenantEntity.getId()).name(tenantEntity.getName()).build();
		}

		// 判断用户是否在租户中
		Optional<TenantUserEntity> tenantUserEntityOptional = tenantUserRepository
				.findByTenantIdAndUserId(getTenantReq.getTenantId(), USER_ID.get());
		if (!tenantUserEntityOptional.isPresent()) {
			throw new IsxAppException("不在租户中");
		}
		return GetTenantRes.builder().id(tenantEntity.getId()).name(tenantEntity.getName()).build();
	}
}
