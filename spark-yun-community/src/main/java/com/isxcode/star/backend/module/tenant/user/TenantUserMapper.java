package com.isxcode.star.backend.module.tenant.user;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.pojos.tenant.user.res.TurQueryTenantUserRes;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(componentModel = "spring")
public interface TenantUserMapper {

  /** TurTenantUserDto To TurQueryTenantUserRes. */
  default TurQueryTenantUserRes turTenantUserDtoToTurQueryTenantUserRes(Map turTenantUserDto) {

    return JSON.parseObject(JSON.toJSONString(turTenantUserDto), TurQueryTenantUserRes.class);
  }

  List<TurQueryTenantUserRes> turTenantUserDtoToTurQueryTenantUserResList(
      List<Map> turTenantUserDto);

  default Page<TurQueryTenantUserRes> turTenantUserDtoToTurQueryTenantUserResPage(
      Page<Map> turTenantUserDtos) {
    return new PageImpl<>(
        turTenantUserDtoToTurQueryTenantUserResList(turTenantUserDtos.getContent()),
        turTenantUserDtos.getPageable(),
        turTenantUserDtos.getTotalElements());
  }
}
