package com.isxcode.star.modules.user.mapper;

import com.isxcode.star.api.user.dto.UserInfo;
import com.isxcode.star.api.user.req.AddUserReq;
import com.isxcode.star.api.user.req.UpdateUserReq;
import com.isxcode.star.api.user.req.UpdateUserInfoReq;
import com.isxcode.star.api.user.res.PageEnableUserRes;
import com.isxcode.star.api.user.res.PageUserRes;
import com.isxcode.star.security.user.UserEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity addUserReqToUserEntity(AddUserReq addUserReq);

    @Mapping(target = "passwd", source = "userEntity.passwd")
    @Mapping(target = "id", source = "userEntity.id")
    @Mapping(target = "remark", source = "updateUserReq.remark")
    @Mapping(target = "account", source = "updateUserReq.account")
    @Mapping(target = "username", source = "updateUserReq.username")
    @Mapping(target = "phone", source = "updateUserReq.phone")
    @Mapping(target = "email", source = "updateUserReq.email")
    UserEntity updateUserReqToUserEntity(UpdateUserReq updateUserReq, UserEntity userEntity);

    @Mapping(target = "passwd", source = "userEntity.passwd")
    @Mapping(target = "id", source = "userEntity.id")
    @Mapping(target = "username", source = "updateUserInfoReq.username")
    @Mapping(target = "phone", source = "updateUserInfoReq.phone")
    @Mapping(target = "email", source = "updateUserInfoReq.email")
    @Mapping(target = "remark", source = "updateUserInfoReq.remark")
    UserEntity updateUserInfoToUserEntity(UpdateUserInfoReq updateUserInfoReq, UserEntity userEntity);

    @Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    PageUserRes userEntityToUsrQueryAllUsersRes(UserEntity userEntity);

    @Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    PageEnableUserRes userEntityToUsrQueryAllEnableUsersRes(UserEntity userEntity);

    List<PageEnableUserRes> userEntityToUsrQueryAllEnableUsersResList(List<UserEntity> userEntity);

    default Page<PageEnableUserRes> userEntityToUsrQueryAllEnableUsersResPage(Page<UserEntity> userEntities) {
        return new PageImpl<>(userEntityToUsrQueryAllEnableUsersResList(userEntities.getContent()),
            userEntities.getPageable(), userEntities.getTotalElements());
    }

    List<UserInfo> userEntityToUserInfo(List<UserEntity> userEntity);
}
