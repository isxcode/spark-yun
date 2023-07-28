package com.isxcode.star.backend.module.work.file;

import com.isxcode.star.api.pojos.work.file.res.WofAddFileRes;
import com.isxcode.star.api.pojos.work.file.res.WofFileListRes;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkFileMapper {

  WofAddFileRes workFileEntityToWofAddFileRes(WorkFileEntity workFileEntity);

  List<WofFileListRes> workFileEntityListToWofFileListResList(
      List<WorkFileEntity> workFileEntities);
}
