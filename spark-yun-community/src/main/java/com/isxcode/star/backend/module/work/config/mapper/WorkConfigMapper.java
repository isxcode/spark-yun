package com.isxcode.star.backend.module.work.config.mapper;

import com.isxcode.star.api.pojos.work.config.req.WocConfigWorkReq;
import com.isxcode.star.backend.module.work.config.entity.WorkConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkConfigMapper {

  WorkConfigEntity wocConfigWorkReqToWorkConfigEntity(WocConfigWorkReq wocConfigWorkReq);
}
