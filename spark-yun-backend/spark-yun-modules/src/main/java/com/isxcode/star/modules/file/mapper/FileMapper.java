package com.isxcode.star.modules.file.mapper;


import com.isxcode.star.api.file.pojos.res.AddFileRes;
import com.isxcode.star.api.file.pojos.res.FileListRes;
import com.isxcode.star.modules.file.entity.FileEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileMapper {

  AddFileRes fileEntityToAddFileRes(FileEntity fileEntity);

  List<FileListRes> fileEntityListToFileListResList(
      List<FileEntity> fileEntities);
}
