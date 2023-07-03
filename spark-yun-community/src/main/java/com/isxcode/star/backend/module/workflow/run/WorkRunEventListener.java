package com.isxcode.star.backend.module.workflow.run;

import com.isxcode.star.api.constants.work.instance.InstanceStatus;
import com.isxcode.star.api.pojos.work.dto.WorkRunContext;
import com.isxcode.star.backend.module.work.WorkBizService;
import com.isxcode.star.backend.module.work.WorkEntity;
import com.isxcode.star.backend.module.work.WorkRepository;
import com.isxcode.star.backend.module.work.config.WorkConfigEntity;
import com.isxcode.star.backend.module.work.config.WorkConfigRepository;
import com.isxcode.star.backend.module.work.instance.WorkInstanceEntity;
import com.isxcode.star.backend.module.work.instance.WorkInstanceRepository;
import com.isxcode.star.backend.module.work.run.WorkExecutor;
import com.isxcode.star.backend.module.work.run.WorkExecutorFactory;
import com.isxcode.star.backend.module.workflow.instance.WorkflowInstanceEntity;
import com.isxcode.star.backend.module.workflow.instance.WorkflowInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

@Component
@RequiredArgsConstructor
public class WorkRunEventListener {

  private final ApplicationEventPublisher eventPublisher;

  private final WorkExecutorFactory workExecutorFactory;

  private final WorkBizService workBizService;

  private final WorkInstanceRepository workInstanceRepository;

  private final WorkflowInstanceRepository workflowInstanceRepository;

  private final WorkRepository workRepository;

  private final WorkConfigRepository workConfigRepository;

  @EventListener
  @Async("springEventThreadPool")
  public void onApplicationEvent(WorkRunEvent event) {

    USER_ID.set(event.getUserId());
    TENANT_ID.set(event.getTenantId());

    // 运行结束的状态集合
    List<String> runOverStatus = Arrays.asList(InstanceStatus.FAIL, InstanceStatus.SUCCESS);

    // 修改状态前都要加锁，给实例中的作业加, 首先修改作业实例的状态，第一时间加锁
    synchronized (event.getWorkId()) {

      // 查询作业实例
      WorkInstanceEntity workInstance = workInstanceRepository.findByWorkIdAndWorkflowInstanceId(event.getWorkId(), event.getFlowInstanceId());

      // 跑过了，不可以再跑
      if (runOverStatus.contains(workInstance.getStatus())) {
        return;
      }

      // 判断父级是否可以执行
      List<String> parentNodes = WorkflowUtils.getParentNodes(event.getNodeMapping(), event.getWorkId());
      List<WorkInstanceEntity> parentNodeList = workInstanceRepository.findAllByWorkIdAndWorkflowInstanceId(parentNodes, event.getFlowInstanceId());
      boolean flowIsError = parentNodeList.stream().anyMatch(e -> InstanceStatus.FAIL.equals(e.getStatus()));

      // 如果父级有错，则状态直接变更为失败
      if (flowIsError) {
        workInstance.setStatus(InstanceStatus.FAIL);
        workInstance.setSubmitLog("父级执行失败");
        workInstance.setExecEndDateTime(new Date());
      } else {
        workInstance.setStatus(InstanceStatus.RUNNING);
      }
      workInstanceRepository.saveAndFlush(workInstance);
    }

    // 如果状态为运行中，则可以开始运行作业
    WorkInstanceEntity workInstance = workInstanceRepository.findByWorkIdAndWorkflowInstanceId(event.getWorkId(), event.getFlowInstanceId());
    if (InstanceStatus.RUNNING.equals(workInstance.getStatus())) {

      WorkEntity work = workRepository.findById(event.getWorkId()).get();

      WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();

      // 封装workRunContext
      WorkRunContext workRunContext = workBizService.genWorkRunContext(workInstance.getId(), work, workConfig);

      // 同步执行作业
      WorkExecutor workExecutor = workExecutorFactory.create(work.getWorkType());
      workExecutor.syncExecute(workRunContext);
    }

    // 判断工作流是否执行完毕，检查结束节点是否都运行完
    synchronized (event.getFlowInstanceId()) {
      List<String> endNodes = WorkflowUtils.getEndNodes(event.getNodeMapping(), event.getNodeList());
      List<WorkInstanceEntity> endNodeInstance = workInstanceRepository.findAllByWorkIdAndWorkflowInstanceId(endNodes, event.getFlowInstanceId());
      boolean flowIsOver = endNodeInstance.stream().anyMatch(e -> runOverStatus.contains(e.getStatus()));
      if (flowIsOver) {
        boolean flowIsError = endNodeInstance.stream().anyMatch(e -> InstanceStatus.FAIL.equals(e.getStatus()));
        WorkflowInstanceEntity workflowInstance = workflowInstanceRepository.findById(event.getFlowInstanceId()).get();
        workflowInstance.setStatus(flowIsError ? InstanceStatus.FAIL : InstanceStatus.SUCCESS);
        workflowInstance.setExecEndDateTime(new Date());
        workflowInstanceRepository.save(workflowInstance);
        return;
      }
    }

    // 执行完后推送子节点
    List<String> sonNodes = WorkflowUtils.getSonNodes(event.getNodeMapping(), event.getWorkId());
    sonNodes.forEach(e -> {
      WorkRunEvent metaEvent = new WorkRunEvent(event.getFlowInstanceId(), e, event.getNodeMapping(), event.getNodeList(), event.getDagStartList(), event.getDagEndList(), event.getUserId(), event.getTenantId());
      eventPublisher.publishEvent(metaEvent);
    });
  }
}
