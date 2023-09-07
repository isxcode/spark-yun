package com.isxcode.star.modules.work.mapper;

import com.isxcode.star.api.work.pojos.req.SaveSyncWorkConfigReq;
import com.isxcode.star.api.work.pojos.res.GetSyncWorkConfigRes;
import com.isxcode.star.modules.work.entity.SyncWorkConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SyncWorkConfigMapper {
  @Mapping(target = "id", source = "syncWorkConfigEntity.id")
  @Mapping(target = "workId", source = "saveSyncWorkConfigReq.workId")
  @Mapping(target = "sourceDBType", source = "saveSyncWorkConfigReq.sourceDBType")
  @Mapping(target = "sourceDBId", source = "saveSyncWorkConfigReq.sourceDBId")
  @Mapping(target = "sourceTable", source = "saveSyncWorkConfigReq.sourceTable")
  @Mapping(target = "queryCondition", source = "saveSyncWorkConfigReq.queryCondition")
  @Mapping(target = "targetDBType", source = "saveSyncWorkConfigReq.targetDBType")
  @Mapping(target = "targetDBId", source = "saveSyncWorkConfigReq.targetDBId")
  @Mapping(target = "targetTable", source = "saveSyncWorkConfigReq.targetTable")
  @Mapping(target = "overMode", source = "saveSyncWorkConfigReq.overMode")
  @Mapping(target = "columMapping", expression="java(saveSyncWorkConfigReq.getColumMapping().toString())")
  SyncWorkConfigEntity saveSyncWorkConfigReqAndSyncWorkConfigEntityToSyncWorkConfigEntity(SaveSyncWorkConfigReq saveSyncWorkConfigReq,SyncWorkConfigEntity syncWorkConfigEntity);

  GetSyncWorkConfigRes syncWorkConfigEntityToGetSyncWorkConfigRes(SyncWorkConfigEntity syncWorkConfigEntity);

}
