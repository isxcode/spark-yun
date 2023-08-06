package com.isxcode.star.modules.tenant.mapper;

import com.isxcode.star.api.tenant.pojos.req.TetAddTenantReq;
import com.isxcode.star.api.tenant.pojos.req.TetUpdateTenantBySystemAdminReq;
import com.isxcode.star.api.tenant.pojos.req.TetUpdateTenantByTenantAdminReq;
import com.isxcode.star.api.tenant.pojos.res.TetQueryTenantRes;
import com.isxcode.star.api.tenant.pojos.res.TetQueryUserTenantRes;
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
  TenantEntity tetAddTenantReqToTenantEntity(TetAddTenantReq tetAddTenantReq);

  /** TenantEntity To TetQueryUserTenantRes. */
  TetQueryUserTenantRes tenantEntityToTetQueryUserTenantRes(TenantEntity tenantEntity);

  List<TetQueryUserTenantRes> tenantEntityToTetQueryUserTenantResList(
      List<TenantEntity> tenantEntities);

  @Mapping(target = "remark", source = "tetUpdateTenantBySystemAdminReq.remark")
  @Mapping(target = "maxWorkflowNum", source = "tetUpdateTenantBySystemAdminReq.maxWorkflowNum")
  @Mapping(target = "maxMemberNum", source = "tetUpdateTenantBySystemAdminReq.maxMemberNum")
  @Mapping(target = "name", source = "tetUpdateTenantBySystemAdminReq.name")
  @Mapping(target = "id", source = "tenantEntity.id")
  TenantEntity tetUpdateTenantBySystemAdminReqToTenantEntity(
      TetUpdateTenantBySystemAdminReq tetUpdateTenantBySystemAdminReq, TenantEntity tenantEntity);

  @Mapping(target = "introduce", source = "tetUpdateTenantByTenantAdminReq.introduce")
  @Mapping(target = "id", source = "tenantEntity.id")
  TenantEntity tetUpdateTenantByTenantAdminReqToTenantEntity(
      TetUpdateTenantByTenantAdminReq tetUpdateTenantByTenantAdminReq, TenantEntity tenantEntity);

  @Mapping(target = "checkDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  TetQueryTenantRes tenantEntityToTetQueryTenantRes(TenantEntity tenantEntity);

  List<TetQueryTenantRes> tenantEntityToTetQueryTenantResList(List<TenantEntity> tenantEntities);

  default Page<TetQueryTenantRes> tenantEntityToTetQueryTenantResPage(
      Page<TenantEntity> tenantEntityPage) {
    List<TetQueryTenantRes> dtoList =
        tenantEntityToTetQueryTenantResList(tenantEntityPage.getContent());
    return new PageImpl<>(
        dtoList, tenantEntityPage.getPageable(), tenantEntityPage.getTotalElements());
  }
}
