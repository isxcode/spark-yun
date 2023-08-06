package com.isxcode.star.modules.user.mapper;

import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.api.user.constants.UserStatus;
import com.isxcode.star.api.user.pojos.req.UsrAddUserReq;
import com.isxcode.star.api.user.pojos.req.UsrUpdateUserReq;
import com.isxcode.star.api.user.pojos.res.UsrQueryAllEnableUsersRes;
import com.isxcode.star.api.user.pojos.res.UsrQueryAllUsersRes;
import com.isxcode.star.security.user.UserEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

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

  /** UsrQueryAllUsersRes. */
  @Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  UsrQueryAllUsersRes userEntityToUsrQueryAllUsersRes(UserEntity userEntity);

  List<UsrQueryAllUsersRes> userEntityToUsrQueryAllUsersResList(List<UserEntity> userEntity);

  default Page<UsrQueryAllUsersRes> userEntityToUsrQueryAllUsersResPage(
      Page<UserEntity> userEntities) {
    return new PageImpl<>(
        userEntityToUsrQueryAllUsersResList(userEntities.getContent()),
        userEntities.getPageable(),
        userEntities.getTotalElements());
  }

  /** UsrQueryAllEnableUsersRes. */
  @Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  UsrQueryAllEnableUsersRes userEntityToUsrQueryAllEnableUsersRes(UserEntity userEntity);

  List<UsrQueryAllEnableUsersRes> userEntityToUsrQueryAllEnableUsersResList(
      List<UserEntity> userEntity);

  default Page<UsrQueryAllEnableUsersRes> userEntityToUsrQueryAllEnableUsersResPage(
      Page<UserEntity> userEntities) {
    return new PageImpl<>(
        userEntityToUsrQueryAllEnableUsersResList(userEntities.getContent()),
        userEntities.getPageable(),
        userEntities.getTotalElements());
  }
}
