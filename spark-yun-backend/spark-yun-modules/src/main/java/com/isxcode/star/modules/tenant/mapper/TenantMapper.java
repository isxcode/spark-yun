package com.isxcode.star.modules.tenant.mapper;

import com.isxcode.star.api.tenant.pojos.req.AddTenantReq;
import com.isxcode.star.api.tenant.pojos.req.UpdateTenantForSystemAdminReq;
import com.isxcode.star.api.tenant.pojos.req.UpdateTenantForTenantAdminReq;
import com.isxcode.star.api.tenant.pojos.res.PageTenantRes;
import com.isxcode.star.api.tenant.pojos.res.QueryUserTenantRes;
import com.isxcode.star.api.user.constants.UserStatus;
import com.isxcode.star.modules.tenant.entity.TenantEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(componentModel = "spring")
public interface TenantMapper {

	/** TetAddTenantReq To TenantEntity. */
	@Mapping(target = "checkDateTime", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "status", constant = UserStatus.ENABLE)
	@Mapping(target = "maxMemberNum", constant = "1L")
	@Mapping(target = "usedMemberNum", constant = "1L")
	@Mapping(target = "maxWorkflowNum", constant = "0L")
	@Mapping(target = "usedWorkflowNum", constant = "0L")
	TenantEntity tetAddTenantReqToTenantEntity(AddTenantReq tetAddTenantReq);

	/** TenantEntity To TetQueryUserTenantRes. */
	QueryUserTenantRes tenantEntityToTetQueryUserTenantRes(TenantEntity tenantEntity);

	List<QueryUserTenantRes> tenantEntityToTetQueryUserTenantResList(List<TenantEntity> tenantEntities);

	@Mapping(target = "remark", source = "tetUpdateTenantBySystemAdminReq.remark")
	@Mapping(target = "maxWorkflowNum", source = "tetUpdateTenantBySystemAdminReq.maxWorkflowNum")
	@Mapping(target = "maxMemberNum", source = "tetUpdateTenantBySystemAdminReq.maxMemberNum")
	@Mapping(target = "name", source = "tetUpdateTenantBySystemAdminReq.name")
	@Mapping(target = "id", source = "tenantEntity.id")
	TenantEntity tetUpdateTenantBySystemAdminReqToTenantEntity(
			UpdateTenantForSystemAdminReq tetUpdateTenantBySystemAdminReq, TenantEntity tenantEntity);

	@Mapping(target = "introduce", source = "tetUpdateTenantByTenantAdminReq.introduce")
	@Mapping(target = "id", source = "tenantEntity.id")
	TenantEntity tetUpdateTenantByTenantAdminReqToTenantEntity(
			UpdateTenantForTenantAdminReq tetUpdateTenantByTenantAdminReq, TenantEntity tenantEntity);

	@Mapping(target = "checkDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
	PageTenantRes tenantEntityToTetQueryTenantRes(TenantEntity tenantEntity);

	List<PageTenantRes> tenantEntityToTetQueryTenantResList(List<TenantEntity> tenantEntities);

	default Page<PageTenantRes> tenantEntityToTetQueryTenantResPage(Page<TenantEntity> tenantEntityPage) {
		List<PageTenantRes> dtoList = tenantEntityToTetQueryTenantResList(tenantEntityPage.getContent());
		return new PageImpl<>(dtoList, tenantEntityPage.getPageable(), tenantEntityPage.getTotalElements());
	}
}
