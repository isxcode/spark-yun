package com.isxcode.star.backend.security.module.user.mapper;

import com.isxcode.star.api.pojos.tenant.req.TetAddUserReq;
import com.isxcode.star.api.pojos.tenant.req.TetUpdateUserReq;
import com.isxcode.star.backend.security.module.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "passwd", source = "password")
  @Mapping(target = "commentInfo", source = "comment")
  UserEntity tetAddUserReqToUserEntity(TetAddUserReq tetAddUserReq);

  @Mapping(target = "passwd", source = "tetUpdateUserReq.password")
  @Mapping(target = "commentInfo", source = "tetUpdateUserReq.comment")
  @Mapping(target = "account", source = "tetUpdateUserReq.account")
  @Mapping(target = "id", source = "userEntity.id")
  @Mapping(target = "username", source = "tetUpdateUserReq.username")
  @Mapping(target = "phone", source = "tetUpdateUserReq.phone")
  @Mapping(target = "email", source = "tetUpdateUserReq.email")
  UserEntity tetUpdateUserReqToUserEntity(TetUpdateUserReq tetUpdateUserReq, UserEntity userEntity);

}
