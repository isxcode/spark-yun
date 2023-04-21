package com.isxcode.star.backend.module.tenant.mapper;

import com.isxcode.star.api.constants.UserStatus;
import com.isxcode.star.api.pojos.tenant.req.TetAddTenantReq;
import com.isxcode.star.backend.module.tenant.entity.TenantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TenantMapper {

  /**
   * TetAddTenantReq To TenantEntity.
   */
  @Mapping(target = "checkDateTime", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "status", constant = UserStatus.ENABLE)
  @Mapping(target = "usedMemberNum", constant = "1")
  @Mapping(target = "usedWorkflowNum", constant = "0")
  TenantEntity tetAddTenantReqToTenantEntity(TetAddTenantReq tetAddTenantReq);
}

