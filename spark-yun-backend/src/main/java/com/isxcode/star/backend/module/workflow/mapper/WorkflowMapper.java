package com.isxcode.star.backend.module.workflow.mapper;

import com.isxcode.star.api.pojos.calculate.engine.res.CaeQueryEngineRes;
import com.isxcode.star.api.pojos.workflow.req.WofAddWorkflowReq;
import com.isxcode.star.api.pojos.workflow.res.WofQueryWorkflowRes;
import com.isxcode.star.backend.module.workflow.entity.WorkflowEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface WorkflowMapper {

  @Mapping(source = "comment", target = "commentInfo")
  WorkflowEntity addWorkflowReqToWorkflowEntity(WofAddWorkflowReq addWorkflowReq);

  @Mapping(target = "comment", source = "commentInfo")
  WofQueryWorkflowRes workflowEntityToQueryWorkflowRes(WorkflowEntity workflowEntity);

  List<WofQueryWorkflowRes> workflowEntityListToQueryWorkflowResList(List<WorkflowEntity> workflowEntities);

  default Page<WofQueryWorkflowRes> workflowEntityPageToQueryWorkflowResPage(Page<WorkflowEntity> workflowEntityPage){
    List<WofQueryWorkflowRes> dtoList =
      workflowEntityListToQueryWorkflowResList(workflowEntityPage.getContent());
    return new PageImpl<>(
      dtoList, workflowEntityPage.getPageable(), workflowEntityPage.getTotalElements());
  }
}
