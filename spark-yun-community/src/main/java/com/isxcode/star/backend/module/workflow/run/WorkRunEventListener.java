package com.isxcode.star.backend.module.workflow.run;

import com.isxcode.star.api.constants.work.instance.InstanceStatus;
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
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.internal.util.Strings;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

@Slf4j
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

    WorkEntity work1 = workRepository.findById(event.getWorkId()).get();

    log.info("flowInstanceId:" + event.getFlowInstanceId() + "   接受推送:" + work1.getName() + "   时间:" + LocalDateTime.now());

    // 由于是异步，先初始化系统参数
    USER_ID.set(event.getUserId());
    TENANT_ID.set(event.getTenantId());

    // 修改状态前都要加锁，给实例中的作业加锁
    synchronized (this) {

      // 查询作业实例
      WorkInstanceEntity workInstance = workInstanceRepository.findByWorkIdAndWorkflowInstanceId(event.getWorkId(), event.getFlowInstanceId());

      // 跑过了或者正在跑，不可以再跑
      if (!InstanceStatus.PENDING.equals(workInstance.getStatus())) {
        return;
      }

      // 在自动调度中，如果自身定时没有被触发，则不接受事件推送
      if (!Strings.isEmpty(event.getVersionId()) && !workInstance.getQuartzHasRun()) {
        return;
      }

      // 判断父级是否可以执行
      List<String> parentNodes = WorkflowUtils.getParentNodes(event.getNodeMapping(), event.getWorkId());
      List<WorkInstanceEntity> parentInstances = workInstanceRepository.findAllByWorkIdAndWorkflowInstanceId(parentNodes, event.getFlowInstanceId());
      boolean flowIsError = parentInstances.stream().anyMatch(e -> InstanceStatus.FAIL.equals(e.getStatus()));
      boolean parentIsPending = parentInstances.stream().anyMatch(e -> InstanceStatus.PENDING.equals(e.getStatus()));

      if (flowIsError) {
        // 如果父级有错，则状态直接变更为失败
        workInstance.setStatus(InstanceStatus.FAIL);
        workInstance.setSubmitLog("父级执行失败");
        workInstance.setExecEndDateTime(new Date());
      } else if (parentIsPending) {
        // 如果父级在运行中，直接中断
        return;
      } else {
        workInstance.setStatus(InstanceStatus.RUNNING);
      }
      workInstanceRepository.saveAndFlush(workInstance);
    }

    // 再次查询作业实例，如果状态为运行中，则可以开始运行作业
    WorkInstanceEntity workInstance = workInstanceRepository.findByWorkIdAndWorkflowInstanceId(event.getWorkId(), event.getFlowInstanceId());
    if (InstanceStatus.RUNNING.equals(workInstance.getStatus())) {

      log.info("flowInstanceId:" + event.getFlowInstanceId() + "   开始执行:" + work1.getName() + "  时间:" + LocalDateTime.now());

      // 封装workRunContext
      WorkRunContext workRunContext;
      if (Strings.isEmpty(event.getVersionId())) {
        WorkEntity work = workRepository.findById(event.getWorkId()).get();
        WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();
        workRunContext = workBizService.genWorkRunContext(workInstance.getId(), work, workConfig);
      }else{
        workRunContext = event.getWorkRunContext();
      }

      // 同步执行作业
      WorkExecutor workExecutor = workExecutorFactory.create(workRunContext.getWorkType());
      workExecutor.syncExecute(workRunContext);
    }

    // 判断工作流是否执行完毕，检查结束节点是否都运行完
    synchronized (this) {

      // 获取结束节点实例
      List<String> endNodes = WorkflowUtils.getEndNodes(event.getNodeMapping(), event.getNodeList());
      List<WorkInstanceEntity> endNodeInstance = workInstanceRepository.findAllByWorkIdAndWorkflowInstanceId(endNodes, event.getFlowInstanceId());
      boolean flowIsOver = endNodeInstance.stream().allMatch(e -> !InstanceStatus.PENDING.equals(e.getStatus()) && !InstanceStatus.RUNNING.equals(e.getStatus()));

      // 判断实例是否执行完
      if (flowIsOver) {
        boolean flowIsError = endNodeInstance.stream().anyMatch(e -> InstanceStatus.FAIL.equals(e.getStatus()));
        WorkflowInstanceEntity workflowInstance = workflowInstanceRepository.findById(event.getFlowInstanceId()).get();
        workflowInstance.setStatus(flowIsError ? InstanceStatus.FAIL : InstanceStatus.SUCCESS);
        workflowInstance.setExecEndDateTime(new Date());
        workflowInstanceRepository.saveAndFlush(workflowInstance);
        return;
      }
    }

    // 工作流没有继续，执行完后推送子节点
    List<String> sonNodes = WorkflowUtils.getSonNodes(event.getNodeMapping(), event.getWorkId());
    sonNodes.forEach(e -> {
      if (!Strings.isEmpty(e)) {
        WorkRunEvent metaEvent = new WorkRunEvent(event.getFlowInstanceId(), e, event.getNodeMapping(), event.getNodeList(), event.getDagStartList(), event.getDagEndList(), event.getUserId(), event.getTenantId());
        eventPublisher.publishEvent(metaEvent);
      }
    });
  }
}
