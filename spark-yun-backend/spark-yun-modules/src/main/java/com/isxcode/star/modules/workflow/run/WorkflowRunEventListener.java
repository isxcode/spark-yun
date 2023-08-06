package com.isxcode.star.modules.workflow.run;

import static com.isxcode.star.security.main.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.security.main.WebSecurityConfig.USER_ID;

import com.isxcode.star.api.instance.constants.InstanceStatus;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.modules.work.entity.VipWorkVersionEntity;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.VipWorkVersionRepository;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.work.repository.WorkRepository;
import com.isxcode.star.modules.work.run.WorkExecutor;
import com.isxcode.star.modules.work.run.WorkExecutorFactory;
import com.isxcode.star.modules.work.run.WorkRunContext;
import com.isxcode.star.modules.work.service.WorkBizService;
import com.isxcode.star.modules.workflow.entity.WorkflowInstanceEntity;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/** 工作流执行器. */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowRunEventListener {

  private final ApplicationEventPublisher eventPublisher;

  private final WorkExecutorFactory workExecutorFactory;

  private final WorkBizService workBizService;

  private final WorkInstanceRepository workInstanceRepository;

  private final WorkflowInstanceRepository workflowInstanceRepository;

  private final WorkRepository workRepository;

  private final WorkConfigRepository workConfigRepository;

  private final VipWorkVersionRepository vipWorkVersionRepository;

  @EventListener
  @Async("springEventThreadPool")
  public void onApplicationEvent(WorkflowRunEvent event) {

    // 由于是异步，先初始化系统参数
    USER_ID.set(event.getUserId());
    TENANT_ID.set(event.getTenantId());

    // 修改状态前都要加锁，给工作流实例加锁
    synchronized (event.getFlowInstanceId()) {

      // 查询作业实例
      WorkInstanceEntity workInstance =
          workInstanceRepository.findByWorkIdAndWorkflowInstanceId(
              event.getWorkId(), event.getFlowInstanceId());

      // 已中止的任务，不可以再跑
      if (InstanceStatus.ABORT.equals(workInstance.getStatus())) {
        return;
      }

      // 跑过了或者正在跑，不可以再跑
      if (!InstanceStatus.PENDING.equals(workInstance.getStatus())
          && !InstanceStatus.BREAK.equals(workInstance.getStatus())) {
        return;
      }

      // 在调度中，如果自身定时器没有被触发，不可以跑
      // 先接受定时器触发，才能接受spring的event事件触发
      if (!Strings.isEmpty(event.getVersionId()) && !workInstance.getQuartzHasRun()) {
        return;
      }

      // 判断父级是否可以执行
      List<String> parentNodes =
          WorkflowUtils.getParentNodes(event.getNodeMapping(), event.getWorkId());
      List<WorkInstanceEntity> parentInstances =
          workInstanceRepository.findAllByWorkIdAndWorkflowInstanceId(
              parentNodes, event.getFlowInstanceId());
      boolean parentIsError =
          parentInstances.stream().anyMatch(e -> InstanceStatus.FAIL.equals(e.getStatus()));
      boolean parentIsBreak =
          parentInstances.stream().anyMatch(e -> InstanceStatus.BREAK.equals(e.getStatus()));
      boolean parentIsRunning =
          parentInstances.stream()
              .anyMatch(
                  e ->
                      InstanceStatus.RUNNING.equals(e.getStatus())
                          || InstanceStatus.PENDING.equals(e.getStatus()));

      // 如果父级在运行中，直接中断
      if (parentIsRunning) {
        return;
      }

      // 根据父级的不同状态，执行不同的逻辑
      if (parentIsError) {
        // 如果父级有错，则状态直接变更为失败
        workInstance.setStatus(InstanceStatus.FAIL);
        workInstance.setSubmitLog("父级执行失败");
        workInstance.setExecEndDateTime(new Date());
      } else if (parentIsBreak || InstanceStatus.BREAK.equals(workInstance.getStatus())) {
        workInstance.setStatus(InstanceStatus.BREAK);
        workInstance.setExecEndDateTime(new Date());
      } else {
        workInstance.setStatus(InstanceStatus.RUNNING);
      }
      workInstanceRepository.saveAndFlush(workInstance);
    }

    // 再次查询作业实例，如果状态为运行中，则可以开始运行作业
    WorkInstanceEntity workInstance =
        workInstanceRepository.findByWorkIdAndWorkflowInstanceId(
            event.getWorkId(), event.getFlowInstanceId());
    if (InstanceStatus.RUNNING.equals(workInstance.getStatus())) {

      // 作业开始执行，添加作业流实例日志
      synchronized (event.getFlowInstanceId()) {
        WorkflowInstanceEntity workflowInstance =
            workflowInstanceRepository.findById(event.getFlowInstanceId()).get();

        // 保存到缓存中
        String runLog =
            workflowInstanceRepository.getWorkflowLog(event.getFlowInstanceId())
                + "\n"
                + LocalDateTime.now()
                + WorkLog.SUCCESS_INFO
                + "作业: 【"
                + event.getWorkName()
                + "】开始执行";
        workflowInstanceRepository.setWorkflowLog(event.getFlowInstanceId(), runLog);

        // 更新工作流实例日志
        workflowInstance.setRunLog(runLog);
        workflowInstanceRepository.saveAndFlush(workflowInstance);
      }

      // 封装workRunContext
      WorkRunContext workRunContext;
      if (Strings.isEmpty(event.getVersionId())) {
        // 通过workId封装workRunContext
        WorkEntity work = workRepository.findById(event.getWorkId()).get();
        WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();
        workRunContext = WorkflowUtils.genWorkRunContext(workInstance.getId(), work, workConfig);
      } else {
        // 通过versionId封装workRunContext
        VipWorkVersionEntity workVersion =
            vipWorkVersionRepository.findById(event.getVersionId()).get();
        workRunContext = WorkflowUtils.genWorkRunContext(workInstance.getId(), workVersion);
      }

      // 同步执行作业
      WorkExecutor workExecutor = workExecutorFactory.create(workRunContext.getWorkType());
      workExecutor.syncExecute(workRunContext);
    }

    // 判断工作流是否执行完毕，检查结束节点是否都运行完
    synchronized (event.getFlowInstanceId()) {

      // 获取结束节点实例
      List<String> endNodes =
          WorkflowUtils.getEndNodes(event.getNodeMapping(), event.getNodeList());
      List<WorkInstanceEntity> endNodeInstance =
          workInstanceRepository.findAllByWorkIdAndWorkflowInstanceId(
              endNodes, event.getFlowInstanceId());
      boolean flowIsOver =
          endNodeInstance.stream()
              .allMatch(
                  e ->
                      InstanceStatus.FAIL.equals(e.getStatus())
                          || InstanceStatus.SUCCESS.equals(e.getStatus())
                          || InstanceStatus.ABORT.equals(e.getStatus())
                          || InstanceStatus.BREAK.equals(e.getStatus()));

      // 判断工作流是否执行完
      if (flowIsOver) {
        boolean flowIsError =
            endNodeInstance.stream().anyMatch(e -> InstanceStatus.FAIL.equals(e.getStatus()));
        WorkflowInstanceEntity workflowInstance =
            workflowInstanceRepository.findById(event.getFlowInstanceId()).get();
        workflowInstance.setStatus(flowIsError ? InstanceStatus.FAIL : InstanceStatus.SUCCESS);
        workflowInstance.setRunLog(
            workflowInstanceRepository.getWorkflowLog(event.getFlowInstanceId())
                + "\n"
                + LocalDateTime.now()
                + (flowIsError ? WorkLog.ERROR_INFO : WorkLog.SUCCESS_INFO)
                + (flowIsError ? "运行失败" : "运行成功"));
        workflowInstance.setExecEndDateTime(new Date());
        workflowInstanceRepository.saveAndFlush(workflowInstance);

        // 清除缓存中的作业流日志
        workflowInstanceRepository.deleteWorkflowLog(event.getFlowInstanceId());
        return;
      }
    }

    // 工作流没有执行完，解析推送子节点
    List<String> sonNodes = WorkflowUtils.getSonNodes(event.getNodeMapping(), event.getWorkId());
    List<WorkEntity> sonNodeWorks = workRepository.findAllByWorkIds(sonNodes);
    sonNodeWorks.forEach(
        work -> {
          WorkflowRunEvent metaEvent = new WorkflowRunEvent(work.getId(), work.getName(), event);
          WorkInstanceEntity sonWorkInstance =
              workInstanceRepository.findByWorkIdAndWorkflowInstanceId(
                  work.getId(), event.getFlowInstanceId());
          metaEvent.setVersionId(sonWorkInstance.getVersionId());
          eventPublisher.publishEvent(metaEvent);
        });
  }
}
