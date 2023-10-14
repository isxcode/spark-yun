package com.isxcode.star.modules.user.mapper;

import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.api.user.constants.UserStatus;
import com.isxcode.star.api.user.pojos.req.AddUserReq;
import com.isxcode.star.api.user.pojos.req.UpdateUserReq;
import com.isxcode.star.api.user.pojos.req.UpdateUserInfoReq;
import com.isxcode.star.api.user.pojos.res.PageEnableUserRes;
import com.isxcode.star.api.user.pojos.res.PageUserRes;
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
	UserEntity usrAddUserReqToUserEntity(AddUserReq usrAddUserReq);

	@Mapping(target = "passwd", source = "userEntity.passwd")
	@Mapping(target = "id", source = "userEntity.id")
	@Mapping(target = "remark", source = "usrUpdateUserReq.remark")
	@Mapping(target = "account", source = "usrUpdateUserReq.account")
	@Mapping(target = "username", source = "usrUpdateUserReq.username")
	@Mapping(target = "phone", source = "usrUpdateUserReq.phone")
	@Mapping(target = "email", source = "usrUpdateUserReq.email")
	UserEntity usrUpdateUserReqToUserEntity(UpdateUserReq usrUpdateUserReq, UserEntity userEntity);

  @Mapping(target = "passwd", source = "userEntity.passwd")
  @Mapping(target = "id", source = "userEntity.id")
  @Mapping(target = "username", source = "updateUserInfoReq.username")
  @Mapping(target = "phone", source = "updateUserInfoReq.phone")
  @Mapping(target = "email", source = "updateUserInfoReq.email")
  @Mapping(target = "remark", source = "updateUserInfoReq.remark")
  UserEntity usrUpdateUserInfoToUserEntity(UpdateUserInfoReq updateUserInfoReq, UserEntity userEntity);

	/** UsrQueryAllUsersRes. */
	@Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
	PageUserRes userEntityToUsrQueryAllUsersRes(UserEntity userEntity);

	List<PageUserRes> userEntityToUsrQueryAllUsersResList(List<UserEntity> userEntity);

	default Page<PageUserRes> userEntityToUsrQueryAllUsersResPage(Page<UserEntity> userEntities) {
		return new PageImpl<>(userEntityToUsrQueryAllUsersResList(userEntities.getContent()),
				userEntities.getPageable(), userEntities.getTotalElements());
	}

	/** UsrQueryAllEnableUsersRes. */
	@Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
	PageEnableUserRes userEntityToUsrQueryAllEnableUsersRes(UserEntity userEntity);

	List<PageEnableUserRes> userEntityToUsrQueryAllEnableUsersResList(List<UserEntity> userEntity);

	default Page<PageEnableUserRes> userEntityToUsrQueryAllEnableUsersResPage(Page<UserEntity> userEntities) {
		return new PageImpl<>(userEntityToUsrQueryAllEnableUsersResList(userEntities.getContent()),
				userEntities.getPageable(), userEntities.getTotalElements());
	}
}
