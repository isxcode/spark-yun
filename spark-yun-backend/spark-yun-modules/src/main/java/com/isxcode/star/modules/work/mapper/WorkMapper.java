package com.isxcode.star.modules.work.mapper;

import com.isxcode.star.api.agent.pojos.res.YagGetStatusRes;
import com.isxcode.star.api.work.pojos.req.AddWorkReq;
import com.isxcode.star.api.work.pojos.req.ConfigWorkReq;
import com.isxcode.star.api.work.pojos.req.UpdateWorkReq;
import com.isxcode.star.api.work.pojos.res.GetWorkRes;
import com.isxcode.star.api.work.pojos.res.PageWorkRes;
import com.isxcode.star.api.work.pojos.res.RunWorkRes;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface WorkMapper {

  WorkEntity addWorkReqToWorkEntity(AddWorkReq addWorkReq);

  @Mapping(source = "wokUpdateWorkReq.remark", target = "remark")
  @Mapping(source = "wokUpdateWorkReq.name", target = "name")
  @Mapping(source = "workEntity.id", target = "id")
  @Mapping(source = "workEntity.workType", target = "workType")
  WorkEntity updateWorkReqToWorkEntity(UpdateWorkReq wokUpdateWorkReq, WorkEntity workEntity);

  WorkConfigEntity configWorkReqToWorkConfigEntity(ConfigWorkReq configWorkReq);

  @Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  PageWorkRes workEntityToPageWorkRes(WorkEntity workEntity);

  @Mapping(target = "clusterId", source = "workConfigEntity.clusterId")
  @Mapping(target = "datasourceId", source = "workConfigEntity.datasourceId")
  @Mapping(target = "workflowId", source = "workEntity.workflowId")
  @Mapping(target = "workId", source = "workEntity.id")
  GetWorkRes workEntityAndWorkConfigEntityToGetWorkRes(
      WorkEntity workEntity, WorkConfigEntity workConfigEntity);

  RunWorkRes getStatusToRunWorkRes(YagGetStatusRes getStatusRes);
}
