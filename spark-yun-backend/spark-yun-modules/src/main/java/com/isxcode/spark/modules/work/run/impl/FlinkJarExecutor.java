package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.agent.constants.AgentType;
import com.isxcode.spark.api.agent.constants.FlinkAgentUrl;
import com.isxcode.spark.api.agent.req.flink.*;
import com.isxcode.spark.api.api.constants.PathConstants;
import com.isxcode.spark.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.api.work.res.AgentLinkResponse;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.common.utils.aes.AesUtils;
import com.isxcode.spark.common.utils.path.PathUtils;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.cluster.entity.ClusterEntity;
import com.isxcode.spark.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.spark.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.spark.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.spark.modules.cluster.repository.ClusterRepository;
import com.isxcode.spark.modules.file.entity.FileEntity;
import com.isxcode.spark.modules.file.repository.FileRepository;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.*;
import com.isxcode.spark.modules.work.run.AgentLinkUtils;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.service.WorkService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.isxcode.spark.common.utils.ssh.SshUtils.scpJar;

@Service
@Slf4j
public class FlinkJarExecutor extends WorkExecutor {

    private final ClusterRepository clusterRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final IsxAppProperties isxAppProperties;

    private final FileRepository fileRepository;

    private final AgentLinkUtils agentLinkUtils;

    public FlinkJarExecutor(WorkInstanceRepository workInstanceRepository, ClusterRepository clusterRepository,
        ClusterNodeRepository clusterNodeRepository, WorkflowInstanceRepository workflowInstanceRepository,
        WorkRepository workRepository, WorkConfigRepository workConfigRepository, Locker locker,
        ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, IsxAppProperties isxAppProperties,
        FileRepository fileRepository, AlarmService alarmService, SqlFunctionService sqlFunctionService,
        WorkEventRepository workEventRepository, WorkRunJobFactory workRunJobFactory,
        VipWorkVersionRepository vipWorkVersionRepository, WorkService workService, AgentLinkUtils agentLinkUtils) {

        super(alarmService, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository,
            workService);
        this.clusterRepository = clusterRepository;
        this.clusterNodeRepository = clusterNodeRepository;
        this.clusterNodeMapper = clusterNodeMapper;
        this.aesUtils = aesUtils;
        this.isxAppProperties = isxAppProperties;
        this.fileRepository = fileRepository;
        this.agentLinkUtils = agentLinkUtils;
    }

    @Override
    public String getWorkType() {
        return WorkType.FLINK_JAR;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
        WorkEventEntity workEvent) {

        // 获取日志
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 打印首行日志，防止前端卡顿
        if (workEvent.getEventProcess() == 0) {
            logBuilder.append(startLog("申请计算集群资源开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 申请计算集群资源
        if (workEvent.getEventProcess() == 1) {

            // 检测集群是否配置
            if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
                throw errorLogException("申请计算集群资源异常 : 计算引擎未配置");
            }

            // 检查集群是否存在
            ClusterEntity cluster = clusterRepository.findById(workRunContext.getClusterConfig().getClusterId())
                .orElseThrow(() -> errorLogException("申请计算集群资源异常 : 计算引擎不存在"));

            // 检测集群中是否为空
            List<ClusterNodeEntity> clusterNodes =
                clusterNodeRepository.findAllByClusterIdAndStatus(cluster.getId(), ClusterNodeStatus.RUNNING);
            if (clusterNodes.isEmpty()) {
                throw errorLogException("申请计算集群资源异常 : 集群不存在可用节点，请切换一个集群");
            }

            // 随机选择一个节点，解析请求节点信息
            ClusterNodeEntity agentNode = clusterNodes.get(new Random().nextInt(clusterNodes.size()));
            ScpFileEngineNodeDto scpNode = clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(agentNode);
            scpNode.setPasswd(aesUtils.decrypt(scpNode.getPasswd()));

            // 保存上下文
            workRunContext.setClusterType(cluster.getClusterType());
            workRunContext.setScpNodeInfo(scpNode);
            workRunContext.setAgentNode(agentNode);

            // 保存日志
            logBuilder.append(endLog("申请计算集群资源完成，激活节点: " + agentNode.getName()));
            logBuilder.append(startLog("上传Jar包开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 上传Jar包
        if (workEvent.getEventProcess() == 2) {

            // 获取上下文参数
            ScpFileEngineNodeDto scpNode = workRunContext.getScpNodeInfo();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 检测Jar是否配置
            if (workRunContext.getJarJobConfig() == null) {
                throw errorLogException("上传Jar包异常 : 请检查作业配置");
            }

            // 上传到指定服务器上
            try {
                String jarDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator
                    + "file" + File.separator + agentNode.getTenantId();
                scpJar(scpNode, jarDir + File.separator + workRunContext.getJarJobConfig().getJarFileId(),
                    agentNode.getAgentHomePath() + "/zhiqingyun-agent/file/"
                        + workRunContext.getJarJobConfig().getJarFileId() + ".jar");
            } catch (JSchException | SftpException | InterruptedException | IOException e) {
                log.error(e.getMessage());
                throw errorLogException("上传Jar包异常 : " + e.getMessage());
            }

            // 保存日志
            logBuilder.append(endLog("上传Jar包完成"));
            logBuilder.append(startLog("上传依赖包开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 上传依赖包
        if (workEvent.getEventProcess() == 3) {

            if (workRunContext.getLibConfig() != null) {

                // 获取上下文参数
                ScpFileEngineNodeDto scpNode = workRunContext.getScpNodeInfo();
                ClusterNodeEntity agentNode = workRunContext.getAgentNode();

                // 遍历上传到集群节点
                String libDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator
                    + "file" + File.separator + workInstance.getTenantId();
                List<FileEntity> libFile = fileRepository.findAllById(workRunContext.getLibConfig());
                libFile.forEach(e -> {
                    try {
                        scpJar(scpNode, libDir + File.separator + e.getId(),
                            agentNode.getAgentHomePath() + "/zhiqingyun-agent/file/" + e.getId() + ".jar");
                    } catch (JSchException | SftpException | InterruptedException | IOException ex) {
                        throw errorLogException("上传依赖包异常 : " + ex.getMessage());
                    }
                });
            }

            // 保存日志
            logBuilder.append(endLog("上传依赖包完成"));
            logBuilder.append(startLog("构建请求体开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 构建请求体
        if (workEvent.getEventProcess() == 4) {

            // 获取上下文参数
            String clusterType = workRunContext.getClusterType();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 构造代理请求体
            SubmitWorkReq submitWorkReq = new SubmitWorkReq();
            submitWorkReq.setWorkId(workInstance.getWorkId());
            submitWorkReq.setWorkType(workRunContext.getWorkType());
            submitWorkReq.setWorkInstanceId(workInstance.getId());
            submitWorkReq.setClusterType(clusterType);
            submitWorkReq.setAgentHomePath(agentNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME);
            submitWorkReq.setFlinkHome(agentNode.getFlinkHomePath());

            // 配置依赖包
            if (workRunContext.getLibConfig() != null) {
                submitWorkReq.setLibConfig(workRunContext.getLibConfig());
            }

            // 构建Flink提交请求体
            FlinkSubmit flinkSubmit = FlinkSubmit.builder().appName(workRunContext.getJarJobConfig().getAppName())
                .entryClass(workRunContext.getJarJobConfig().getMainClass())
                .appResource(workRunContext.getJarJobConfig().getJarFileId() + ".jar")
                .conf(workRunContext.getClusterConfig().getFlinkConfig()).build();


            // 构建Flink插件运行请求体
            PluginReq pluginReq = PluginReq.builder().args(workRunContext.getJarJobConfig().getArgs()).build();

            // 保存上下文
            submitWorkReq.setFlinkSubmit(flinkSubmit);
            submitWorkReq.setPluginReq(pluginReq);
            workRunContext.setFlinkSubmitWorkReq(submitWorkReq);

            // 保存日志
            logBuilder.append(endLog("构建请求体完成"));
            workRunContext.getClusterConfig().getFlinkConfig()
                .forEach((k, v) -> logBuilder.append(k).append(":").append(v).append(" \n"));
            logBuilder.append(startLog("提交作业开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 提交作业
        if (workEvent.getEventProcess() == 5) {

            // 获取上下文参数
            SubmitWorkReq submitWorkReq = workRunContext.getFlinkSubmitWorkReq();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 提交作业
            AgentLinkResponse agentLinkResponse =
                agentLinkUtils.getAgentLinkResponse(agentNode, FlinkAgentUrl.SUBMIT_WORK_URL, submitWorkReq);
            logBuilder.append(endLog("提交作业完成 : " + agentLinkResponse.getAppId()));

            // 保存实例
            workInstance.setSparkStarRes(JSON.toJSONString(agentLinkResponse));

            // 保存上下文
            workRunContext.setAppId(agentLinkResponse.getAppId());

            // 保存日志
            logBuilder.append(startLog("监听作业状态"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 监听作业状态
        if (workEvent.getEventProcess() == 6) {

            // 获取上下文参数
            String preStatus = workRunContext.getPreStatus() == null ? "" : workRunContext.getPreStatus();
            String appId = workRunContext.getAppId();
            String clusterType = workRunContext.getClusterType();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 获取作业状态并保存
            GetWorkInfoReq jobInfoReq = GetWorkInfoReq.builder().agentHome(agentNode.getAgentHomePath())
                .flinkHome(agentNode.getFlinkHomePath()).appId(appId).clusterType(clusterType).build();

            // 请求代理
            AgentLinkResponse agentLinkResponse =
                agentLinkUtils.getAgentLinkResponse(agentNode, FlinkAgentUrl.GET_WORK_INFO_URL, jobInfoReq);

            // 如果是yarn的话，FinalStatus是undefine的话，使用status状态
            if (AgentType.YARN.equals(clusterType) && "UNDEFINED".equalsIgnoreCase(agentLinkResponse.getFinalState())
                && !agentLinkResponse.getAppState().isEmpty()) {
                agentLinkResponse.setFinalState(agentLinkResponse.getAppState());
            }

            // 只有作业状态发生了变化，才能更新状态
            if (!preStatus.equals(agentLinkResponse.getFinalState())) {
                logBuilder.append(statusLog("作业当前状态: " + agentLinkResponse.getFinalState()));

                // 立即保存实例
                workInstance.setSparkStarRes(JSON.toJSONString(agentLinkResponse));
                updateInstance(workInstance, logBuilder);

                // 立即保存上下文
                workRunContext.setPreStatus(agentLinkResponse.getFinalState());
                updateWorkEvent(workEvent, workRunContext);
            }

            // 如果是运行中状态，直接返回
            List<String> runningStatus = Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING",
                "PENDING", "TERMINATING", "INITIALIZING", "RESTARTING");
            if (runningStatus.contains(agentLinkResponse.getFinalState().toUpperCase())) {
                return InstanceStatus.RUNNING;
            }

            // 如果是中止，直接退出
            List<String> abortStatus = Arrays.asList("KILLED", "TERMINATED");
            if (abortStatus.contains(agentLinkResponse.getFinalState().toUpperCase())) {
                throw errorLogException("作业运行中止");
            }

            // 其他状态则为运行结束
            logBuilder.append(startLog("保存日志开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 保存日志
        if (workEvent.getEventProcess() == 7) {

            // 获取上下文参数
            String appId = workRunContext.getAppId();
            String clusterType = workRunContext.getClusterType();
            String preStatus = workRunContext.getPreStatus();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 获取日志并保存
            GetWorkLogReq getJobLogReq = GetWorkLogReq.builder()
                .agentHomePath(agentNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME).appId(appId)
                .workInstanceId(workInstance.getId()).flinkHome(agentNode.getFlinkHomePath()).workStatus(preStatus)
                .clusterType(clusterType).build();

            AgentLinkResponse agentLinkResponse =
                agentLinkUtils.getAgentLinkResponse(agentNode, FlinkAgentUrl.GET_WORK_LOG_URL, getJobLogReq);

            workInstance.setYarnLog(agentLinkResponse.getLog());
            logBuilder.append(endLog("保存日志完成"));

            // 作业状态为成功的特殊处理情况
            List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED", "OVER");
            if (successStatus.contains(preStatus.toUpperCase())) {

                // 如果是k8s容器部署需要注意，成功状态需要通过日志中内容判断
                if (AgentType.K8S.equals(clusterType) && (Strings.isEmpty(workInstance.getYarnLog())
                    || workInstance.getYarnLog().contains("Caused by:"))) {
                    throw errorLogException("任务运行异常");
                }
                // 如果是yarn容器部署需要注意,状态是finish但是实际可能异常
                if (AgentType.YARN.equals(clusterType) && workInstance.getYarnLog().contains("Caused by:")) {
                    throw errorLogException("任务运行异常");
                }
            } else {
                // 其他状态为异常
                workRunContext.setPreStatus(InstanceStatus.FAIL);
            }

            // 保存日志
            logBuilder.append(startLog("清理缓存文件开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 清理缓存文件
        if (workEvent.getEventProcess() == 8) {

            // 获取上下文参数
            String appId = workRunContext.getAppId();
            String clusterType = workRunContext.getClusterType();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // k8s作业要关闭作业
            if (AgentType.K8S.equals(clusterType)) {

                // k8s异常主动停止，否则一直重试
                StopWorkReq stopWorkReq = StopWorkReq.builder().flinkHome(agentNode.getFlinkHomePath()).appId(appId)
                    .clusterType(clusterType).build();

                agentLinkUtils.getAgentLinkResponse(agentNode, FlinkAgentUrl.STOP_WORK_URL, stopWorkReq);
            }

            // 保存日志
            logBuilder.append(endLog("清理缓存文件完成"));
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
        if (workEvent.getEventProcess() < 5) {
            return true;
        }

        // 运行完毕
        if (workEvent.getEventProcess() > 6) {
            return false;
        }

        // 如果能获取appId则尝试直接杀死
        WorkRunContext workRunContext = JSON.parseObject(workEvent.getEventContext(), WorkRunContext.class);
        if (!Strings.isEmpty(workRunContext.getAppId())) {

            StopWorkReq stopWorkReq = StopWorkReq.builder().flinkHome(workRunContext.getAgentNode().getFlinkHomePath())
                .appId(workRunContext.getAppId()).clusterType(workRunContext.getClusterType()).build();

            agentLinkUtils.getAgentLinkResponse(workRunContext.getAgentNode(), FlinkAgentUrl.STOP_WORK_URL,
                stopWorkReq);
        }

        // 可以中止
        return true;
    }
}
