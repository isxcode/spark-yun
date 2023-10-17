package com.isxcode.star.modules.work.mapper;

import com.isxcode.star.api.work.pojos.dto.SyncWorkConfig;
import com.isxcode.star.api.work.pojos.res.GetSyncWorkConfigRes;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkConfigMapper {

	GetSyncWorkConfigRes syncWorkConfigToGetSyncWorkConfigRes(SyncWorkConfig syncWorkConfig);
}
