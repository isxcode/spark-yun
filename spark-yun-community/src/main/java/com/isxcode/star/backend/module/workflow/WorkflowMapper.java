package com.isxcode.star.backend.module.workflow;

import com.isxcode.star.api.pojos.workflow.dto.WorkInstanceInfo;
import com.isxcode.star.api.pojos.workflow.req.WofAddWorkflowReq;
import com.isxcode.star.api.pojos.workflow.req.WofUpdateWorkflowReq;
import com.isxcode.star.api.pojos.workflow.res.WofQueryWorkflowRes;
import java.util.List;

import com.isxcode.star.backend.module.work.instance.WorkInstanceEntity;
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
  WorkInstanceInfo workInstanceEntityToWorkInstanceInfo(WorkInstanceEntity workInstances);

  List<WorkInstanceInfo> workInstanceEntityListToWorkInstanceInfoList(List<WorkInstanceEntity> workInstances);
}
