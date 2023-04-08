package com.isxcode.star.backend.module.work.mapper;

import com.isxcode.star.api.pojos.work.config.req.WocConfigWorkReq;
import com.isxcode.star.api.pojos.work.req.WokAddWorkReq;
import com.isxcode.star.api.pojos.work.res.WokGetWorkRes;
import com.isxcode.star.api.pojos.work.res.WokQueryWorkRes;
import com.isxcode.star.api.pojos.work.res.WokRunWorkRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetDataRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetLogRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetStatusRes;
import com.isxcode.star.backend.module.work.config.entity.WorkConfigEntity;
import com.isxcode.star.backend.module.work.entity.WorkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface WorkMapper {

  @Mapping(source = "comment", target = "commentInfo")
  WorkEntity addWorkReqToWorkEntity(WokAddWorkReq addWorkReq);

  WorkConfigEntity configWorkReqToWorkConfigEntity(WocConfigWorkReq configWorkReq);

  @Mapping(target = "comment", source = "commentInfo")
  @Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  WokQueryWorkRes workEntityToQueryWorkRes(WorkEntity workEntity);
  List<WokQueryWorkRes> workEntityListToQueryWorkResList(List<WorkEntity> workEntities);

  default Page<WokQueryWorkRes> workEntityListToQueryWorkResList(
    Page<WorkEntity> workEntities) {
    List<WokQueryWorkRes> dtoList = workEntityListToQueryWorkResList(workEntities.getContent());
    return new PageImpl<>(dtoList, workEntities.getPageable(), workEntities.getTotalElements());
  }

  @Mapping(target = "workflowId", source = "workEntity.workflowId")
  WokGetWorkRes workEntityAndWorkConfigEntityToGetWorkRes(
          WorkEntity workEntity, WorkConfigEntity workConfigEntity);

  WokRunWorkRes getStatusToRunWorkRes(YagGetStatusRes getStatusRes);
}
