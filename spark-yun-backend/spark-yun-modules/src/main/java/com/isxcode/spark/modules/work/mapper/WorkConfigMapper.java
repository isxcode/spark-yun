package com.isxcode.spark.modules.work.mapper;

import com.isxcode.spark.api.work.dto.SyncWorkConfig;
import com.isxcode.spark.api.work.res.GetSyncWorkConfigRes;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkConfigMapper {

    GetSyncWorkConfigRes syncWorkConfigToGetSyncWorkConfigRes(SyncWorkConfig syncWorkConfig);
}
