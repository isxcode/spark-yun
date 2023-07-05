package com.isxcode.star.backend.module.workflow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.isxcode.star.api.constants.work.instance.InstanceStatus;
import com.isxcode.star.api.constants.work.instance.InstanceType;
import com.isxcode.star.api.constants.workflow.WorkflowStatus;
import com.isxcode.star.api.constants.workflow.instance.FlowInstanceStatus;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.pojos.workflow.dto.WorkInstanceInfo;
import com.isxcode.star.api.pojos.workflow.req.WocQueryWorkflowReq;
import com.isxcode.star.api.pojos.workflow.req.WofAddWorkflowReq;
import com.isxcode.star.api.pojos.workflow.req.WofUpdateWorkflowReq;
import com.isxcode.star.api.pojos.workflow.res.WofQueryRunWorkInstancesRes;
import com.isxcode.star.api.pojos.workflow.res.WofQueryWorkflowRes;
import com.isxcode.star.backend.module.work.instance.WorkInstanceEntity;
import com.isxcode.star.backend.module.work.instance.WorkInstanceRepository;
import com.isxcode.star.backend.module.workflow.config.WorkflowConfigEntity;
import com.isxcode.star.backend.module.workflow.config.WorkflowConfigRepository;
import com.isxcode.star.backend.module.workflow.instance.WorkflowInstanceEntity;
import com.isxcode.star.backend.module.workflow.instance.WorkflowInstanceRepository;
import com.isxcode.star.backend.module.workflow.run.WorkflowRunEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

/**
 * 用户模块接口的业务逻辑.
 */
@Service
@RequiredArgsConstructor
public class WorkflowBizService {

  private final WorkflowRepository workflowRepository;

  private final WorkflowMapper workflowMapper;

  private final WorkflowConfigRepository workflowConfigRepository;

  private final ApplicationEventPublisher eventPublisher;

  private final WorkflowInstanceRepository workflowInstanceRepository;

  private final WorkInstanceRepository workInstanceRepository;

  public WorkflowEntity getWorkflowEntity(String workflowId) {

    Optional<WorkflowEntity> workflowEntityOptional = workflowRepository.findById(workflowId);
    if (!workflowEntityOptional.isPresent()) {
      throw new SparkYunException("作业流不存在");
    }
    return workflowEntityOptional.get();
  }

  public void addWorkflow(WofAddWorkflowReq wofAddWorkflowReq) {

    // 初始化工作流配置
    WorkflowConfigEntity workflowConfig = new WorkflowConfigEntity();
    workflowConfig = workflowConfigRepository.save(workflowConfig);

    // 工作流绑定配置
    WorkflowEntity workflow = workflowMapper.addWorkflowReqToWorkflowEntity(wofAddWorkflowReq);
    workflow.setStatus(WorkflowStatus.UN_AUTO);
    workflow.setConfigId(workflowConfig.getId());
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

  /**
   * 运行工作流，返回工作流实例id
   */
  public String runFlow(String workflowId) {

    // 获取工作流配置id
    WorkflowEntity workflow = getWorkflowEntity(workflowId);

    // 获取作业配置
    WorkflowConfigEntity workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();

    // 创建工作流实例
    WorkflowInstanceEntity workflowInstance = WorkflowInstanceEntity.builder()
      .flowId(workflowId)
      .status(FlowInstanceStatus.RUNNING)
      .instanceType(InstanceType.MANUAL)
      .execStartDateTime(new Date()).build();
    workflowInstance = workflowInstanceRepository.saveAndFlush(workflowInstance);

    // 初始化所有节点的作业实例
    List<String> nodeList = JSON.parseArray(workflowConfig.getNodeList(), String.class);
    List<WorkInstanceEntity> workInstances = new ArrayList<>();
    for (String workId : nodeList) {
      WorkInstanceEntity metaInstance = WorkInstanceEntity.builder()
        .workId(workId)
        .instanceType(InstanceType.MANUAL)
        .status(InstanceStatus.PENDING)
        .workflowInstanceId(workflowInstance.getId())
        .build();

      workInstances.add(metaInstance);
    }
    workInstanceRepository.saveAllAndFlush(workInstances);

    // 获取startNode
    List<String> startNodes = JSON.parseArray(workflowConfig.getDagStartList(), String.class);
    List<String> endNodes = JSON.parseArray(workflowConfig.getDagEndList(), String.class);
    List<List<String>> nodeMapping = JSON.parseObject(workflowConfig.getNodeMapping(), new TypeReference<List<List<String>>>() {
    });

    // 封装event推送时间，开始执行任务
    // 异步触发工作流
    for (String workId : startNodes) {
      WorkflowRunEvent metaEvent = WorkflowRunEvent.builder()
        .workId(workId)
        .dagEndList(endNodes)
        .dagStartList(startNodes)
        .flowInstanceId(workflowInstance.getId())
        .nodeMapping(nodeMapping)
        .nodeList(nodeList)
        .tenantId(TENANT_ID.get())
        .userId(USER_ID.get())
        .build();
      eventPublisher.publishEvent(metaEvent);
    }

    return workflowInstance.getId();
  }

  public WofQueryRunWorkInstancesRes queryRunWorkInstances(String workflowInstanceId) {

    // 查询工作流实例
    Optional<WorkflowInstanceEntity> workflowInstanceEntityOptional = workflowInstanceRepository.findById(workflowInstanceId);
    if (!workflowInstanceEntityOptional.isPresent()) {
      throw new SparkYunException("工作流实例不存在");
    }
    WorkflowInstanceEntity workflowInstance = workflowInstanceEntityOptional.get();

    // 查询作业实例列表
    List<WorkInstanceEntity> workInstances = workInstanceRepository.findAllByWorkflowInstanceId(workflowInstanceId);
    List<WorkInstanceInfo> instanceInfos = workflowMapper.workInstanceEntityListToWorkInstanceInfoList(workInstances);

    // 封装返回对象
    return WofQueryRunWorkInstancesRes.builder().flowStatus(workflowInstance.getStatus()).workInstances(instanceInfos).build();
  }

}
