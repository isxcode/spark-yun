package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.common.utils.aes.AesUtils;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.spark.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.spark.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.spark.modules.cluster.repository.ClusterRepository;
import com.isxcode.spark.modules.meta.service.MetaColumnLineageService;
import com.isxcode.spark.modules.secret.entity.SecretKeyEntity;
import com.isxcode.spark.modules.secret.repository.SecretKeyRepository;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.*;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.service.WorkService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.work.sql.SqlValueService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
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

    private final SecretKeyRepository secretKeyRepository;

    public BashExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, SqlValueService sqlValueService,
        SqlFunctionService sqlFunctionService, AlarmService alarmService, WorkEventRepository workEventRepository,
        Locker locker, WorkRepository workRepository, WorkRunJobFactory workRunJobFactory,
        WorkConfigRepository workConfigRepository, VipWorkVersionRepository vipWorkVersionRepository,
        ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, ClusterNodeRepository clusterNodeRepository,
        ClusterRepository clusterRepository, WorkService workService, SecretKeyRepository secretKeyRepository,
        MetaColumnLineageService metaColumnLineageService) {

        super(alarmService, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository,
            workService, metaColumnLineageService);
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.clusterNodeMapper = clusterNodeMapper;
        this.aesUtils = aesUtils;
        this.clusterNodeRepository = clusterNodeRepository;
        this.clusterRepository = clusterRepository;
        this.secretKeyRepository = secretKeyRepository;
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

        // 打印首行日志，防止前端卡顿
        if (workEvent.getEventProcess() == 0) {
            logBuilder.append(startLog("检测服务器节点开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检测服务器节点
        if (workEvent.getEventProcess() == 1) {

            // 检查计算是否配置
            if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
                throw errorLogException("检测服务器节点异常 : 计算引擎未配置");
            }

            // 检测集群是否存在
            clusterRepository.findById(workRunContext.getClusterConfig().getClusterId())
                .orElseThrow(() -> errorLogException("检测服务器节点异常 : 计算引擎不存在"));

            // 检查计算节点是否配置
            if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterNodeId())) {
                throw errorLogException("检测服务器节点异常 : 指定运行节点未配置");
            }

            // 检测集群中节点是否存在
            ClusterNodeEntity agentNode =
                clusterNodeRepository.findById(workRunContext.getClusterConfig().getClusterNodeId())
                    .orElseThrow(() -> errorLogException("检测服务器节点异常 : 指定运行节点不存在"));

            // 检查节点状态
            if (!ClusterNodeStatus.RUNNING.equals(agentNode.getStatus())) {
                throw errorLogException("检测服务器节点异常 : 节点状态不可用");
            }

            // 解析请求节点信息
            ScpFileEngineNodeDto scpNode = clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(agentNode);
            scpNode.setPasswd(aesUtils.decrypt(scpNode.getPasswd()));

            // 保存上下文
            workRunContext.setScpNodeInfo(scpNode);
            workRunContext.setAgentNode(agentNode);

            // 保存日志
            logBuilder.append(endLog("检测服务器节点完成"));
            logBuilder.append(startLog("检测Bash脚本开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检测Bash脚本
        if (workEvent.getEventProcess() == 2) {

            // 检测脚本是否为空
            if (Strings.isEmpty(workRunContext.getScript())) {
                throw errorLogException("检测Bash脚本异常 : Bash脚本不能为空");
            }

            // 解析上游参数
            String jsonPathSql = parseJsonPath(workRunContext.getScript(), workInstance);

            // 翻译脚本中的系统变量
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

            // 翻译脚本中的系统函数
            String script = sqlFunctionService.parseSqlFunction(parseValueSql);
            String printScript = script;

            // 解析全局变量
            List<SecretKeyEntity> allKey = secretKeyRepository.findAll();
            for (SecretKeyEntity secretKeyEntity : allKey) {
                script = script.replace("${{ secret." + secretKeyEntity.getKeyName() + " }}",
                    aesUtils.decrypt(secretKeyEntity.getSecretValue()));
                printScript = printScript.replace("${{ secret." + secretKeyEntity.getKeyName() + " }}", "******");
            }

            // 禁用rm指令
            if (Pattern.compile("\\brm\\b", Pattern.CASE_INSENSITIVE).matcher(script).find()) {
                throw errorLogException("检测Bash脚本异常 : Bash脚本中禁止包含rm命令");
            }

            // 保存上下文
            workRunContext.setScript(script);

            // 保存日志
            logBuilder.append(printScript).append("\n");
            logBuilder.append(endLog("检测Bash脚本完成"));
            logBuilder.append(startLog("执行Bash脚本开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 执行Bash脚本
        if (workEvent.getEventProcess() == 3) {

            // 获取上下文参数
            String script = workRunContext.getScript();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();
            ScpFileEngineNodeDto scpNode = workRunContext.getScpNodeInfo();

            try {
                // 上传脚本
                scpText(scpNode, script + "\necho 'zhiqingyun_success'",
                    agentNode.getAgentHomePath() + "/zhiqingyun-agent/works/" + workInstance.getId() + ".sh");

                // 执行命令获取pid
                String executeBashWorkCommand = "source /etc/profile && nohup sh " + agentNode.getAgentHomePath()
                    + "/zhiqingyun-agent/works/" + workInstance.getId() + ".sh >> " + agentNode.getAgentHomePath()
                    + "/zhiqingyun-agent/works/" + workInstance.getId() + ".log 2>&1 & echo $!";
                String pid = executeCommand(scpNode, executeBashWorkCommand, false).replace("\n", "");
                logBuilder.append(endLog("执行Bash脚本完成 pid : " + pid));

                // 保存上下文
                workRunContext.setPid(pid);
            } catch (JSchException | SftpException | InterruptedException | IOException e) {
                log.error(e.getMessage(), e);

                // 优化日志
                throw errorLogException("提交作业异常 : " + e.getMessage());
            }

            // 保存日志
            logBuilder.append(startLog("监听作业状态"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 监听作业状态
        if (workEvent.getEventProcess() == 4) {

            // 获取上下文参数
            String preStatus = workRunContext.getPreStatus() == null ? "" : workRunContext.getPreStatus();
            String pid = workRunContext.getPid();
            ScpFileEngineNodeDto scpNodeInfo = workRunContext.getScpNodeInfo();

            // 获取pid状态
            String pidStatus;
            try {
                String getPidStatusCommand = "ps -p " + pid;
                String pidCommandResult = executeCommand(scpNodeInfo, getPidStatusCommand, false);
                pidStatus = pidCommandResult.contains(pid) ? InstanceStatus.RUNNING : InstanceStatus.FINISHED;
            } catch (JSchException | InterruptedException | IOException e) {
                log.error(e.getMessage(), e);

                // 优化日志
                throw errorLogException("获取pid状态异常 : " + e.getMessage());
            }

            // 如果状态发生变化，则更新实例
            if (!preStatus.equals(pidStatus)) {

                // 更新实例
                logBuilder.append(statusLog("作业当前状态: " + pidStatus));
                updateInstance(workInstance, logBuilder);

                // 更新上下文
                workRunContext.setPreStatus(pidStatus);
                updateWorkEvent(workEvent, workRunContext);
            }

            // 如果是运行中状态，直接返回
            if (InstanceStatus.RUNNING.equals(pidStatus)) {
                return InstanceStatus.RUNNING;
            }

            // 其他状态，均为运行结束
            logBuilder.append(startLog("保存日志和数据开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 保存日志和数据
        if (workEvent.getEventProcess() == 5) {

            // 获取上下文参数
            ScpFileEngineNodeDto scpNodeInfo = workRunContext.getScpNodeInfo();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 获取日志
            String getLogCommand =
                "cat " + agentNode.getAgentHomePath() + "/zhiqingyun-agent/works/" + workInstance.getId() + ".log";
            String logCommand;
            try {
                logCommand = executeCommand(scpNodeInfo, getLogCommand, false);
            } catch (JSchException | InterruptedException | IOException e) {
                throw errorLogException("保存日志和数据异常 : " + e.getMessage());
            }

            // 解析日志和结果并保存
            String backStr = logCommand.replace("zhiqingyun_success", "");
            workInstance.setYarnLog(backStr);
            if (backStr.endsWith("\n\n")) {
                workInstance.setResultData(backStr.substring(0, backStr.length() - 2));
            } else {
                workInstance.setResultData(backStr);
            }

            // 如果日志不包含关键字则为异常
            if (!logCommand.contains("zhiqingyun_success")) {
                workRunContext.setPreStatus(InstanceStatus.FAIL);
            }

            // 保存日志
            logBuilder.append(endLog("保存日志和数据完成"));
            logBuilder.append(startLog("清理缓存文件开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 清理缓存文件
        if (workEvent.getEventProcess() == 6) {

            // 获取上下文参数
            ScpFileEngineNodeDto scpNode = workRunContext.getScpNodeInfo();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 删除脚本和日志
            try {
                String clearWorkRunFile = "rm -f " + agentNode.getAgentHomePath() + "/zhiqingyun-agent/works/"
                    + workInstance.getId() + ".log && " + "rm -f " + agentNode.getAgentHomePath()
                    + "/zhiqingyun-agent/works/" + workInstance.getId() + ".sh";
                executeCommand(scpNode, clearWorkRunFile, false);
            } catch (JSchException | InterruptedException | IOException e) {
                throw errorLogException("清理缓存文件异常 : " + e.getMessage());
            }

            // 保存日志
            logBuilder.append(endLog("清理执行脚本完成"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 判断状态
        if (InstanceStatus.FAIL.equals(workRunContext.getPreStatus())) {
            throw errorLogException("最终状态为失败");
        }
        return InstanceStatus.SUCCESS;
    }

    @Override
    protected boolean abort(WorkInstanceEntity workInstance, WorkEventEntity workEvent) {

        // 还未提交
        if (workEvent.getEventProcess() < 3) {
            return true;
        }

        // 运行完毕
        if (workEvent.getEventProcess() > 4) {
            return false;
        }

        try {
            // 如果能获取pid则尝试直接杀死
            WorkRunContext workRunContext = JSON.parseObject(workEvent.getEventContext(), WorkRunContext.class);
            if (!Strings.isEmpty(workRunContext.getPid())) {

                // 杀死程序
                String killCommand = "kill -9 " + workRunContext.getPid();
                executeCommand(workRunContext.getScpNodeInfo(), killCommand, false);
            }

            // 可以中止
            return true;
        } catch (JSchException | InterruptedException | IOException e) {
            throw new IsxAppException(e.getMessage());
        }

    }

}
