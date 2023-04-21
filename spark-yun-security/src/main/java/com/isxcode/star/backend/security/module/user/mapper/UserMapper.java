package com.isxcode.star.backend.security.module.user.mapper;

import com.isxcode.star.api.pojos.datasource.res.DasQueryDatasourceRes;
import com.isxcode.star.api.pojos.user.req.UsrAddUserReq;
import com.isxcode.star.api.pojos.user.req.UsrUpdateUserReq;
import com.isxcode.star.api.pojos.user.res.UsrQueryAllEnableUsersRes;
import com.isxcode.star.api.pojos.user.res.UsrQueryAllUsersRes;
import com.isxcode.star.backend.security.module.user.entity.UserEntity;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
  
  UserEntity usrAddUserReqToUserEntity(UsrAddUserReq usrAddUserReq);

  @Mapping(target = "passwd", source = "userEntity.passwd")
  @Mapping(target = "id", source = "userEntity.id")
  @Mapping(target = "remark", source = "usrUpdateUserReq.remark")
  @Mapping(target = "account", source = "usrUpdateUserReq.account")
  @Mapping(target = "username", source = "usrUpdateUserReq.username")
  @Mapping(target = "phone", source = "usrUpdateUserReq.phone")
  @Mapping(target = "email", source = "usrUpdateUserReq.email")
  UserEntity usrUpdateUserReqToUserEntity(UsrUpdateUserReq usrUpdateUserReq, UserEntity userEntity);

  /**
   * UsrQueryAllUsersRes.
   */
  @Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  UsrQueryAllUsersRes userEntityToUsrQueryAllUsersRes(UserEntity userEntity);

  List<UsrQueryAllUsersRes> userEntityToUsrQueryAllUsersResList(List<UserEntity> userEntity);

  default Page<UsrQueryAllUsersRes> userEntityToUsrQueryAllUsersResPage(Page<UserEntity> userEntities) {
    return new PageImpl<>(userEntityToUsrQueryAllUsersResList(userEntities.getContent()), userEntities.getPageable(), userEntities.getTotalElements());
  }

  /**
   * UsrQueryAllEnableUsersRes.
   */
  @Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  UsrQueryAllEnableUsersRes userEntityToUsrQueryAllEnableUsersRes(UserEntity userEntity);

  List<UsrQueryAllEnableUsersRes> userEntityToUsrQueryAllEnableUsersResList(List<UserEntity> userEntity);

  default Page<UsrQueryAllEnableUsersRes> userEntityToUsrQueryAllEnableUsersResPage(Page<UserEntity> userEntities) {
    return new PageImpl<>(userEntityToUsrQueryAllEnableUsersResList(userEntities.getContent()), userEntities.getPageable(), userEntities.getTotalElements());
  }
}
