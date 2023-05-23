package com.isxcode.star.backend.module.user;

import com.isxcode.star.api.constants.user.RoleType;
import com.isxcode.star.api.constants.user.UserStatus;
import com.isxcode.star.api.pojos.user.req.UsrAddUserReq;
import com.isxcode.star.api.pojos.user.req.UsrUpdateUserReq;
import com.isxcode.star.api.pojos.user.res.UsrQueryAllEnableUsersRes;
import com.isxcode.star.api.pojos.user.res.UsrQueryAllUsersRes;
import com.isxcode.star.backend.module.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "status", constant = UserStatus.ENABLE)
  @Mapping(target = "roleCode", constant = RoleType.NORMAL_MEMBER)
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
