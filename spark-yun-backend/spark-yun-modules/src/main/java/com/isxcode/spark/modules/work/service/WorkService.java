package com.isxcode.spark.modules.work.service;

import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.LockerPrefix;
import com.isxcode.spark.api.work.constants.QuartzPrefix;
import com.isxcode.spark.api.work.constants.WorkLog;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.modules.work.entity.WorkEntity;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
import com.isxcode.spark.modules.work.repository.WorkRepository;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkExecutorFactory;
import com.isxcode.spark.modules.workflow.entity.WorkflowInstanceEntity;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkService {

    private final WorkRepository workRepository;

    private final WorkInstanceRepository workInstanceRepository;

    private final WorkflowInstanceRepository workflowInstanceRepository;

    private final WorkEventRepository workEventRepository;

    private final Scheduler scheduler;

    private final Locker locker;

    private final WorkExecutorFactory workExecutorFactory;

    @Transactional
    public void abortWork(String workInstanceId) {

        // 通过作业实例查询作业类型
        WorkInstanceEntity workInstance = getWorkInstance(workInstanceId);
        WorkEntity work = getWorkEntity(workInstance.getWorkId());

        // 第一时间等待锁，等待定时器中加的锁释放，保证定时器是运行完的
        Integer lockKey;
        List<String> onlyLockWork = Arrays.asList(WorkType.API, WorkType.EXECUTE_JDBC_SQL, WorkType.QUERY_JDBC_SQL,
            WorkType.PRQL, WorkType.SPARK_CONTAINER_SQL);
        if (onlyLockWork.contains(work.getWorkType())) {
            lockKey = locker.lockOnly(LockerPrefix.WORK_EVENT_THREAD + workInstance.getEventId());
        } else {
            lockKey = locker.lock(LockerPrefix.WORK_EVENT_THREAD + workInstance.getEventId());
        }

        // 重新获取当前最新实例
        workInstance = getWorkInstance(workInstanceId);

        // 如果成功或者失败直接结束
        if (InstanceStatus.SUCCESS.equals(workInstance.getStatus())
            || InstanceStatus.FAIL.equals(workInstance.getStatus())) {
            locker.unlock(lockKey);
            return;
        }

        // 只有运行中的作业才能中止
        if (!InstanceStatus.RUNNING.equals(workInstance.getStatus())) {
            locker.unlock(lockKey);
            throw new IsxAppException("当前状态无法中止:" + workInstance.getStatus());
        }

        // 修改状态为中止中
        workInstance.setStatus(InstanceStatus.ABORTING);
        workInstance = workInstanceRepository.save(workInstance);

        // 获取作业运行事件体
        Optional<WorkEventEntity> optionalWorkEvent = workEventRepository.findById(workInstance.getEventId());
        if (!optionalWorkEvent.isPresent()) {
            locker.unlock(lockKey);
            return;
        }
        WorkEventEntity workEvent = optionalWorkEvent.get();
        String submitLog;

        try {
            // 暂停每秒定时调度
            scheduler.pauseTrigger(TriggerKey.triggerKey(QuartzPrefix.WORK_RUN_PROCESS + workEvent.getId()));

            // 进入每个作业单独的中止逻辑
            WorkEntity workEntity = getWorkEntity(workInstance.getWorkId());
            WorkExecutor workExecutor = workExecutorFactory.create(workEntity.getWorkType());
            boolean canStop = workExecutor.syncAbort(workInstance, workEvent);

            // 无法中止
            if (!canStop) {
                scheduler.resumeTrigger(TriggerKey.triggerKey(QuartzPrefix.WORK_RUN_PROCESS + workEvent.getId()));
                locker.unlock(lockKey);
                return;
            }

            // 重新获取实例状态
            workInstance = getWorkInstance(workInstanceId);
            submitLog = workInstance.getSubmitLog() + LocalDateTime.now() + WorkLog.SUCCESS_INFO + "⚠️ 已中止  \n";
            workInstance.setStatus(InstanceStatus.ABORT);

        } catch (Exception e) {

            workInstance = getWorkInstance(workInstanceId);
            submitLog = workInstance.getSubmitLog() + LocalDateTime.now() + WorkLog.SUCCESS_INFO + "⚠️ 中止失败 \n";
            workInstance.setStatus(InstanceStatus.FAIL);
        }

        // 实例状态
        workInstance.setExecEndDateTime(new Date());
        workInstance.setSubmitLog(submitLog);
        workInstance.setDuration((System.currentTimeMillis() - workInstance.getExecStartDateTime().getTime()) / 1000);
        workInstanceRepository.save(workInstance);

        // 关闭定时器和事件，清理锁
        try {
            scheduler.unscheduleJob(TriggerKey.triggerKey(QuartzPrefix.WORK_RUN_PROCESS + workEvent.getId()));
            workEventRepository.deleteById(workEvent.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            locker.clearLock(LockerPrefix.WORK_EVENT_THREAD + workInstance.getEventId());
        }
    }

    public WorkEntity getWorkEntity(String workId) {

        return workRepository.findById(workId).orElseThrow(() -> new IsxAppException("作业不存在"));
    }

    public WorkInstanceEntity getWorkInstance(String workInstanceId) {

        return workInstanceRepository.findById(workInstanceId).orElseThrow(() -> new IsxAppException("作业实例不存在"));
    }

    public WorkflowInstanceEntity getWorkFlowInstance(String workflowInstanceId) {

        return workflowInstanceRepository.findById(workflowInstanceId)
            .orElseThrow(() -> new IsxAppException("作业流实例不存在"));
    }

    public WorkEventEntity getWorkEvent(String workEventId) {

        return workEventRepository.findById(workEventId).orElseThrow(() -> new IsxAppException("作业事件不存在"));
    }
}
