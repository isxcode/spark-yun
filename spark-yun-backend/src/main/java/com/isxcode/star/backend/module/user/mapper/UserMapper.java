package com.isxcode.star.backend.module.user.mapper;

import com.isxcode.star.api.pojos.user.req.AddUserReq;
import com.isxcode.star.backend.module.user.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity addUserReqToUserEntity(AddUserReq addUserReq);
}
