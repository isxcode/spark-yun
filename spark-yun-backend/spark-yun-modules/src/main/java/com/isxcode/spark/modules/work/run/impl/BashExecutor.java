package com.isxcode.spark.modules.work.run.impl;

import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.WorkLog;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.common.utils.aes.AesUtils;
import com.isxcode.spark.common.utils.ssh.SshUtils;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.cluster.entity.ClusterEntity;
import com.isxcode.spark.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.spark.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.spark.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.spark.modules.cluster.repository.ClusterRepository;
import com.isxcode.spark.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.spark.modules.datasource.repository.DatasourceRepository;
import com.isxcode.spark.modules.datasource.source.DataSourceFactory;
import com.isxcode.spark.modules.secret.repository.SecretKeyRepository;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.*;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.sql.SqlCommentService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.work.sql.SqlValueService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static com.isxcode.spark.common.utils.ssh.SshUtils.*;

@Service
@Slf4j
public class BashExecutor extends WorkExecutor {

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final ClusterRepository clusterRepository;

    private final SqlValueService sqlValueService;

    private final SqlFunctionService sqlFunctionService;

    private final DatasourceRepository datasourceRepository;

    private final SqlCommentService sqlCommentService;

    private final WorkEventRepository workEventRepository;

    private final DataSourceFactory dataSourceFactory;

    private final DatasourceMapper datasourceMapper;

    private final SecretKeyRepository secretKeyRepository;

    public BashExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, DatasourceRepository datasourceRepository,
        SqlCommentService sqlCommentService, SqlValueService sqlValueService, SqlFunctionService sqlFunctionService,
        AlarmService alarmService, DataSourceFactory dataSourceFactory, DatasourceMapper datasourceMapper,
        SecretKeyRepository secretKeyRepository, WorkEventRepository workEventRepository, Scheduler scheduler,
        Locker locker, WorkRepository workRepository, WorkRunJobFactory workRunJobFactory,
        WorkConfigRepository workConfigRepository, VipWorkVersionRepository vipWorkVersionRepository,
        ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, ClusterNodeRepository clusterNodeRepository,
        ClusterRepository clusterRepository) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository);
        this.datasourceRepository = datasourceRepository;
        this.sqlCommentService = sqlCommentService;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.dataSourceFactory = dataSourceFactory;
        this.datasourceMapper = datasourceMapper;
        this.secretKeyRepository = secretKeyRepository;
        this.workEventRepository = workEventRepository;
        this.clusterNodeMapper = clusterNodeMapper;
        this.aesUtils = aesUtils;
        this.clusterNodeRepository = clusterNodeRepository;
        this.clusterRepository = clusterRepository;
    }

    @Override
    public String getWorkType() {
        return WorkType.BASH;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
        WorkEventEntity workEvent) {

        // 获取日志
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 首行日志
        if (workEvent.getEventProcess() == 0) {
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测集群 \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检查集群
        if (workEvent.getEventProcess() == 1) {

            // 检查计算集群是否存在
            if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 计算引擎未配置 \n");
            }
            Optional<ClusterEntity> calculateEngineEntityOptional =
                clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
            if (!calculateEngineEntityOptional.isPresent()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 计算引擎不存在  \n");
            }
            if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterNodeId())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 指定运行节点未配置 \n");
            }
            // 检测集群中是否有合法节点
            Optional<ClusterNodeEntity> nodeRepositoryOptional =
                clusterNodeRepository.findById(workRunContext.getClusterConfig().getClusterNodeId());
            if (!nodeRepositoryOptional.isPresent()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 指定运行节点不存在  \n");
            }
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("集群检测正常 \n");

            // 解析节点请求体
            ScpFileEngineNodeDto scpFileEngineNodeDto =
                clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(nodeRepositoryOptional.get());
            scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

            // 保存事件
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测脚本 \n");
            workRunContext.setScpNodeInfo(scpFileEngineNodeDto);
            workRunContext.setAgentHomePath(nodeRepositoryOptional.get().getAgentHomePath());
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 解析Bash脚本
        if (workEvent.getEventProcess() == 2) {

            // 判断执行脚本是否为空
            if (Strings.isEmpty(workRunContext.getScript())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测脚本失败 : BASH内容为空不能执行  \n");
            }

            // 解析上游参数
            String jsonPathSql = parseJsonPath(workRunContext.getScript(), workInstance);

            // 翻译脚本中的系统变量
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

            // 翻译脚本中的系统函数
            String script = sqlFunctionService.parseSqlFunction(parseValueSql);
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("Bash脚本: \n").append(script)
                .append("\n");

            // 禁用rm指令
            if (Pattern.compile("\\brm\\b", Pattern.CASE_INSENSITIVE).matcher(script).find()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测语句失败 : BASH内容包含rm指令不能执行  \n");
            }
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("脚本检测正常 \n");

            // 保存脚本到事件上下文
            workRunContext.setScript(script);

            // 保存事件
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 执行脚本
        if (workEvent.getEventProcess() == 3) {

            String script = workRunContext.getScript();
            String agentHomePath = workRunContext.getAgentHomePath();
            ScpFileEngineNodeDto scpFileEngineNodeDto = workRunContext.getScpNodeInfo();

            String pid;
            try {
                // 上传脚本
                scpText(scpFileEngineNodeDto, script + "\necho 'zhiqingyun_success'",
                    agentHomePath + "/zhiqingyun-agent/works/" + workInstance.getId() + ".sh");

                // 执行命令获取pid
                String executeBashWorkCommand = "source /etc/profile && nohup sh " + agentHomePath
                    + "/zhiqingyun-agent/works/" + workInstance.getId() + ".sh >> " + agentHomePath
                    + "/zhiqingyun-agent/works/" + workInstance.getId() + ".log 2>&1 & echo $!";
                pid = executeCommand(scpFileEngineNodeDto, executeBashWorkCommand, false).replace("\n", "");

                // 保存pid
                workInstance.setWorkPid(pid);
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("BASH作业提交成功，pid:【")
                    .append(pid).append("】\n");
            } catch (JSchException | SftpException | InterruptedException | IOException e) {
                log.debug(e.getMessage(), e);
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业异常 : " + e.getMessage() + "\n");
            }

            // 保存脚本到事件上下文
            workRunContext.setPid(pid);

            // 保存事件
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始监听作业状态 \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 监听状态
        if (workEvent.getEventProcess() == 4) {

            String preStatus = workRunContext.getPreStatus() == null ? "" : workRunContext.getPreStatus();
            String pid = workRunContext.getPid();
            ScpFileEngineNodeDto scpFileEngineNodeDto = workRunContext.getScpNodeInfo();

            // 提交作业成功后，开始循环判断状态
            String getPidStatusCommand = "ps -p " + pid;
            String pidStatus;
            try {
                String pidCommandResult = executeCommand(scpFileEngineNodeDto, getPidStatusCommand, false);
                if (pidCommandResult.contains(pid)) {
                    pidStatus = InstanceStatus.RUNNING;
                } else {
                    pidStatus = InstanceStatus.FINISHED;
                }

                // 如果状态发生变化，则保存日志
                if (!preStatus.equals(pidStatus)) {
                    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:")
                        .append(pidStatus).append("\n");
                    workRunContext.setPreStatus(pidStatus);
                    updateWorkEvent(workEvent, workRunContext);
                    updateInstance(workInstance, logBuilder);
                }
            } catch (JSchException | InterruptedException | IOException e) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "获取pid状态异常 : " + e.getMessage() + "\n");
            }

            // 返回继续下一次调度
            if (InstanceStatus.RUNNING.equals(pidStatus)) {
                return InstanceStatus.RUNNING;
            }

            // 状态发生变化，则添加日志状态
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始保存日志和数据 \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 保存日志和数据
        if (workEvent.getEventProcess() == 5) {

            ScpFileEngineNodeDto scpFileEngineNodeDto = workRunContext.getScpNodeInfo();
            String agentHomePath = workRunContext.getAgentHomePath();

            // 获取日志
            String getLogCommand = "cat " + agentHomePath + "/zhiqingyun-agent/works/" + workInstance.getId() + ".log";
            String logCommand;
            try {
                logCommand = executeCommand(scpFileEngineNodeDto, getLogCommand, false);
            } catch (JSchException | InterruptedException | IOException e) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "获取日志异常 : " + e.getMessage() + "\n");
            }

            // 保存运行日志
            String backStr = logCommand.replace("zhiqingyun_success", "");
            workInstance.setYarnLog(backStr);
            workInstance.setResultData(backStr.substring(0, backStr.length() - 2));
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("保存日志成功 \n");

            // 保存运行日志
            workRunContext.setLog(logCommand);

            // 保存运行结果
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始清理执行文件 \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 清理执行的文件
        if (workEvent.getEventProcess() == 6) {

            ScpFileEngineNodeDto scpFileEngineNodeDto = workRunContext.getScpNodeInfo();
            String agentHomePath = workRunContext.getAgentHomePath();
            String logCommand = workRunContext.getLog();

            // 删除脚本和日志
            try {
                String clearWorkRunFile = "rm -f " + agentHomePath + "/zhiqingyun-agent/works/" + workInstance.getId()
                    + ".log && " + "rm -f " + agentHomePath + "/zhiqingyun-agent/works/" + workInstance.getId() + ".sh";
                SshUtils.executeCommand(scpFileEngineNodeDto, clearWorkRunFile, false);
            } catch (JSchException | InterruptedException | IOException e) {
                log.error("删除运行脚本失败");
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "删除运行脚本失败" + "\n");
            }

            // 判断脚本运行成功还是失败
            if (!logCommand.contains("zhiqingyun_success")) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常" + "\n");
            }

            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("清理执行文件成功 \n");
            updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        return InstanceStatus.SUCCESS;
    }


    @Override
    protected void abort(WorkInstanceEntity workInstance) {

        Thread thread = WORK_THREAD.get(workInstance.getId());
        if (thread != null) {
            thread.interrupt();
        }
    }
}
