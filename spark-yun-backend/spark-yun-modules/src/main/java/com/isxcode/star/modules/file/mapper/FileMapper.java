package com.isxcode.star.modules.file.mapper;

import com.isxcode.star.api.file.res.PageFileRes;
import com.isxcode.star.modules.file.entity.FileEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {

    PageFileRes fileEntityToPageFileRes(FileEntity fileEntity);
}
