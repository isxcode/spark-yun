package com.isxcode.star.backend.module.work.run;

import com.isxcode.star.api.constants.work.WorkLog;
import com.isxcode.star.api.constants.work.instance.InstanceStatus;
import com.isxcode.star.api.exceptions.WorkRunException;
import com.isxcode.star.backend.module.work.instance.WorkInstanceEntity;
import com.isxcode.star.backend.module.work.instance.WorkInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.Date;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

/**
 * 作业执行器.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class WorkExecutor {

  private final WorkInstanceRepository workInstanceRepository;

  protected abstract void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance);

  /**
   * 更新实例的日志和状态
   */
  public WorkInstanceEntity updateInstance(WorkInstanceEntity workInstance, StringBuilder logBuilder) {

    workInstance.setSubmitLog(logBuilder.toString());
    return workInstanceRepository.saveAndFlush(workInstance);
  }

  /**
   * 异步执行作业.
   */
  @Async("sparkYunWorkThreadPool")
  public void asyncExecute(WorkRunContext workRunContext) {

    // 初始化异步线程中的上下文
    USER_ID.set(workRunContext.getUserId());
    TENANT_ID.set(workRunContext.getTenantId());

    syncExecute(workRunContext);
  }

  /**
   * 同步执行作业.
   */
  public void syncExecute(WorkRunContext workRunContext) {

    // 获取作业实例
    WorkInstanceEntity workInstance = workInstanceRepository.findById(workRunContext.getInstanceId()).get();

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
    }
  }
}
