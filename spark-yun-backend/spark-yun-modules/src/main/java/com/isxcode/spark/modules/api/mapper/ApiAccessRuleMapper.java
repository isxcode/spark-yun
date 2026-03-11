package com.isxcode.spark.modules.api.mapper;

import com.isxcode.spark.api.api.req.AddAccessRuleReq;
import com.isxcode.spark.api.api.req.UpdateAccessRuleReq;
import com.isxcode.spark.api.api.res.PageAccessRuleRes;
import com.isxcode.spark.modules.api.entity.ApiAccessRuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApiAccessRuleMapper {

    ApiAccessRuleEntity addAccessRuleReqToApiAccessRuleEntity(AddAccessRuleReq addAccessRuleReq);

    @Mapping(target = "name", source = "updateAccessRuleReq.name")
    @Mapping(target = "ruleType", source = "updateAccessRuleReq.ruleType")
    @Mapping(target = "ipAddress", source = "updateAccessRuleReq.ipAddress")
    @Mapping(target = "remark", source = "updateAccessRuleReq.remark")
    @Mapping(target = "id", source = "apiAccessRuleEntity.id")
    ApiAccessRuleEntity updateAccessRuleReqToApiAccessRuleEntity(UpdateAccessRuleReq updateAccessRuleReq,
        ApiAccessRuleEntity apiAccessRuleEntity);

    PageAccessRuleRes apiAccessRuleEntityToPageAccessRuleRes(ApiAccessRuleEntity apiAccessRuleEntity);
}
