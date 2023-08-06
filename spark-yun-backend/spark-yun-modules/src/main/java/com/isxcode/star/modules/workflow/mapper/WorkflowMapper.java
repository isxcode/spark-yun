package com.isxcode.star.modules.workflow.mapper;

import com.isxcode.star.api.workflow.pojos.dto.WorkInstanceInfo;
import com.isxcode.star.api.workflow.pojos.req.WofAddWorkflowReq;
import com.isxcode.star.api.workflow.pojos.req.WofUpdateWorkflowReq;
import com.isxcode.star.api.workflow.pojos.res.WofQueryWorkflowRes;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.workflow.entity.WorkflowEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface WorkflowMapper {

  WorkflowEntity addWorkflowReqToWorkflowEntity(WofAddWorkflowReq addWorkflowReq);

  @Mapping(source = "wofUpdateWorkflowReq.name", target = "name")
  @Mapping(source = "workflowEntity.id", target = "id")
  @Mapping(source = "wofUpdateWorkflowReq.remark", target = "remark")
  WorkflowEntity updateWorkflowReqToWorkflowEntity(
      WofUpdateWorkflowReq wofUpdateWorkflowReq, WorkflowEntity workflowEntity);

  WofQueryWorkflowRes workflowEntityToQueryWorkflowRes(WorkflowEntity workflowEntity);

  List<WofQueryWorkflowRes> workflowEntityListToQueryWorkflowResList(
      List<WorkflowEntity> workflowEntities);

  default Page<WofQueryWorkflowRes> workflowEntityPageToQueryWorkflowResPage(
      Page<WorkflowEntity> workflowEntityPage) {
    List<WofQueryWorkflowRes> dtoList =
        workflowEntityListToQueryWorkflowResList(workflowEntityPage.getContent());
    return new PageImpl<>(
        dtoList, workflowEntityPage.getPageable(), workflowEntityPage.getTotalElements());
  }

  @Mapping(source = "status", target = "runStatus")
  @Mapping(source = "id", target = "workInstanceId")
  WorkInstanceInfo workInstanceEntityToWorkInstanceInfo(WorkInstanceEntity workInstances);

  List<WorkInstanceInfo> workInstanceEntityListToWorkInstanceInfoList(
      List<WorkInstanceEntity> workInstances);
}
