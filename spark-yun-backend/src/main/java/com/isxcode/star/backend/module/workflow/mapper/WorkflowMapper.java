package com.isxcode.star.backend.module.workflow.mapper;

import com.isxcode.star.api.pojos.workflow.req.AddWorkflowReq;
import com.isxcode.star.api.pojos.workflow.res.QueryWorkflowRes;
import com.isxcode.star.backend.module.workflow.entity.WorkflowEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface WorkflowMapper {

  @Mapping(source = "comment", target = "commentInfo")
  WorkflowEntity addWorkflowReqToWorkflowEntity(AddWorkflowReq addWorkflowReq);

  @Mapping(target = "comment", source = "commentInfo")
  QueryWorkflowRes workflowEntityToQueryWorkflowRes(WorkflowEntity workflowEntity);

  default List<QueryWorkflowRes> workflowEntityListToQueryWorkflowResList(
      List<WorkflowEntity> workflowEntities) {

    return workflowEntities.stream()
        .map(this::workflowEntityToQueryWorkflowRes)
        .collect(Collectors.toList());
  }
}
