package com.isxcode.star.backend.module.workflow.mapper;

import com.isxcode.star.api.pojos.work.req.AddWorkReq;
import com.isxcode.star.api.pojos.work.req.ConfigWorkReq;
import com.isxcode.star.api.pojos.work.res.GetWorkRes;
import com.isxcode.star.api.pojos.work.res.QueryWorkRes;
import com.isxcode.star.api.pojos.work.res.RunWorkRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagExecuteWorkRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetDataRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetLogRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetStatusRes;
import com.isxcode.star.backend.module.workflow.entity.WorkConfigEntity;
import com.isxcode.star.backend.module.workflow.entity.WorkEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface WorkMapper {

  @Mapping(source = "comment", target = "commentInfo")
  WorkEntity addWorkReqToWorkEntity(AddWorkReq addWorkReq);

  WorkConfigEntity configWorkReqToWorkConfigEntity(ConfigWorkReq configWorkReq);

  @Mapping(target = "comment", source = "commentInfo")
  @Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  QueryWorkRes workEntityToQueryWorkRes(WorkEntity workEntity);

  default List<QueryWorkRes> workEntityListToQueryWorkResList(List<WorkEntity> workEntities) {

    return workEntities.stream().map(this::workEntityToQueryWorkRes).collect(Collectors.toList());
  }

  @Mapping(target = "workflowId", source = "workEntity.workflowId")
  GetWorkRes workEntityAndWorkConfigEntityToGetWorkRes(
      WorkEntity workEntity, WorkConfigEntity workConfigEntity);

  RunWorkRes executeResToRunWorkRes(YagExecuteWorkRes executeRes);

  RunWorkRes getLogResToRunWorkRes(YagGetLogRes getLogRes);

  RunWorkRes getDataResToRunWorkRes(YagGetDataRes getDataRes);

  RunWorkRes getStatusToRunWorkRes(YagGetStatusRes getStatusRes);
}
