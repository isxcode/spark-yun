package com.isxcode.spark.modules.work.mapper;

import com.isxcode.spark.api.work.dto.SyncWorkConfig;
import com.isxcode.spark.api.work.res.GetSyncWorkConfigRes;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkConfigMapper {

    GetSyncWorkConfigRes syncWorkConfigToGetSyncWorkConfigRes(SyncWorkConfig syncWorkConfig);
}
