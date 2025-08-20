package com.isxcode.spark.modules.secret.mapper;

import com.isxcode.spark.api.secret.req.AddSecretReq;
import com.isxcode.spark.api.secret.req.UpdateSecretReq;
import com.isxcode.spark.api.secret.res.PageSecretRes;
import com.isxcode.spark.modules.secret.entity.SecretKeyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SecretMapper {

    SecretKeyEntity addSecretReqToSecretKeyEntity(AddSecretReq addSecretReq);

    @Mapping(target = "keyName", source = "updateSecretReq.keyName")
    @Mapping(target = "secretValue", source = "updateSecretReq.secretValue")
    @Mapping(target = "remark", source = "updateSecretReq.remark")
    @Mapping(target = "id", source = "secretKeyEntity.id")
    SecretKeyEntity updateSecretReqToSecretKeyEntity(UpdateSecretReq updateSecretReq, SecretKeyEntity secretKeyEntity);

    PageSecretRes secretKeyEntityToSecretPageRes(SecretKeyEntity secretKeyEntity);
}
