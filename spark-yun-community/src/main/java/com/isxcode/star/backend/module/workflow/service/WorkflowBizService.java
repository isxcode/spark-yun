package com.isxcode.star.backend.module.workflow.service;

import com.isxcode.star.api.constants.WorkflowStatus;
import com.isxcode.star.api.pojos.workflow.req.WocQueryWorkflowReq;
import com.isxcode.star.api.pojos.workflow.req.WofAddWorkflowReq;
import com.isxcode.star.api.pojos.workflow.req.WofUpdateWorkflowReq;
import com.isxcode.star.api.pojos.workflow.res.WofQueryWorkflowRes;
import com.isxcode.star.backend.module.workflow.entity.WorkflowEntity;
import com.isxcode.star.backend.module.workflow.mapper.WorkflowMapper;
import com.isxcode.star.backend.module.workflow.repository.WorkflowRepository;

import java.util.Optional;
import javax.transaction.Transactional;

import com.isxcode.star.api.exception.SparkYunException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
public class WorkflowBizService {

  private final WorkflowRepository workflowRepository;

  private final WorkflowMapper workflowMapper;

  public void addWorkflow(WofAddWorkflowReq wofAddWorkflowReq) {

    WorkflowEntity workflow = workflowMapper.addWorkflowReqToWorkflowEntity(wofAddWorkflowReq);
    workflow.setStatus(WorkflowStatus.UN_AUTO);

    workflowRepository.save(workflow);
  }

  public void updateWorkflow(WofUpdateWorkflowReq wofUpdateWorkflowReq) {

    Optional<WorkflowEntity> workflowEntityOptional = workflowRepository.findById(wofUpdateWorkflowReq.getId());
    if (!workflowEntityOptional.isPresent()) {
      throw new SparkYunException("作业流不存在");
    }

    WorkflowEntity workflow = workflowMapper.updateWorkflowReqToWorkflowEntity(wofUpdateWorkflowReq, workflowEntityOptional.get());

    workflowRepository.save(workflow);
  }

  public Page<WofQueryWorkflowRes> queryWorkflow(WocQueryWorkflowReq wocQueryWorkflowReq) {

    Page<WorkflowEntity> WorkflowEntityPage = workflowRepository.searchAll(wocQueryWorkflowReq.getSearchKeyWord(), PageRequest.of(wocQueryWorkflowReq.getPage(), wocQueryWorkflowReq.getPageSize()));

    return workflowMapper.workflowEntityPageToQueryWorkflowResPage(WorkflowEntityPage);
  }

  public void delWorkflow(String workflowId) {

    workflowRepository.deleteById(workflowId);
  }
}
