package com.isxcode.spark.modules.work.run;


import com.alibaba.fastjson2.JSON;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.EventType;
import com.isxcode.spark.api.work.constants.WorkLog;
import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.work.entity.*;
import com.isxcode.spark.modules.work.repository.*;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;

import java.time.LocalDateTime;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;

@Slf4j
@RequiredArgsConstructor
public abstract class WorkExecutor {

    public static final Map<String, Thread> WORK_THREAD = new HashMap<>();

    private final AlarmService alarmService;

    private final Scheduler scheduler;

    private final Locker locker;

    private final WorkRepository workRepository;

    private final WorkInstanceRepository workInstanceRepository;

    private final WorkflowInstanceRepository workflowInstanceRepository;

    private final WorkEventRepository workEventRepository;

    private final WorkRunJobFactory workRunJobFactory;

    private final SqlFunctionService sqlFunctionService;

    private final WorkConfigRepository workConfigRepository;

    private final VipWorkVersionRepository vipWorkVersionRepository;

    public abstract String getWorkType();

    /**
     * 每个作业的执行逻辑
     *
     * @param workRunContext 作业运行所需要的上下文
     * @param workInstance 作业实例
     * @param workEvent 作业运行的事件，记录当前作业运行到哪里了
     */
    protected abstract String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
        WorkEventEntity workEvent) throws Exception;

    protected abstract void abort(WorkInstanceEntity workInstance) throws Exception;

    public String infoLog(String log) {

        return LocalDateTime.now() + WorkLog.SUCCESS_INFO + log + "\n";
    }

    public String errorLog(String log) {

        return LocalDateTime.now() + WorkLog.ERROR_INFO + log + "\n";
    }

    /**
     * 翻译上游的jsonPath.
     */
    public String parseJsonPath(String value, WorkInstanceEntity workInstance) {

        if (workInstance.getWorkflowInstanceId() == null) {
            return value.replace("get_json_value", "get_json_default_value")
                .replace("get_regex_value", "get_regex_default_value")
                .replace("get_table_value", "get_table_default_value");
        }

        List<WorkInstanceEntity> allWorkflowInstance =
            workInstanceRepository.findAllByWorkflowInstanceId(workInstance.getWorkflowInstanceId());

        for (WorkInstanceEntity e : allWorkflowInstance) {
            if (InstanceStatus.SUCCESS.equals(e.getStatus()) && e.getResultData() != null) {
                value = value.replace("${qing." + e.getWorkId() + ".result_data}",
                    Base64.getEncoder().encodeToString(e.getResultData().getBytes()));
            }
        }

        return sqlFunctionService.parseSqlFunction(value);
    }

    public WorkInstanceEntity updateInstance(WorkInstanceEntity workInstance, StringBuilder logBuilder) {

        workInstance.setSubmitLog(logBuilder.toString());
        return workInstanceRepository.saveAndFlush(workInstance);
    }

    public void updateWorkEvent(WorkEventEntity workEvent, WorkRunContext workRunContext) {

        workEvent.setEventContext(JSON.toJSONString(workRunContext));
        workEventRepository.save(workEvent);
    }

    public String updateWorkEventAndInstance(WorkInstanceEntity workInstance, StringBuilder logBuilder,
        WorkEventEntity workEvent, WorkRunContext workRunContext) {

        // 保存提交日志
        workInstance.setSubmitLog(logBuilder.toString());
        workInstanceRepository.save(workInstance);

        // 保存事件
        workEvent.setEventContext(JSON.toJSONString(workRunContext));
        workEvent.setEventProcess(workEvent.getEventProcess() + 1);
        workEventRepository.save(workEvent);

        // 返回继续运行
        return InstanceStatus.RUNNING;
    }

    /**
     * 执行作业.
     */
    public String runWork(String workEventId, String workEventType) {

        if (EventType.WORK.equals(workEventType)) {
            return runSingleWork(workEventId);
        } else {
            return "";
            // return runFlowWork(workEventId);
        }
    }

    public void syncAbort(WorkInstanceEntity workInstance) throws Exception {

        this.abort(workInstance);
    }

    public String runSingleWork(String workEventId) {

        // 获取事件和上下文
        WorkEventEntity workEvent = workEventRepository.findById(workEventId).get();
        WorkRunContext workRunContext = JSON.parseObject(workEvent.getEventContext(), WorkRunContext.class);

        // 获取作业最新实例
        WorkInstanceEntity workInstance = workInstanceRepository.findById(workRunContext.getInstanceId()).get();

        // 中止、中止中、成功、失败，不可以再运行
        if (InstanceStatus.ABORT.equals(workInstance.getStatus())
            || InstanceStatus.ABORTING.equals(workInstance.getStatus())
            || InstanceStatus.SUCCESS.equals(workInstance.getStatus())
            || InstanceStatus.FAIL.equals(workInstance.getStatus())) {
            return InstanceStatus.FINISHED;
        }

        // 将作业状态改成运行中
        if (InstanceStatus.PENDING.equals(workInstance.getStatus())) {
            workInstance.setSubmitLog(infoLog("🔥 开始提交作业"));
            workInstance.setStatus(InstanceStatus.RUNNING);
            workInstance.setExecStartDateTime(new Date());
            workInstanceRepository.saveAndFlush(workInstance);
        }

        try {

            // 执行作业，每次都会执行
            String executeStatus = execute(workRunContext, workInstance, workEvent);

            // 如果是运行中，直接跳过，等待下一个调度
            if (InstanceStatus.RUNNING.equals(executeStatus)) {
                return InstanceStatus.RUNNING;
            }

            // 作业运行成功
            if (InstanceStatus.SUCCESS.equals(executeStatus)) {

                // 只有运行中的作业，才能改成成功
                workInstance = workInstanceRepository.findById(workRunContext.getInstanceId()).get();
                if (InstanceStatus.RUNNING.equals(workInstance.getStatus())) {
                    workInstance.setStatus(InstanceStatus.SUCCESS);
                    workInstance.setExecEndDateTime(new Date());
                    workInstance.setDuration(
                        (System.currentTimeMillis() - workInstance.getExecStartDateTime().getTime()) / 1000);
                    workInstance.setSubmitLog(
                        workInstance.getSubmitLog() + LocalDateTime.now() + WorkLog.SUCCESS_INFO + "✅ 执行成功 \n");
                    workInstanceRepository.saveAndFlush(workInstance);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            // 只有运行中的作业，才能改成失败
            workInstance = workInstanceRepository.findById(workRunContext.getInstanceId()).get();
            if (InstanceStatus.RUNNING.equals(workInstance.getStatus())) {
                workInstance.setStatus(InstanceStatus.FAIL);
                workInstance.setExecEndDateTime(new Date());
                workInstance
                    .setDuration((System.currentTimeMillis() - workInstance.getExecStartDateTime().getTime()) / 1000);
                workInstance.setSubmitLog(workInstance.getSubmitLog()
                    + (e instanceof WorkRunException ? ((WorkRunException) e).getMsg() : e.getMessage()) + "\n"
                    + LocalDateTime.now() + WorkLog.ERROR_INFO + "❌ 执行失败");
                workInstanceRepository.saveAndFlush(workInstance);
            }
        }

        // 单个作业运行结束
        return InstanceStatus.FINISHED;
    }

    // public String runFlowWork(WorkRunContext workRunContext) {
    //
    // // 获取事件
    // WorkEventEntity workEvent = workEventRepository.findById(workRunContext.getEventId()).get();
    //
    // // 修改节点状态只能一个一个改，防止并发压力大，导致作业执行两次
    // Integer workChangeStatus = locker.lock("work_change_status_" +
    // workRunContext.getFlowInstanceId());
    //
    // // 获取最新作业实例
    // WorkInstanceEntity workInstance =
    // workInstanceRepository.findById(workRunContext.getInstanceId()).get();
    //
    // // 如果不是当前实例的eventId，直接杀掉
    // if (workInstance.getEventId() != null &&
    // !workInstance.getEventId().equals(workRunContext.getEventId())) {
    // locker.unlock(workChangeStatus);
    // return InstanceStatus.FINISHED;
    // }
    //
    // // 中止、中止中，不可以再运行
    // if (InstanceStatus.ABORT.equals(workInstance.getStatus())
    // || InstanceStatus.ABORTING.equals(workInstance.getStatus())) {
    // locker.unlock(workChangeStatus);
    // return InstanceStatus.FINISHED;
    // }
    //
    // // 将作业实例状态改为运行中
    // if (InstanceStatus.PENDING.equals(workInstance.getStatus())) {
    //
    // // 基线管理，任务开始运行，发送消息
    // if (InstanceType.AUTO.equals(workInstance.getInstanceType())) {
    // alarmService.sendWorkMessage(workInstance, AlarmEventType.START_RUN);
    // }
    //
    // // 在调度中的作业，如果自身定时器没有被触发，不可以再运行
    // if (!Strings.isEmpty(workRunContext.getVersionId()) && !workInstance.getQuartzHasRun()) {
    // locker.unlock(workChangeStatus);
    // return InstanceStatus.FINISHED;
    // }
    //
    // // 获取父级的作业实例状态
    // List<String> parentNodes =
    // WorkflowUtils.getParentNodes(workRunContext.getNodeMapping(), workRunContext.getWorkId());
    // List<WorkInstanceEntity> parentInstances = workInstanceRepository
    // .findAllByWorkIdAndWorkflowInstanceId(parentNodes, workRunContext.getFlowInstanceId());
    // boolean parentIsError = parentInstances.stream().anyMatch(e ->
    // InstanceStatus.FAIL.equals(e.getStatus()));
    // boolean parentIsBreak = parentInstances.stream().anyMatch(e ->
    // InstanceStatus.BREAK.equals(e.getStatus()));
    // boolean parentIsRunning = parentInstances.stream().anyMatch(
    // e -> InstanceStatus.RUNNING.equals(e.getStatus()) ||
    // InstanceStatus.PENDING.equals(e.getStatus()));
    //
    // // 判断当前作业实例的状态
    // if (parentIsRunning) {
    // // 如果父级在运行中，不可以再运行
    // locker.unlock(workChangeStatus);
    // return InstanceStatus.FINISHED;
    // } else if (parentIsError) {
    // // 如果父级有错，则状态直接变更为失败
    // workInstance.setStatus(InstanceStatus.FAIL);
    // workInstance.setSubmitLog("父级执行失败");
    // workInstance.setExecStartDateTime(new Date());
    // workInstance.setExecEndDateTime(new Date());
    // workInstance.setDuration(0L);
    // } else if (parentIsBreak || InstanceStatus.BREAK.equals(workInstance.getStatus())) {
    // // 如果父级有中断，则状态直接变更为中断
    // workInstance.setStatus(InstanceStatus.BREAK);
    // workInstance.setExecEndDateTime(new Date());
    // workInstance.setExecStartDateTime(new Date());
    // workInstance
    // .setDuration((System.currentTimeMillis() - workInstance.getExecStartDateTime().getTime()) /
    // 1000);
    // } else {
    // // 修改作业实例状态为运行中
    // workInstance.setSubmitLog(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始提交作业 \n");
    // workInstance.setStatus(InstanceStatus.RUNNING);
    // workInstance.setExecStartDateTime(new Date());
    // }
    //
    // // 修改作业实例状态，只能一个一个节点修改
    // workInstanceRepository.saveAndFlush(workInstance);
    // log.debug("作业流实例id:{} 作业实例id:{} 事件id:{} 事件:【{}】修改状态为运行中", workRunContext.getFlowInstanceId(),
    // workRunContext.getInstanceId(), workRunContext.getEventId(), workRunContext.getWorkName());
    // }
    //
    // locker.unlock(workChangeStatus);
    //
    // // 实例只有运行中，才能执行作业
    // if (InstanceStatus.RUNNING.equals(workInstance.getStatus())) {
    // try {
    //
    // // 运行前，保存当前线程
    // WORK_THREAD.put(workInstance.getId(), Thread.currentThread());
    //
    // // 开始执行作业，每次都要执行
    // log.debug("作业流实例id:{} 作业实例id:{} 事件id:{} 事件:【{}】执行一次作业", workRunContext.getFlowInstanceId(),
    // workRunContext.getInstanceId(), workRunContext.getEventId(), workRunContext.getWorkName());
    // workInstance.setEventId(workRunContext.getEventId());
    // String executeStatus = execute(workRunContext, workInstance, workEvent);
    //
    // // 作业执行完毕，移除当前线程
    // WORK_THREAD.remove(workInstance.getId());
    //
    // // 如果是运行中，直接跳过，下个调度再执行
    // if (InstanceStatus.RUNNING.equals(executeStatus)) {
    // return InstanceStatus.RUNNING;
    // }
    //
    // // 作业运行成功
    // if (InstanceStatus.SUCCESS.equals(executeStatus)) {
    //
    // // 只有运行中的作业，才能改成成功
    // workInstance = workInstanceRepository.findById(workRunContext.getInstanceId()).get();
    // if (InstanceStatus.RUNNING.equals(workInstance.getStatus())) {
    // workInstance.setStatus(InstanceStatus.SUCCESS);
    // workInstance.setExecEndDateTime(new Date());
    // workInstance.setDuration(
    // (System.currentTimeMillis() - workInstance.getExecStartDateTime().getTime()) / 1000);
    // workInstance.setSubmitLog(
    // workInstance.getSubmitLog() + LocalDateTime.now() + WorkLog.SUCCESS_INFO + "执行成功 \n");
    // workInstanceRepository.saveAndFlush(workInstance);
    // log.debug("作业流实例id:{} 作业实例id:{} 事件id:{} 事件:【{}】运行成功", workRunContext.getFlowInstanceId(),
    // workRunContext.getInstanceId(), workRunContext.getEventId(), workRunContext.getWorkName());
    //
    // // 基线管理，任务运行成功发送消息
    // if (InstanceType.AUTO.equals(workInstance.getInstanceType())) {
    // alarmService.sendWorkMessage(workInstance, AlarmEventType.RUN_SUCCESS);
    // }
    // }
    // }
    // } catch (Exception e) {
    // log.error(e.getMessage(), e);
    //
    // // 只有运行中的作业，才能改成失败
    // workInstance = workInstanceRepository.findById(workRunContext.getInstanceId()).get();
    // if (InstanceStatus.RUNNING.equals(workInstance.getStatus())) {
    // workInstance.setStatus(InstanceStatus.FAIL);
    // workInstance.setExecEndDateTime(new Date());
    // workInstance.setDuration(
    // (System.currentTimeMillis() - workInstance.getExecStartDateTime().getTime()) / 1000);
    // workInstance.setSubmitLog(workInstance.getSubmitLog()
    // + (e instanceof WorkRunException ? ((WorkRunException) e).getMsg() : e.getMessage())
    // + LocalDateTime.now() + WorkLog.ERROR_INFO + "执行失败 \n");
    // workInstanceRepository.saveAndFlush(workInstance);
    // log.debug("作业流实例id:{} 作业实例id:{} 事件id:{} 事件:【{}】运行失败", workRunContext.getFlowInstanceId(),
    // workRunContext.getInstanceId(), workRunContext.getEventId(), workRunContext.getWorkName());
    //
    // // 基线管理，任务运行失败发送消息
    // if (InstanceType.AUTO.equals(workInstance.getInstanceType())) {
    // alarmService.sendWorkMessage(workInstance, AlarmEventType.RUN_FAIL);
    // }
    // }
    // }
    //
    // // 基线管理，任务运行结束发送消息
    // if (InstanceType.AUTO.equals(workInstance.getInstanceType())) {
    // alarmService.sendWorkMessage(workInstance, AlarmEventType.RUN_END);
    // }
    //
    // }
    //
    // // 如果作业执行结束，需要继续推送任务
    // if ((InstanceStatus.SUCCESS.equals(workInstance.getStatus())
    // || InstanceStatus.FAIL.equals(workInstance.getStatus())) &&
    // !Strings.isEmpty(workInstance.getEventId())) {
    //
    // // 获取最新的作业流实例
    // WorkflowInstanceEntity workflowInstance =
    // workflowInstanceRepository.findById(workRunContext.getFlowInstanceId()).get();
    //
    // // 中止中的作业流，不可以再推送
    // if (InstanceStatus.ABORTING.equals(workflowInstance.getStatus())) {
    // return InstanceStatus.FINISHED;
    // }
    //
    // // 获取所有节点实例状态，判断作业流是否执行完毕
    // List<WorkInstanceEntity> endNodeInstance = workInstanceRepository
    // .findAllByWorkIdAndWorkflowInstanceId(workRunContext.getNodeList(),
    // workRunContext.getFlowInstanceId());
    // boolean flowIsOver = endNodeInstance.stream()
    // .allMatch(e -> InstanceStatus.FAIL.equals(e.getStatus()) ||
    // InstanceStatus.SUCCESS.equals(e.getStatus())
    // || InstanceStatus.ABORT.equals(e.getStatus()) || InstanceStatus.BREAK.equals(e.getStatus()));
    //
    // // 如果作业流执行结束
    // if (flowIsOver) {
    //
    // // 修改作业流状态
    // boolean flowIsError = endNodeInstance.stream().anyMatch(e ->
    // InstanceStatus.FAIL.equals(e.getStatus()));
    // workflowInstance.setExecEndDateTime(new Date());
    // workflowInstance.setDuration(
    // (System.currentTimeMillis() - workflowInstance.getExecStartDateTime().getTime()) / 1000);
    // workflowInstance.setStatus(flowIsError ? InstanceStatus.FAIL : InstanceStatus.SUCCESS);
    // workflowInstanceRepository.saveAndFlush(workflowInstance);
    //
    // // 基线告警，作业流成功、失败、运行结束发送消息
    // if (InstanceType.AUTO.equals(workflowInstance.getInstanceType())) {
    // if (flowIsError) {
    // alarmService.sendWorkflowMessage(workflowInstance, AlarmEventType.RUN_FAIL);
    // } else {
    // alarmService.sendWorkflowMessage(workflowInstance, AlarmEventType.RUN_SUCCESS);
    // }
    // alarmService.sendWorkflowMessage(workflowInstance, AlarmEventType.RUN_END);
    // }
    // log.debug("作业流实例id:{} 作业实例id:{} 事件id:{} 事件:【{}】作业流运行结束", workRunContext.getFlowInstanceId(),
    // workRunContext.getInstanceId(), workRunContext.getEventId(), workRunContext.getWorkName());
    // } else {
    // // 工作流没有执行完，继续推送子节点
    // List<String> sonNodes =
    // WorkflowUtils.getSonNodes(workRunContext.getNodeMapping(), workRunContext.getWorkId());
    // List<WorkEntity> sonNodeWorks = workRepository.findAllByWorkIds(sonNodes);
    // sonNodeWorks.forEach(work -> {
    //
    // // 查询子作业的实例
    // WorkInstanceEntity sonWorkInstance = workInstanceRepository
    // .findByWorkIdAndWorkflowInstanceId(work.getId(), workRunContext.getFlowInstanceId());
    //
    // // 封装WorkRunContext，通过是否有versionId，判断是调度中作业还是普通手动运行的作业
    // WorkRunContext sonWorkRunContext;
    // if (Strings.isEmpty(sonWorkInstance.getVersionId())) {
    // WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();
    // sonWorkRunContext = WorkflowUtils.genWorkRunContext(sonWorkInstance.getId(), EventType.WORKFLOW,
    // work, workConfig);
    // } else {
    // VipWorkVersionEntity workVersion = vipWorkVersionRepository.findById(work.getVersionId()).get();
    // sonWorkRunContext = WorkflowUtils.genWorkRunContext(sonWorkInstance.getId(), EventType.WORKFLOW,
    // work, workVersion);
    // sonWorkRunContext.setVersionId(sonWorkInstance.getVersionId());
    // }
    // sonWorkRunContext.setDagEndList(workRunContext.getDagEndList());
    // sonWorkRunContext.setDagStartList(workRunContext.getDagStartList());
    // sonWorkRunContext.setFlowInstanceId(workRunContext.getFlowInstanceId());
    // sonWorkRunContext.setNodeMapping(workRunContext.getNodeMapping());
    // sonWorkRunContext.setNodeList(workRunContext.getNodeList());
    //
    // // 调用调度器触发子作业
    // workRunJobFactory.execute(sonWorkRunContext);
    // log.debug("作业流实例id:{} 作业实例id:{} 事件：【{}】推送【{}】", sonWorkRunContext.getFlowInstanceId(),
    // sonWorkRunContext.getInstanceId(), workRunContext.getWorkName(),
    // sonWorkRunContext.getWorkName());
    // });
    //
    // // 每个作业只能推送一次任务
    // workInstance.setEventId(null);
    // workInstanceRepository.saveAndFlush(workInstance);
    // }
    // }
    //
    // // 当前作业运行完毕
    // log.debug("作业流实例id:{} 作业实例id:{} 事件id:{} 事件:【{}】作业流最终执行结束", workRunContext.getFlowInstanceId(),
    // workRunContext.getInstanceId(), workRunContext.getEventId(), workRunContext.getWorkName());
    // return InstanceStatus.FINISHED;
    // }
}
