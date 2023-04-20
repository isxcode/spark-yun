package com.isxcode.star.backend.security.module.user.mapper;

import com.isxcode.star.api.pojos.tenant.req.TetAddUserReq;
import com.isxcode.star.backend.security.module.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "passwd", source = "password")
  @Mapping(target = "commentInfo", source = "comment")
  UserEntity tetAddUserReqToUserEntity(TetAddUserReq tetAddUserReq);

}
