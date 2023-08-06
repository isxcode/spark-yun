package com.isxcode.star.modules.work.run;

import static com.isxcode.star.security.main.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.security.main.WebSecurityConfig.USER_ID;

import com.isxcode.star.api.instance.constants.InstanceStatus;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.backend.api.base.exceptions.WorkRunException;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.workflow.entity.WorkflowInstanceEntity;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Async;

/** 作业执行器. */
@Slf4j
@RequiredArgsConstructor
public abstract class WorkExecutor {

  public static final Map<String, Thread> WORK_THREAD = new HashMap<>();

  private final WorkInstanceRepository workInstanceRepository;

  private final WorkflowInstanceRepository workflowInstanceRepository;

  protected abstract void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance);

  protected abstract void abort(WorkInstanceEntity workInstance);

  /** 更新实例的日志和状态 */
  public WorkInstanceEntity updateInstance(
      WorkInstanceEntity workInstance, StringBuilder logBuilder) {

    workInstance.setSubmitLog(logBuilder.toString());
    return workInstanceRepository.saveAndFlush(workInstance);
  }

  /** 异步执行作业. */
  @Async("sparkYunWorkThreadPool")
  public void asyncExecute(WorkRunContext workRunContext) {

    // 初始化异步线程中的上下文
    USER_ID.set(workRunContext.getUserId());
    TENANT_ID.set(workRunContext.getTenantId());

    syncExecute(workRunContext);
  }

  /** 同步执行作业. */
  public void syncExecute(WorkRunContext workRunContext) {

    // 获取作业实例
    WorkInstanceEntity workInstance =
        workInstanceRepository.findById(workRunContext.getInstanceId()).get();

    // 将线程存到Map
    WORK_THREAD.put(workInstance.getId(), Thread.currentThread());

    // 初始化日志
    StringBuilder logBuilder = new StringBuilder();
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始提交作业 \n");

    // 更新作业实例开始运行参数
    workInstance.setStatus(InstanceStatus.RUNNING);
    workInstance.setSubmitLog(logBuilder.toString());
    workInstance.setExecStartDateTime(new Date());
    workInstance = workInstanceRepository.saveAndFlush(workInstance);

    try {

      // 日志需要贯穿上下文
      workRunContext.setLogBuilder(logBuilder);

      // 开始执行作业
      execute(workRunContext, workInstance);

      // 重新获取当前最新实例
      workInstance = workInstanceRepository.findById(workRunContext.getInstanceId()).get();

      // 更新作业实例成功状态
      workInstance.setStatus(InstanceStatus.SUCCESS);
      workInstance.setExecEndDateTime(new Date());
      logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("执行成功 \n");
      workInstance.setSubmitLog(logBuilder.toString());
      workInstanceRepository.save(workInstance);

      // 修改工作流日志
      if (!Strings.isEmpty(workInstance.getWorkflowInstanceId())) {
        WorkflowInstanceEntity workflowInstance =
            workflowInstanceRepository.findById(workInstance.getWorkflowInstanceId()).get();
        String runLog =
            workflowInstanceRepository.getWorkflowLog(workflowInstance.getId())
                + "\n"
                + LocalDateTime.now()
                + WorkLog.SUCCESS_INFO
                + "作业: 【"
                + workRunContext.getWorkName()
                + "】运行成功";
        workflowInstance.setRunLog(runLog);
        workflowInstanceRepository.setWorkflowLog(workflowInstance.getId(), runLog);
      }

      // 执行完请求线程
      WORK_THREAD.remove(workInstance.getId());

    } catch (WorkRunException e) {

      // 打印异常
      log.error(e.getMsg());

      // 重新获取当前最新实例
      workInstance = workInstanceRepository.findById(workRunContext.getInstanceId()).get();

      // 更新作业实例失败状态
      workInstance.setStatus(InstanceStatus.FAIL);
      workInstance.setExecEndDateTime(new Date());
      logBuilder.append(e.getMsg());
      logBuilder.append(LocalDateTime.now()).append(WorkLog.ERROR_INFO).append("执行失败 \n");
      workInstance.setSubmitLog(logBuilder.toString());
      workInstanceRepository.save(workInstance);

      // 修改工作流日志
      if (!Strings.isEmpty(workInstance.getWorkflowInstanceId())) {
        WorkflowInstanceEntity workflowInstance =
            workflowInstanceRepository.findById(workInstance.getWorkflowInstanceId()).get();
        String runLog =
            workflowInstanceRepository.getWorkflowLog(workflowInstance.getId())
                + "\n"
                + LocalDateTime.now()
                + WorkLog.SUCCESS_INFO
                + "作业: 【"
                + workRunContext.getWorkName()
                + "】运行失败";
        workflowInstance.setRunLog(runLog);
        workflowInstanceRepository.setWorkflowLog(workflowInstance.getId(), runLog);
      }
    }

    // 执行完请求线程
    WORK_THREAD.remove(workInstance.getId());
  }

  public void syncAbort(WorkInstanceEntity workInstance) {

    this.abort(workInstance);
  }
}
