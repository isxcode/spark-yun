package com.isxcode.star.modules.file.mapper;

import com.isxcode.star.api.file.pojos.res.PageFileRes;
import com.isxcode.star.modules.file.entity.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileMapper {

	PageFileRes fileEntityToPageFileRes(FileEntity fileEntity);
}
