package com.isxcode.spark.modules.work.run;


import com.alibaba.fastjson2.JSON;
import com.isxcode.spark.api.alarm.constants.AlarmEventType;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.instance.constants.InstanceType;
import com.isxcode.spark.api.work.constants.EventType;
import com.isxcode.spark.api.work.constants.LockerPrefix;
import com.isxcode.spark.api.work.constants.WorkLog;
import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.meta.service.MetaColumnLineageService;
import com.isxcode.spark.modules.work.entity.*;
import com.isxcode.spark.modules.work.repository.*;
import com.isxcode.spark.modules.work.service.WorkService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.workflow.entity.WorkflowInstanceEntity;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.isxcode.spark.modules.workflow.run.WorkflowUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

@Slf4j
@RequiredArgsConstructor
public abstract class WorkExecutor {

    public static final Map<String, Thread> WORK_THREAD = new ConcurrentHashMap<>();

    private final AlarmService alarmService;

    private final Locker locker;

    private final WorkRepository workRepository;

    private final WorkInstanceRepository workInstanceRepository;

    private final WorkflowInstanceRepository workflowInstanceRepository;

    private final WorkEventRepository workEventRepository;

    private final WorkRunJobFactory workRunJobFactory;

    private final SqlFunctionService sqlFunctionService;

    private final WorkConfigRepository workConfigRepository;

    private final VipWorkVersionRepository vipWorkVersionRepository;

    private final WorkService workService;

    private final MetaColumnLineageService metaColumnLineageService;

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

    protected abstract boolean abort(WorkInstanceEntity workInstance, WorkEventEntity workEvent) throws Exception;

    public String infoLog(String log) {

        return LocalDateTime.now() + WorkLog.SUCCESS_INFO + log + "\n";
    }

    public String startLog(String log) {

        return LocalDateTime.now() + WorkLog.SUCCESS_INFO + "⌛ " + log + "\n";
    }

    public String endLog(String log) {

        return LocalDateTime.now() + WorkLog.SUCCESS_INFO + "👌 " + log + "\n";
    }

    public String statusLog(String log) {

        return LocalDateTime.now() + WorkLog.SUCCESS_INFO + "⏩ " + log + "\n";
    }

    public WorkRunException errorLogException(String log) {
        throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "⚠️ " + log + "\n");
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

    public String runWork(String workEventId, String workEventType) {

        // 执行单个作业
        if (EventType.WORK.equals(workEventType)) {
            return runSingleWork(workEventId);
        } else {
            return runFlowWork(workEventId);
        }
    }

    public boolean syncAbort(WorkInstanceEntity workInstance, WorkEventEntity workEvent) throws Exception {

        return this.abort(workInstance, workEvent);
    }

    public String runSingleWork(String workEventId) {

        // 获取作业事件和运行上下文
        WorkEventEntity workEvent = workService.getWorkEvent(workEventId);
        WorkRunContext workRunContext = JSON.parseObject(workEvent.getEventContext(), WorkRunContext.class);

        // 获取作业当前实例
        WorkInstanceEntity workInstance = workService.getWorkInstance(workRunContext.getInstanceId());

        // 把事件id保存到实例中
        workInstance.setEventId(workEvent.getId());
        workInstanceRepository.save(workInstance);

        // 中止、中止中、成功、失败，不可以再运行
        if (InstanceStatus.ABORT.equals(workInstance.getStatus())
            || InstanceStatus.SUCCESS.equals(workInstance.getStatus())
            || InstanceStatus.FAIL.equals(workInstance.getStatus())) {
            return InstanceStatus.FINISHED;
        }

        // 将作业状态改成运行中
        if (InstanceStatus.PENDING.equals(workInstance.getStatus())) {
            workInstance.setSubmitLog(infoLog("🔥 开始运行作业"));
            workInstance.setStatus(InstanceStatus.RUNNING);
            workInstance.setExecStartDateTime(new Date());
            workInstanceRepository.save(workInstance);
        }

        try {

            // 执行单个作业
            String executeStatus = execute(workRunContext, workInstance, workEvent);

            // 如果是运行中，直接跳过，等待下一个调度
            if (InstanceStatus.RUNNING.equals(executeStatus)) {
                return InstanceStatus.RUNNING;
            }

            // 作业运行成功，修改实例状态
            if (InstanceStatus.SUCCESS.equals(executeStatus)) {

                // 只有运行中的作业，才能改成成功
                workInstance = workService.getWorkInstance(workRunContext.getInstanceId());
                if (InstanceStatus.RUNNING.equals(workInstance.getStatus())) {
                    workInstance.setStatus(InstanceStatus.SUCCESS);
                    workInstance.setExecEndDateTime(new Date());
                    workInstance.setDuration(
                        (System.currentTimeMillis() - workInstance.getExecStartDateTime().getTime()) / 1000);
                    workInstance.setSubmitLog(
                        workInstance.getSubmitLog() + LocalDateTime.now() + WorkLog.SUCCESS_INFO + "✅ 运行作业成功 \n");
                    workInstanceRepository.save(workInstance);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            // 只有运行中的作业，才能改成失败
            workInstance = workService.getWorkInstance(workRunContext.getInstanceId());
            if (InstanceStatus.RUNNING.equals(workInstance.getStatus())) {
                workInstance.setStatus(InstanceStatus.FAIL);
                workInstance.setExecEndDateTime(new Date());
                workInstance
                    .setDuration((System.currentTimeMillis() - workInstance.getExecStartDateTime().getTime()) / 1000);
                workInstance.setSubmitLog(workInstance.getSubmitLog()
                    + (e instanceof WorkRunException ? ((WorkRunException) e).getMsg() : e.getMessage() + "\n")
                    + LocalDateTime.now() + WorkLog.ERROR_INFO + "❌ 运行作业失败");
                workInstanceRepository.saveAndFlush(workInstance);
            }
        }

        // 执行完成
        return InstanceStatus.FINISHED;
    }

    /**
     * 作业流中的作业运行结束后，需要额外推送.
     */
    public String runFlowWork(String workEventId) {

        // 获取作业事件和运行上下文
        WorkEventEntity workEvent = workService.getWorkEvent(workEventId);
        WorkRunContext workRunContext = JSON.parseObject(workEvent.getEventContext(), WorkRunContext.class);

        // 获取最新作业实例
        WorkInstanceEntity workInstance = workService.getWorkInstance(workRunContext.getInstanceId());

        // 当实例状态是运行中，并且绑定了指定的作业事件，才能直接运行，中止中可能无法正常中止，需要继续运行
        if ((InstanceStatus.RUNNING.equals(workInstance.getStatus())
            || InstanceStatus.ABORTING.equals(workInstance.getStatus()) && workInstance.getEventId() != null
                && workInstance.getEventId().equals(workEventId))) {

            try {

                // 每秒执行作业
                String executeStatus = execute(workRunContext, workInstance, workEvent);

                // 如果是运行中，直接跳过，下个调度再执行
                if (InstanceStatus.RUNNING.equals(executeStatus)) {
                    return InstanceStatus.RUNNING;
                }

                // 作业运行成功
                if (InstanceStatus.SUCCESS.equals(executeStatus)) {

                    // 重新获取最新的作业实例，只有运行中的作业，才能改成成功
                    workInstance = workService.getWorkInstance(workRunContext.getInstanceId());
                    if (InstanceStatus.RUNNING.equals(workInstance.getStatus())) {

                        // 修改状态SUCCESS
                        log.debug("【作业流实例id】:{},【作业实例id】:{},【运行事件id】:{},修改状态:SUCCESS,【作业名】:{}",
                            workInstance.getWorkflowInstanceId(), workInstance.getId(), workEventId,
                            workRunContext.getWorkName());

                        // 修改实例状态
                        workInstance.setStatus(InstanceStatus.SUCCESS);
                        workInstance.setExecEndDateTime(new Date());
                        workInstance.setDuration(
                            (System.currentTimeMillis() - workInstance.getExecStartDateTime().getTime()) / 1000);
                        workInstance.setSubmitLog(
                            workInstance.getSubmitLog() + LocalDateTime.now() + WorkLog.SUCCESS_INFO + "✅ 运行作业成功 \n");
                        workInstanceRepository.saveAndFlush(workInstance);

                        // 基线管理，任务运行成功发送消息
                        if (InstanceType.AUTO.equals(workInstance.getInstanceType())) {
                            alarmService.sendWorkMessage(workInstance, AlarmEventType.RUN_SUCCESS);
                        }

                        // 只有发布的作业，任务运行成功，才能异步同步血缘
                        if (workRunContext.getVersionId() != null) {
                            metaColumnLineageService.addMetaColumnLineage(workRunContext);
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);

                // 重新获取最新的作业实例，只有运行中的作业，才能改成失败
                workInstance = workService.getWorkInstance(workRunContext.getInstanceId());
                if (InstanceStatus.RUNNING.equals(workInstance.getStatus())) {

                    // 修改状态FAIL
                    log.debug("【作业流实例id】:{},【作业实例id】:{},【运行事件id】:{},修改状态:FAIL,【作业名】:{}",
                        workInstance.getWorkflowInstanceId(), workInstance.getId(), workEventId,
                        workRunContext.getWorkName());

                    // 修改实例状态
                    workInstance.setStatus(InstanceStatus.FAIL);
                    workInstance.setExecEndDateTime(new Date());
                    workInstance.setDuration(
                        (System.currentTimeMillis() - workInstance.getExecStartDateTime().getTime()) / 1000);
                    workInstance.setSubmitLog(workInstance.getSubmitLog()
                        + (e instanceof WorkRunException ? ((WorkRunException) e).getMsg() : e.getMessage())
                        + LocalDateTime.now() + WorkLog.ERROR_INFO + "❌ 运行作业失败");
                    workInstance = workInstanceRepository.saveAndFlush(workInstance);

                    // 基线管理，任务运行失败发送消息
                    if (InstanceType.AUTO.equals(workInstance.getInstanceType())) {
                        alarmService.sendWorkMessage(workInstance, AlarmEventType.RUN_FAIL);
                    }
                }
            }

            // 基线管理，任务运行结束发送消息
            if (InstanceType.AUTO.equals(workInstance.getInstanceType())) {
                alarmService.sendWorkMessage(workInstance, AlarmEventType.RUN_END);
            }

        } else {

            // 修改状态，节点状态只能一个一个修改，防止并发压力大，导致作业执行两次
            Integer lockerKey = locker.lock(LockerPrefix.WORK_CHANGE_STATUS + workRunContext.getFlowInstanceId());

            // 获取最新作业实例，一定要以加锁后的实例为准
            workInstance = workService.getWorkInstance(workRunContext.getInstanceId());

            // 作业事件和实例绑定的不一致，为上游重复推送，不再运行
            if (workInstance.getEventId() != null && !workInstance.getEventId().equals(workEventId)) {
                locker.unlock(lockerKey);
                return InstanceStatus.FINISHED;
            }

            // 中止、中止中，不可以再运行
            if (InstanceStatus.ABORT.equals(workInstance.getStatus())
                || InstanceStatus.ABORTING.equals(workInstance.getStatus())) {
                locker.unlock(lockerKey);
                return InstanceStatus.FINISHED;
            }

            // 在调度中的作业，如果自身定时器没有被触发，不可以再运行，上游推过来，但是定时器还没到时间
            if (!Strings.isEmpty(workRunContext.getVersionId()) && !workInstance.getQuartzHasRun()) {
                locker.unlock(lockerKey);
                return InstanceStatus.FINISHED;
            }

            // 如果是中断状态赋值workEventId
            if (InstanceStatus.BREAK.equals(workInstance.getStatus())) {
                workInstance.setEventId(workEventId);
            }

            // 开始修改对PENDING状态的作业，中断状态需要传递
            if (InstanceStatus.PENDING.equals(workInstance.getStatus())) {

                // 获取父级的作业实例状态
                List<String> parentNodes =
                    WorkflowUtils.getParentNodes(workRunContext.getNodeMapping(), workRunContext.getWorkId());
                List<WorkInstanceEntity> parentInstances = workInstanceRepository
                    .findAllByWorkIdAndWorkflowInstanceId(parentNodes, workRunContext.getFlowInstanceId());
                boolean parentIsError =
                    parentInstances.stream().anyMatch(e -> InstanceStatus.FAIL.equals(e.getStatus()));
                boolean parentIsBreak =
                    parentInstances.stream().anyMatch(e -> InstanceStatus.BREAK.equals(e.getStatus()));
                boolean parentIsRunning = parentInstances.stream().anyMatch(
                    e -> InstanceStatus.RUNNING.equals(e.getStatus()) || InstanceStatus.PENDING.equals(e.getStatus()));

                // 修改状态
                if (parentIsRunning) {

                    // 如果父级在运行中，不可以再运行
                    locker.unlock(lockerKey);
                    return InstanceStatus.FINISHED;
                } else if (parentIsError) {

                    // 如果父级有错，则状态直接变更为失败
                    workInstance.setStatus(InstanceStatus.FAIL);
                    workInstance.setSubmitLog("父级执行失败");
                    workInstance.setExecEndDateTime(new Date());
                    workInstance.setDuration(0L);
                } else if (parentIsBreak) {

                    // 如果父级有中断，则状态直接变更为中断
                    workInstance.setStatus(InstanceStatus.BREAK);
                    workInstance.setSubmitLog("当前作业中断");
                    workInstance.setExecEndDateTime(new Date());
                    workInstance.setDuration(0L);
                } else {
                    // 修改作业状态为RUNNING
                    log.debug("【作业流实例id】:{},【作业实例id】:{},【运行事件id】:{},修改状态:RUNNING,【作业名】:{}",
                        workInstance.getWorkflowInstanceId(), workInstance.getId(), workEventId,
                        workRunContext.getWorkName());

                    // 基线管理，任务开始运行，发送消息
                    if (InstanceType.AUTO.equals(workInstance.getInstanceType())) {
                        alarmService.sendWorkMessage(workInstance, AlarmEventType.START_RUN);
                    }

                    // 修改作业实例状态为运行中
                    workInstance.setSubmitLog(infoLog("🔥 开始运行作业"));
                    workInstance.setStatus(InstanceStatus.RUNNING);
                }

                // 绑定作业事件
                workInstance.setEventId(workEvent.getId());

                // 保存实例状态
                workInstance.setExecStartDateTime(new Date());
                workInstanceRepository.saveAndFlush(workInstance);

                // 修改状态后继续执行
                locker.unlock(lockerKey);
                return InstanceStatus.RUNNING;
            }

            // 最终都要解锁
            locker.unlock(lockerKey);
        }

        // 每个作业运行完，都要检测一次作业流的所有作业状态，并推送后面的节点，且只对绑定事件id的实例才生效
        if ((InstanceStatus.SUCCESS.equals(workInstance.getStatus())
            || InstanceStatus.FAIL.equals(workInstance.getStatus())
            || InstanceStatus.BREAK.equals(workInstance.getStatus())) && !Strings.isEmpty(workInstance.getEventId())) {

            // 获取最新的作业流实例
            WorkflowInstanceEntity workflowInstance =
                workService.getWorkFlowInstance(workRunContext.getFlowInstanceId());

            // 获取所有节点实例状态，判断作业流是否执行完毕
            List<WorkInstanceEntity> endNodeInstance = workInstanceRepository
                .findAllByWorkIdAndWorkflowInstanceId(workRunContext.getNodeList(), workRunContext.getFlowInstanceId());
            boolean flowIsOver = endNodeInstance.stream()
                .allMatch(e -> InstanceStatus.FAIL.equals(e.getStatus()) || InstanceStatus.SUCCESS.equals(e.getStatus())
                    || InstanceStatus.ABORT.equals(e.getStatus()) || InstanceStatus.BREAK.equals(e.getStatus()));
            if (flowIsOver) {

                // 作业流节点都跑完了，判断成功失败
                boolean flowIsError = endNodeInstance.stream().anyMatch(e -> InstanceStatus.FAIL.equals(e.getStatus()));
                boolean flowIsSuccess =
                    endNodeInstance.stream().allMatch(e -> InstanceStatus.SUCCESS.equals(e.getStatus()));
                if (flowIsError) {
                    workflowInstance.setStatus(InstanceStatus.FAIL);
                } else if (flowIsSuccess) {
                    workflowInstance.setStatus(InstanceStatus.SUCCESS);
                } else {
                    workflowInstance.setStatus(InstanceStatus.ABORT);
                }
                workflowInstance.setExecEndDateTime(new Date());
                workflowInstance.setDuration(
                    (System.currentTimeMillis() - workflowInstance.getExecStartDateTime().getTime()) / 1000);
                workflowInstanceRepository.saveAndFlush(workflowInstance);

                // 基线告警，作业流成功、失败、运行结束发送消息
                if (InstanceType.AUTO.equals(workflowInstance.getInstanceType())) {
                    if (flowIsError) {
                        alarmService.sendWorkflowMessage(workflowInstance, AlarmEventType.RUN_FAIL);
                    } else if (flowIsSuccess) {
                        alarmService.sendWorkflowMessage(workflowInstance, AlarmEventType.RUN_SUCCESS);
                    }
                    alarmService.sendWorkflowMessage(workflowInstance, AlarmEventType.RUN_END);
                }
            } else {

                // 作业流没有跑完，继续触发下游作业
                List<String> sonNodes =
                    WorkflowUtils.getSonNodes(workRunContext.getNodeMapping(), workRunContext.getWorkId());
                List<WorkEntity> sonNodeWorks = workRepository.findAllByWorkIds(sonNodes);
                for (WorkEntity work : sonNodeWorks) {
                    WorkInstanceEntity sonWorkInstance = workInstanceRepository
                        .findByWorkIdAndWorkflowInstanceId(work.getId(), workRunContext.getFlowInstanceId());

                    // 封装WorkRunContext，通过是否有versionId，判断是调度中作业还是普通手动运行的作业
                    WorkRunContext sonWorkRunContext;
                    if (Strings.isEmpty(sonWorkInstance.getVersionId())) {
                        WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();
                        sonWorkRunContext = WorkflowUtils.genWorkRunContext(sonWorkInstance.getId(), EventType.WORKFLOW,
                            work, workConfig);
                    } else {
                        VipWorkVersionEntity workVersion = vipWorkVersionRepository.findById(work.getVersionId()).get();
                        sonWorkRunContext = WorkflowUtils.genWorkRunContext(sonWorkInstance.getId(), EventType.WORKFLOW,
                            work, workVersion);
                        sonWorkRunContext.setVersionId(sonWorkInstance.getVersionId());
                    }
                    sonWorkRunContext.setDagEndList(workRunContext.getDagEndList());
                    sonWorkRunContext.setDagStartList(workRunContext.getDagStartList());
                    sonWorkRunContext.setFlowInstanceId(workRunContext.getFlowInstanceId());
                    sonWorkRunContext.setNodeMapping(workRunContext.getNodeMapping());
                    sonWorkRunContext.setNodeList(workRunContext.getNodeList());

                    // 调用调度器触发子作业
                    workRunJobFactory.run(sonWorkRunContext);
                }
            }
        }

        // 当前作业运行完毕
        return InstanceStatus.FINISHED;
    }
}
