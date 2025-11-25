package com.isxcode.spark.modules.file.mapper;

import com.isxcode.spark.api.file.req.AddLibPackageReq;
import com.isxcode.spark.api.file.req.UpdateLibPackageReq;
import com.isxcode.spark.api.file.res.*;
import com.isxcode.spark.modules.file.entity.FileEntity;
import com.isxcode.spark.modules.file.entity.LibPackageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileMapper {

    PageFileRes fileEntityToPageFileRes(FileEntity fileEntity);

    LibPackageEntity addLibPackageReqToLibPackageEntity(AddLibPackageReq addLibPackageReq);

    AddLibPackageRes libPackageEntityToAddLibPackageRes(LibPackageEntity libPackageEntity);

    @Mapping(target = "id", source = "libPackageEntity.id")
    @Mapping(target = "name", source = "updateLibPackageReq.name")
    @Mapping(target = "remark", source = "updateLibPackageReq.remark")
    LibPackageEntity updateLibPackageReqToLibPackageEntity(LibPackageEntity libPackageEntity,
        UpdateLibPackageReq updateLibPackageReq);

    UpdateLibPackageRes libPackageEntityToUpdateLibPackageRes(LibPackageEntity libPackageEntity);

    GetLibPackageRes libPackageEntityToGetLibPackageRes(LibPackageEntity libPackageEntity);

    PageLibPackageRes libPackageEntityToPageLibPackageRes(LibPackageEntity libPackageEntity);
}
