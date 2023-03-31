package com.isxcode.star.backend.module.workflow.service;

import com.isxcode.star.api.pojos.engine.res.QueryEngineRes;
import com.isxcode.star.api.pojos.work.req.AddWorkReq;
import com.isxcode.star.api.pojos.workflow.req.AddWorkflowReq;
import com.isxcode.star.api.pojos.workflow.res.QueryWorkflowRes;
import com.isxcode.star.backend.module.workflow.entity.WorkflowEntity;
import com.isxcode.star.backend.module.workflow.mapper.WorkflowMapper;
import com.isxcode.star.backend.module.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
public class WorkflowBizService {

  private final WorkflowRepository workflowRepository;

  private final WorkflowMapper workflowMapper;

  public void addWorkflow(AddWorkflowReq addWorkflowReq) {

    // req 转 entity
    WorkflowEntity workflow = workflowMapper.addWorkflowReqToWorkflowEntity(addWorkflowReq);
    workflow.setStatus("未调度");

    // 数据持久化
    workflowRepository.save(workflow);
  }

  public List<QueryWorkflowRes> queryWorkflow() {

    List<WorkflowEntity> workflowEntities = workflowRepository.findAll();

    List<QueryWorkflowRes> queryWorkflowRes = workflowMapper.workflowEntityListToQueryWorkflowResList(workflowEntities);

    queryWorkflowRes.forEach(e -> {

      e.setWorkNum(0);
    });

    return queryWorkflowRes;
  }

  public void delWorkflow(String workflowId) {

    workflowRepository.deleteById(workflowId);
  }
}
