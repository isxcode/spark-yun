package com.isxcode.spark.modules.user.mapper;

import com.isxcode.spark.api.user.dto.UserInfo;
import com.isxcode.spark.api.user.req.AddUserReq;
import com.isxcode.spark.api.user.req.UpdateUserReq;
import com.isxcode.spark.api.user.req.UpdateUserInfoReq;
import com.isxcode.spark.api.user.res.PageEnableUserRes;
import com.isxcode.spark.api.user.res.PageUserRes;
import com.isxcode.spark.security.user.UserEntity;
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
    @Mapping(target = "validStartDateTime", source = "updateUserReq.validStartDateTime")
    @Mapping(target = "validEndDateTime", source = "updateUserReq.validEndDateTime")
    UserEntity updateUserReqToUserEntity(UpdateUserReq updateUserReq, UserEntity userEntity);

    @Mapping(target = "passwd", source = "userEntity.passwd")
    @Mapping(target = "id", source = "userEntity.id")
    @Mapping(target = "account", source = "userEntity.account")
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
