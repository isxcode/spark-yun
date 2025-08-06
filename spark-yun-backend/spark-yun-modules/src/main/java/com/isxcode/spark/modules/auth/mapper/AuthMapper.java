package com.isxcode.spark.modules.auth.mapper;

import com.isxcode.spark.api.auth.req.AddSsoAuthReq;
import com.isxcode.spark.api.auth.req.UpdateSsoAuthReq;
import com.isxcode.spark.api.auth.res.PageSsoAuthRes;
import com.isxcode.spark.modules.auth.entity.AuthEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthEntity addSsoAuthReqToAuthEntity(AddSsoAuthReq addSsoAuthReq);

    @Mapping(target = "name", source = "updateSsoAuthReq.name")
    @Mapping(target = "clientId", source = "updateSsoAuthReq.clientId")
    @Mapping(target = "clientSecret", source = "updateSsoAuthReq.clientSecret")
    @Mapping(target = "scope", source = "updateSsoAuthReq.scope")
    @Mapping(target = "authUrl", source = "updateSsoAuthReq.authUrl")
    @Mapping(target = "accessTokenUrl", source = "updateSsoAuthReq.accessTokenUrl")
    @Mapping(target = "redirectUrl", source = "updateSsoAuthReq.redirectUrl")
    @Mapping(target = "userUrl", source = "updateSsoAuthReq.userUrl")
    @Mapping(target = "authJsonPath", source = "updateSsoAuthReq.authJsonPath")
    @Mapping(target = "remark", source = "updateSsoAuthReq.remark")
    @Mapping(target = "id", source = "authEntity.id")
    AuthEntity updateSsoAuthReqToAuthEntity(UpdateSsoAuthReq updateSsoAuthReq, AuthEntity authEntity);

    PageSsoAuthRes AuthEntityToPageSsoAuthRes(AuthEntity authEntity);
}
