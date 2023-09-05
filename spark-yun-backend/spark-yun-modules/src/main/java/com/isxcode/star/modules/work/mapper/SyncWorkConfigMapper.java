package com.isxcode.star.modules.work.mapper;

import com.isxcode.star.api.work.pojos.req.SaveSyncWorkConfigReq;
import com.isxcode.star.modules.work.entity.SyncWorkConfigEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SyncWorkConfigMapper {
  SyncWorkConfigEntity saveSyncWorkConfigReqToSyncWorkConfigEntity(SaveSyncWorkConfigReq saveSyncWorkConfigReq);
}
