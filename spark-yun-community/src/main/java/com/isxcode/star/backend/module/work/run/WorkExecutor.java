package com.isxcode.star.backend.module.work.run;

import com.isxcode.star.api.constants.work.WorkLog;
import com.isxcode.star.api.constants.work.instance.InstanceStatus;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.exceptions.WorkRunException;
import com.isxcode.star.api.pojos.workflow.dto.WorkRunContext;
import com.isxcode.star.backend.module.work.instance.WorkInstanceEntity;
import com.isxcode.star.backend.module.work.instance.WorkInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.Date;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

@Slf4j
@RequiredArgsConstructor
public abstract class WorkExecutor {

  private final WorkInstanceRepository workInstanceRepository;

  protected abstract void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance);

  public WorkInstanceEntity updateInstance(WorkInstanceEntity workInstance, StringBuilder logBuilder) {

    workInstance.setSubmitLog(logBuilder.toString());
    return workInstanceRepository.saveAndFlush(workInstance);
  }

  @Async("sparkYunWorkThreadPool")
  public void executeWork(WorkRunContext workRunContext) {

    // 初始化异步线程中的上下文
    USER_ID.set(workRunContext.getUserId());
    TENANT_ID.set(workRunContext.getTenantId());

    // 获取实例信息
    WorkInstanceEntity workInstance = workInstanceRepository.findById(workRunContext.getInstanceId())
      .orElseThrow(() -> new SparkYunException(workRunContext.getWorkId() + "作业异常无法执行"));

    // 初始化日志
    StringBuilder logBuilder = new StringBuilder();
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("提交作业完成 \n");

    // 更新实例信息
    workInstance.setStatus(InstanceStatus.RUNNING);
    workInstance.setSubmitLog(logBuilder.toString());
    workInstance.setExecStartDateTime(new Date());
    workInstance = workInstanceRepository.saveAndFlush(workInstance);

    try {

      // 日志需要贯穿上下文
      workRunContext.setLogBuilder(logBuilder);

      // 开始执行
      execute(workRunContext, workInstance);

      // 执行成功
      workInstance = workInstanceRepository.findById(workRunContext.getInstanceId()).orElseThrow(() -> new SparkYunException("实例异常"));
      workInstance.setStatus(InstanceStatus.SUCCESS);
      workInstance.setExecEndDateTime(new Date());
      logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("执行成功 \n");
      workInstance.setSubmitLog(logBuilder.toString());
      workInstanceRepository.save(workInstance);
    } catch (WorkRunException e) {

      // 打印异常
      log.error(e.getMsg());

      // 补充异常日志，更新实例
      logBuilder.append(e.getMsg());
      workInstance = workInstanceRepository.findById(workRunContext.getInstanceId()).orElseThrow(() -> new SparkYunException("实例异常"));
      workInstance.setStatus(InstanceStatus.FAIL);
      workInstance.setExecEndDateTime(new Date());
      logBuilder.append(LocalDateTime.now()).append(WorkLog.ERROR_INFO).append("执行失败 \n");
      workInstance.setSubmitLog(logBuilder.toString());
      workInstanceRepository.save(workInstance);
    }
  }
}
