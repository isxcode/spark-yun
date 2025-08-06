package com.isxcode.spark.modules.file.mapper;

import com.isxcode.spark.api.file.res.PageFileRes;
import com.isxcode.spark.modules.file.entity.FileEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {

    PageFileRes fileEntityToPageFileRes(FileEntity fileEntity);
}
