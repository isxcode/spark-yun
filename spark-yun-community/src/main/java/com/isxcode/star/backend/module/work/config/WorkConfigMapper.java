package com.isxcode.star.backend.module.work.config;

import com.isxcode.star.api.pojos.work.config.req.WocConfigWorkReq;
import com.isxcode.star.backend.module.work.config.WorkConfigEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkConfigMapper {

  WorkConfigEntity wocConfigWorkReqToWorkConfigEntity(WocConfigWorkReq wocConfigWorkReq);
}
