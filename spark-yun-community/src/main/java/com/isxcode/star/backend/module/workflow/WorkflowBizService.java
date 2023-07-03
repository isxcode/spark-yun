package com.isxcode.star.backend.module.workflow;

import com.isxcode.star.api.constants.workflow.WorkflowStatus;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.pojos.workflow.req.WocQueryWorkflowReq;
import com.isxcode.star.api.pojos.workflow.req.WofAddWorkflowReq;
import com.isxcode.star.api.pojos.workflow.req.WofUpdateWorkflowReq;
import com.isxcode.star.api.pojos.workflow.res.WofQueryWorkflowRes;
import java.util.Optional;
import javax.transaction.Transactional;
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

    Optional<WorkflowEntity> workflowEntityOptional =
        workflowRepository.findById(wofUpdateWorkflowReq.getId());
    if (!workflowEntityOptional.isPresent()) {
      throw new SparkYunException("作业流不存在");
    }

    WorkflowEntity workflow =
        workflowMapper.updateWorkflowReqToWorkflowEntity(
            wofUpdateWorkflowReq, workflowEntityOptional.get());

    workflowRepository.save(workflow);
  }

  public Page<WofQueryWorkflowRes> queryWorkflow(WocQueryWorkflowReq wocQueryWorkflowReq) {

    Page<WorkflowEntity> workflowEntityPage =
        workflowRepository.searchAll(
            wocQueryWorkflowReq.getSearchKeyWord(),
            PageRequest.of(wocQueryWorkflowReq.getPage(), wocQueryWorkflowReq.getPageSize()));

    return workflowMapper.workflowEntityPageToQueryWorkflowResPage(workflowEntityPage);
  }

  public void delWorkflow(String workflowId) {

    workflowRepository.deleteById(workflowId);
  }

  public void drawFlow() {

    // 解析前端传递的数据

    // 保存工作流的配置，解析成nodeIdList，startNodeList，endNodeList，nodeMapping

    // 解析完保存下来

  }

  public void runFlow() {

    // 获取工作流配置

    // 获取startnode

    // 生成实例

    // 封装event推送时间，开始执行任务
  }
}
