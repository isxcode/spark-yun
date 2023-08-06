package com.isxcode.star.modules.work.mapper;

import com.isxcode.star.api.agent.pojos.res.YagGetStatusRes;
import com.isxcode.star.api.work.pojos.req.WocConfigWorkReq;
import com.isxcode.star.api.work.pojos.req.WokAddWorkReq;
import com.isxcode.star.api.work.pojos.req.WokUpdateWorkReq;
import com.isxcode.star.api.work.pojos.res.WokGetWorkRes;
import com.isxcode.star.api.work.pojos.res.WokQueryWorkRes;
import com.isxcode.star.api.work.pojos.res.WokRunWorkRes;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface WorkMapper {

  WorkEntity addWorkReqToWorkEntity(WokAddWorkReq addWorkReq);

  @Mapping(source = "wokUpdateWorkReq.remark", target = "remark")
  @Mapping(source = "wokUpdateWorkReq.name", target = "name")
  @Mapping(source = "workEntity.id", target = "id")
  @Mapping(source = "workEntity.workType", target = "workType")
  WorkEntity updateWorkReqToWorkEntity(WokUpdateWorkReq wokUpdateWorkReq, WorkEntity workEntity);

  WorkConfigEntity configWorkReqToWorkConfigEntity(WocConfigWorkReq configWorkReq);

  @Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  WokQueryWorkRes workEntityToQueryWorkRes(WorkEntity workEntity);

  List<WokQueryWorkRes> workEntityListToQueryWorkResList(List<WorkEntity> workEntities);

  default Page<WokQueryWorkRes> workEntityListToQueryWorkResList(Page<WorkEntity> workEntities) {
    List<WokQueryWorkRes> dtoList = workEntityListToQueryWorkResList(workEntities.getContent());
    return new PageImpl<>(dtoList, workEntities.getPageable(), workEntities.getTotalElements());
  }

  @Mapping(target = "clusterId", source = "workConfigEntity.clusterId")
  @Mapping(target = "datasourceId", source = "workConfigEntity.datasourceId")
  @Mapping(target = "workflowId", source = "workEntity.workflowId")
  @Mapping(target = "workId", source = "workEntity.id")
  WokGetWorkRes workEntityAndWorkConfigEntityToGetWorkRes(
      WorkEntity workEntity, WorkConfigEntity workConfigEntity);

  WokRunWorkRes getStatusToRunWorkRes(YagGetStatusRes getStatusRes);
}
