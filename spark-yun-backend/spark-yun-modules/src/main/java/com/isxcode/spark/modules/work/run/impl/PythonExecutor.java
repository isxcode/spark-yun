package com.isxcode.spark.modules.work.run.impl;

import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.WorkLog;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
import com.isxcode.spark.common.utils.aes.AesUtils;
import com.isxcode.spark.common.utils.ssh.SshUtils;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.cluster.entity.ClusterEntity;
import com.isxcode.spark.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.spark.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.spark.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.spark.modules.cluster.repository.ClusterRepository;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.work.sql.SqlValueService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.entity.VipWorkVersionEntity;
import com.isxcode.spark.modules.work.repository.VipWorkVersionRepository;
import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
import com.isxcode.spark.modules.work.repository.WorkRepository;
import com.isxcode.spark.common.locker.Locker;
import org.quartz.Scheduler;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.isxcode.spark.common.utils.ssh.SshUtils.executeCommand;
import static com.isxcode.spark.common.utils.ssh.SshUtils.scpText;

@Service
@Slf4j
public class PythonExecutor extends WorkExecutor {

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final ClusterRepository clusterRepository;

    private final SqlValueService sqlValueService;

    private final SqlFunctionService sqlFunctionService;

    private final WorkEventRepository workEventRepository;

    private final Scheduler scheduler;

    private final WorkRunJobFactory workRunJobFactory;

    private final VipWorkVersionRepository vipWorkVersionRepository;

    private final WorkConfigRepository workConfigRepository;

    private final WorkRepository workRepository;

    private final Locker locker;

    public PythonExecutor(WorkInstanceRepository workInstanceRepository,
                          WorkflowInstanceRepository workflowInstanceRepository, ClusterNodeRepository
                              clusterNodeRepository,
                          ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, ClusterRepository clusterRepository,
                          SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService
                              alarmService, WorkEventRepository workEventRepository, Scheduler scheduler,
                          WorkRunJobFactory workRunJobFactory, VipWorkVersionRepository vipWorkVersionRepository,
                          WorkConfigRepository workConfigRepository, WorkRepository workRepository, Locker locker) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository, workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository);
        this.clusterNodeRepository = clusterNodeRepository;
        this.clusterNodeMapper = clusterNodeMapper;
        this.aesUtils = aesUtils;
        this.clusterRepository = clusterRepository;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.workEventRepository = workEventRepository;
        this.scheduler = scheduler;
        this.workRunJobFactory = workRunJobFactory;
        this.vipWorkVersionRepository = vipWorkVersionRepository;
        this.workConfigRepository = workConfigRepository;
        this.workRepository = workRepository;
        this.locker = locker;
    }

    @Override
    public String getWorkType() {
        return WorkType.PYTHON;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance, WorkEventEntity workEvent) {

        // 获取作业运行上下文
        PythonExecutorContext workEventBody = JSON.parseObject(workEvent.getEventContext(), PythonExecutorContext.class);
        if (workEventBody == null) {
            workEventBody = new PythonExecutorContext();
            workEventBody.setWorkRunContext(workRunContext);
        }
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 检查脚本内容和安全性，保存脚本信息
        if (processNeverRun(workEvent, 1)) {

            // 判断执行脚本是否为空
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测脚本内容 \n");
            if (Strings.isEmpty(workRunContext.getScript())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测脚本失败 : PYTHON内容为空不能执行 \n");
            }

            // 禁用rm指令
            if (Pattern.compile("\\brm\\b",
                Pattern.CASE_INSENSITIVE).matcher(workRunContext.getScript()).find()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测语句失败 : PYTHON内容包含rm指令不能执行 \n");
            }

            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("脚本安全检测完成 \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 保存脚本信息
            workEventBody.setScript(workRunContext.getScript());
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }

        // 检查集群配置，保存集群和节点信息
        if (processNeverRun(workEvent, 2)) {

            // 检测计算集群是否存在
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测集群 \n");
            if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 计算引擎未配置 \n");
            }

            // 检查计算集群是否存在
            Optional<ClusterEntity> calculateEngineEntityOptional =
                clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
            if (!calculateEngineEntityOptional.isPresent()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 计算引擎不存在 \n");
            }

            // 检查计算集群节点是否配置
            if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterNodeId())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 指定运行节点未配置 \n");
            }

            // 检测集群中是否有合法节点
            Optional<ClusterNodeEntity> nodeRepositoryOptional =
                clusterNodeRepository.findById(workRunContext.getClusterConfig().getClusterNodeId());
            if (!nodeRepositoryOptional.isPresent()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 指定运行节点不存在 \n");
            }

            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("集群检测完成 \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 保存集群和节点信息
            workEventBody.setClusterEntity(calculateEngineEntityOptional.get());
            workEventBody.setClusterNodeEntity(nodeRepositoryOptional.get());
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }

        // 脚本处理和翻译，保存处理后的脚本
        if (processNeverRun(workEvent, 3)) {

            // 获取原始脚本
            String originalScript = workEventBody.getScript();

            // 解析上游参数
            String jsonPathSql = parseJsonPath(originalScript, workInstance);

            // 翻译脚本中的系统变量
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

            // 翻译脚本中的系统函数
            String script = sqlFunctionService.parseSqlFunction(parseValueSql);
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("Python脚本: \n").append(script).append("\n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 脚本检查通过
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 保存处理后的脚本
            workEventBody.setProcessedScript(script);
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }

        // 上传脚本和执行，保存执行信息
        if (processNeverRun(workEvent, 4)) {

            // 获取集群节点和处理后的脚本
            ClusterNodeEntity clusterNode = workEventBody.getClusterNodeEntity();
            String script = workEventBody.getProcessedScript();

            // 将脚本推送到指定集群节点中
            ScpFileEngineNodeDto scpFileEngineNodeDto =
                clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(clusterNode);
            scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));
            try {
                // 上传脚本
                scpText(scpFileEngineNodeDto, script + "\nprint('zhiqingyun_success')",
                    clusterNode.getAgentHomePath() + "/zhiqingyun-agent/works/" + workInstance.getId() + ".py");

                // 执行命令获取pid
                String executeBashWorkCommand = "source /etc/profile && nohup python3 " +
                    clusterNode.getAgentHomePath()
                    + "/zhiqingyun-agent/works/" + workInstance.getId() + ".py >> " + clusterNode.getAgentHomePath()
                    + "/zhiqingyun-agent/works/" + workInstance.getId() + ".log 2>&1 & echo $!";
                String pid = executeCommand(scpFileEngineNodeDto, executeBashWorkCommand, false).replace("\n", "");

                // 保存pid
                workInstance.setWorkPid(pid);
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("Python作业提交成功，pid:【").append(pid).append("】\n");
                workInstance = updateInstance(workInstance, logBuilder);

                // 保存执行信息
                workEventBody.setPid(pid);
                workEventBody.setScpFileEngineNodeDto(scpFileEngineNodeDto);
                workEvent.setEventContext(JSON.toJSONString(workEventBody));
                workEventRepository.saveAndFlush(workEvent);

            } catch (JSchException | SftpException | InterruptedException | IOException e) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业异常 : " + e.getMessage() + "\n");
            }
        }

        // 监控作业状态，循环检查直到完成
        if (processNeverRun(workEvent, 5)) {

            // 获取执行信息
            String pid = workEventBody.getPid();
            ScpFileEngineNodeDto scpFileEngineNodeDto = workEventBody.getScpFileEngineNodeDto();

            // 提交作业成功后，开始循环判断状态
            String getPidStatusCommand = "ps -p " + pid;
            String oldStatus = "";
            while (true) {

                String pidStatus;
                try {
                    String pidCommandResult = executeCommand(scpFileEngineNodeDto, getPidStatusCommand, false);
                    if (pidCommandResult.contains(pid)) {
                        pidStatus = InstanceStatus.RUNNING;
                    } else {
                        pidStatus = InstanceStatus.FINISHED;
                    }
                } catch (JSchException | InterruptedException | IOException e) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "获取pid状态异常 : " + e.getMessage() + "\n");
                }

                // 状态发生变化，则添加日志状态
                if (!oldStatus.equals(pidStatus)) {
                    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:").append(pidStatus).append("\n");
                    workInstance = updateInstance(workInstance, logBuilder);
                }
                oldStatus = pidStatus;

                if (InstanceStatus.RUNNING.equals(pidStatus)) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new WorkRunException(
                            LocalDateTime.now() + WorkLog.ERROR_INFO + "睡眠线程异常 : " + e.getMessage() + "\n");
                    }
                } else {
                    // 运行结束，获取日志和清理资源
                    ClusterNodeEntity clusterNode = workEventBody.getClusterNodeEntity();

                    // 获取日志
                    String getLogCommand = "cat " + clusterNode.getAgentHomePath() + "/zhiqingyun-agent/works/"
                        + workInstance.getId() + ".log";
                    String logCommand = "";
                    try {
                        logCommand = executeCommand(scpFileEngineNodeDto, getLogCommand, false);
                    } catch (JSchException | InterruptedException | IOException e) {
                        throw new WorkRunException(
                            LocalDateTime.now() + WorkLog.ERROR_INFO + "获取日志异常 : " + e.getMessage() + "\n");
                    }

                    if (!logCommand.contains("zhiqingyun_success")) {
                        throw new WorkRunException(
                            LocalDateTime.now() + WorkLog.ERROR_INFO + "获取日志异常 : " + logCommand + "\n");
                    }

                    // 保存运行日志
                    String backStr = logCommand.replace("zhiqingyun_success", "");
                    workInstance.setYarnLog(backStr);
                    workInstance.setResultData(backStr.substring(0, backStr.length() - 2));
                    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("保存日志成功 \n");
                    updateInstance(workInstance, logBuilder);

                    // 删除脚本和日志
                    try {
                        String clearWorkRunFile = "rm -f " + clusterNode.getAgentHomePath() + "/zhiqingyun-agent/works/"
                            + workInstance.getId() + ".log && " + "rm -f " + clusterNode.getAgentHomePath()
                            + "/zhiqingyun-agent/works/" + workInstance.getId() + ".py";
                        SshUtils.executeCommand(scpFileEngineNodeDto, clearWorkRunFile, false);
                    } catch (JSchException | InterruptedException | IOException e) {
                        log.error("删除运行脚本失败");
                        throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "删除运行脚本失败" + "\n");
                    }

                    // 判断脚本运行成功还是失败
                    if (!logCommand.contains("zhiqingyun_success")) {
                        throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常" + "\n");
                    }

                    // 保存执行结果
                    workEventBody.setLogCommand(logCommand);
                    workEvent.setEventContext(JSON.toJSONString(workEventBody));
                    workEventRepository.saveAndFlush(workEvent);

                    break;
                }
            }
        }

        return InstanceStatus.SUCCESS;
    }

    @Override
    protected void abort(WorkInstanceEntity workInstance) {

        Thread thread = WORK_THREAD.get(workInstance.getId());
        thread.interrupt();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PythonExecutorContext {

        private WorkRunContext workRunContext;

        private String script;

        private ClusterEntity clusterEntity;

        private ClusterNodeEntity clusterNodeEntity;

        private String processedScript;

        private String pid;

        private ScpFileEngineNodeDto scpFileEngineNodeDto;

        private String logCommand;
    }
}
