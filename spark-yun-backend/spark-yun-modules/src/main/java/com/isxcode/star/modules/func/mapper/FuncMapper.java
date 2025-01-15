package com.isxcode.star.modules.func.mapper;

import com.isxcode.star.api.func.dto.FuncInfo;
import com.isxcode.star.api.func.req.AddFuncReq;
import com.isxcode.star.api.func.req.UpdateFuncReq;
import com.isxcode.star.api.func.res.PageFuncRes;
import com.isxcode.star.modules.func.entity.FuncEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FuncMapper {

    FuncEntity addFuncReqToFuncEntity(AddFuncReq addFuncReq);

    @Mapping(source = "updateFuncReq.type", target = "type")
    @Mapping(source = "updateFuncReq.funcName", target = "funcName")
    @Mapping(source = "updateFuncReq.className", target = "className")
    @Mapping(source = "updateFuncReq.resultType", target = "resultType")
    @Mapping(source = "updateFuncReq.id", target = "id")
    @Mapping(source = "updateFuncReq.remark", target = "remark")
    @Mapping(source = "updateFuncReq.fileId", target = "fileId")
    FuncEntity updateFuncReqToFuncEntity(UpdateFuncReq updateFuncReq, FuncEntity udfEntity);

    PageFuncRes funcEntityToPageFuncRes(FuncEntity funcEntity);

    List<FuncInfo> funcEntityListToFuncInfoList(List<FuncEntity> funcEntities);
}
