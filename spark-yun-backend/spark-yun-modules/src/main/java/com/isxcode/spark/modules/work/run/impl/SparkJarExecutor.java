package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.agent.constants.AgentType;
import com.isxcode.spark.api.agent.constants.SparkAgentUrl;
import com.isxcode.spark.api.agent.req.spark.*;
import com.isxcode.spark.api.agent.res.spark.GetWorkStderrLogRes;
import com.isxcode.spark.api.agent.res.spark.GetWorkStdoutLogRes;
import com.isxcode.spark.api.api.constants.PathConstants;
import com.isxcode.spark.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.api.work.dto.ClusterConfig;
import com.isxcode.spark.api.work.res.RunWorkRes;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.backend.api.base.pojos.BaseResponse;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.common.utils.aes.AesUtils;
import com.isxcode.spark.common.utils.http.HttpUrlUtils;
import com.isxcode.spark.common.utils.http.HttpUtils;
import com.isxcode.spark.common.utils.path.PathUtils;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.cluster.entity.ClusterEntity;
import com.isxcode.spark.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.spark.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.spark.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.spark.modules.cluster.repository.ClusterRepository;
import com.isxcode.spark.modules.file.entity.FileEntity;
import com.isxcode.spark.modules.file.repository.FileRepository;
import com.isxcode.spark.modules.work.entity.WorkConfigEntity;
import com.isxcode.spark.modules.work.entity.WorkEntity;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.*;
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
import org.quartz.Scheduler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.isxcode.spark.common.utils.ssh.SshUtils.scpJar;

@Service
@Slf4j
public class SparkJarExecutor extends WorkExecutor {

    private final WorkInstanceRepository workInstanceRepository;

    private final ClusterRepository clusterRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final WorkRepository workRepository;

    private final WorkConfigRepository workConfigRepository;

    private final IsxAppProperties isxAppProperties;

    private final Locker locker;

    private final HttpUrlUtils httpUrlUtils;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final FileRepository fileRepository;

    public SparkJarExecutor(WorkInstanceRepository workInstanceRepository, ClusterRepository clusterRepository,
        ClusterNodeRepository clusterNodeRepository, WorkflowInstanceRepository workflowInstanceRepository,
        WorkRepository workRepository, WorkConfigRepository workConfigRepository, IsxAppProperties isxAppProperties,
        Locker locker, HttpUrlUtils httpUrlUtils, ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils,
        FileRepository fileRepository, AlarmService alarmService, SqlFunctionService sqlFunctionService,
        Scheduler scheduler, WorkEventRepository workEventRepository, WorkRunJobFactory workRunJobFactory,
        VipWorkVersionRepository vipWorkVersionRepository, WorkService workService) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository,
            workService);
        this.workInstanceRepository = workInstanceRepository;
        this.clusterRepository = clusterRepository;
        this.clusterNodeRepository = clusterNodeRepository;
        this.workRepository = workRepository;
        this.workConfigRepository = workConfigRepository;
        this.isxAppProperties = isxAppProperties;
        this.locker = locker;
        this.httpUrlUtils = httpUrlUtils;
        this.clusterNodeMapper = clusterNodeMapper;
        this.aesUtils = aesUtils;
        this.fileRepository = fileRepository;
    }

    @Override
    public String getWorkType() {
        return WorkType.SPARK_JAR;
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

        // 上传自定义依赖
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
            submitWorkReq.setWorkId(workRunContext.getWorkId());
            submitWorkReq.setWorkType(WorkType.SPARK_JAR);
            submitWorkReq.setWorkInstanceId(workInstance.getId());
            submitWorkReq.setSparkHomePath(agentNode.getSparkHomePath());
            submitWorkReq.setAgentHomePath(agentNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME);
            submitWorkReq.setClusterType(clusterType);
            submitWorkReq.setArgs(workRunContext.getJarJobConfig().getArgs());

            // 配置依赖包
            if (workRunContext.getLibConfig() != null) {
                submitWorkReq.setLibConfig(workRunContext.getLibConfig());
            }

            // 构建Spark提交请求体
            SparkSubmit sparkSubmit =
                SparkSubmit.builder().verbose(true).mainClass(workRunContext.getJarJobConfig().getMainClass())
                    .appResource(workRunContext.getJarJobConfig().getJarFileId() + ".jar")
                    .appName(workRunContext.getJarJobConfig().getAppName())
                    .conf(genSparkSubmitConfig(workRunContext.getClusterConfig().getSparkConfig())).build();

            // 构建Spark插件运行请求体
            PluginReq pluginReq =
                PluginReq.builder().sparkConfig(workRunContext.getClusterConfig().getSparkConfig()).build();

            // 保存上下文
            submitWorkReq.setPluginReq(pluginReq);
            submitWorkReq.setSparkSubmit(sparkSubmit);
            workRunContext.setSubmitWorkReq(submitWorkReq);

            // 保存日志
            logBuilder.append(endLog("构建请求体完成"));
            workRunContext.getClusterConfig().getSparkConfig()
                .forEach((k, v) -> logBuilder.append(k).append(":").append(v).append(" \n"));
            logBuilder.append(startLog("提交作业开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 提交作业
        if (workEvent.getEventProcess() == 5) {

            // 获取上下文参数
            SubmitWorkReq submitWorkReq = workRunContext.getSubmitWorkReq();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            try {
                // 提交作业
                BaseResponse<?> baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(agentNode.getHost(),
                    agentNode.getAgentPort(), SparkAgentUrl.SUBMIT_WORK_URL), submitWorkReq, BaseResponse.class);
                if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())
                    || baseResponse.getData() == null) {
                    throw errorLogException("提交作业异常 : " + baseResponse.getMsg());
                }

                // 获取appId
                RunWorkRes submitWorkRes =
                    JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
                logBuilder.append(endLog("提交作业完成 : " + submitWorkRes.getAppId()));

                // 保存实例
                workInstance.setSparkStarRes(JSON.toJSONString(submitWorkRes));

                // 保存上下文
                workRunContext.setAppId(submitWorkRes.getAppId());
            } catch (ResourceAccessException e) {
                log.error(e.getMessage(), e);
                throw errorLogException("提交作业异常 : " + e.getMessage());
            } catch (HttpServerErrorException e1) {
                log.error(e1.getMessage(), e1);
                if (HttpStatus.BAD_GATEWAY.value() == e1.getRawStatusCode()) {
                    throw errorLogException("提交作业异常 : 无法访问节点服务器,请检查服务器防火墙或者计算集群");
                }
                throw errorLogException("提交作业异常 : " + e1.getMessage());
            }

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
            GetWorkStatusReq getWorkStatusReq = GetWorkStatusReq.builder().appId(appId).clusterType(clusterType)
                .sparkHomePath(agentNode.getSparkHomePath()).build();
            BaseResponse<?> baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(agentNode.getHost(),
                agentNode.getAgentPort(), SparkAgentUrl.GET_WORK_STATUS_URL), getWorkStatusReq, BaseResponse.class);
            if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                throw errorLogException("获取作业状态异常 : " + baseResponse.getMsg());
            }

            // 解析作业运行状态
            RunWorkRes workStatusRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);

            // 只有作业状态发生了变化，才能更新状态
            if (!preStatus.equals(workStatusRes.getAppStatus())) {
                logBuilder.append(statusLog("作业当前状态: " + workStatusRes.getAppStatus()));

                // 立即保存实例
                workInstance.setSparkStarRes(JSON.toJSONString(workStatusRes));
                updateInstance(workInstance, logBuilder);

                // 立即保存上下文
                workRunContext.setPreStatus(workStatusRes.getAppStatus());
                updateWorkEvent(workEvent, workRunContext);
            }

            // 如果是运行中状态，直接返回
            List<String> runningStatus =
                Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING", "PENDING");
            if (runningStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {
                return InstanceStatus.RUNNING;
            }

            // 如果是中止，直接退出
            List<String> abortStatus = Arrays.asList("KILLED", "TERMINATING");
            if (abortStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {
                throw errorLogException("作业运行中止");
            }

            // 其他状态则为运行结束
            logBuilder.append(startLog("保存日志和数据开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 保存日志和数据
        if (workEvent.getEventProcess() == 7) {

            // 获取上下文参数
            String appId = workRunContext.getAppId();
            String clusterType = workRunContext.getClusterType();
            String preStatus = workRunContext.getPreStatus();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 获取日志并保存
            GetWorkStderrLogReq getWorkStderrLogReq = GetWorkStderrLogReq.builder().appId(appId)
                .clusterType(clusterType).sparkHomePath(agentNode.getSparkHomePath()).build();
            BaseResponse<?> baseResponse =
                HttpUtils.doPost(httpUrlUtils.genHttpUrl(agentNode.getHost(), agentNode.getAgentPort(),
                    SparkAgentUrl.GET_WORK_STDERR_LOG_URL), getWorkStderrLogReq, BaseResponse.class);

            if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                throw errorLogException("保存日志异常 : " + baseResponse.getMsg());
            }

            // 解析日志并保存
            GetWorkStderrLogRes yagGetLogRes =
                JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetWorkStderrLogRes.class);
            workInstance.setYarnLog(yagGetLogRes.getLog());
            logBuilder.append(endLog("保存日志完成"));

            // 如果运行成功，还要继续保存数据
            List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");
            if (successStatus.contains(preStatus.toUpperCase())) {

                // 获取数据
                GetWorkStdoutLogReq getWorkStdoutLogReq = GetWorkStdoutLogReq.builder().appId(appId)
                    .clusterType(clusterType).sparkHomePath(agentNode.getSparkHomePath()).build();
                baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(agentNode.getHost(), agentNode.getAgentPort(),
                    SparkAgentUrl.GET_CUSTOM_WORK_STDOUT_LOG_URL), getWorkStdoutLogReq, BaseResponse.class);
                if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                    throw errorLogException("保存数据异常 : " + baseResponse.getErr());
                }

                // 解析数据并保存
                workInstance.setResultData(
                    JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetWorkStdoutLogRes.class).getLog());

                // 保存日志
                logBuilder.append(endLog("保存数据完成"));
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
                StopWorkReq stopWorkReq = StopWorkReq.builder().appId(appId).clusterType(AgentType.K8S)
                    .sparkHomePath(agentNode.getAgentHomePath()).agentHomePath(agentNode.getAgentHomePath()).build();
                HttpUtils.doPost(
                    httpUrlUtils.genHttpUrl(agentNode.getHost(), agentNode.getPort(), SparkAgentUrl.STOP_WORK_URL),
                    stopWorkReq, BaseResponse.class);
            }

            // 保存日志
            logBuilder.append(endLog("清理缓存文件完成"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 判断状态
        if (InstanceStatus.FAIL.equals(workRunContext.getPreStatus())) {
            throw errorLogException("最终状态为异常");
        }
        return InstanceStatus.SUCCESS;
    }

    @Override
    protected void abort(WorkInstanceEntity workInstance) {

        // 判断作业有没有提交成功
        locker.lock("REQUEST_" + workInstance.getId());
        try {
            workInstance = workInstanceRepository.findById(workInstance.getId()).get();
            if (!Strings.isEmpty(workInstance.getSparkStarRes())) {
                RunWorkRes wokRunWorkRes = JSON.parseObject(workInstance.getSparkStarRes(), RunWorkRes.class);
                if (!Strings.isEmpty(wokRunWorkRes.getAppId())) {
                    // 关闭远程线程
                    WorkEntity work = workRepository.findById(workInstance.getWorkId()).get();
                    WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();
                    ClusterConfig clusterConfig = JSON.parseObject(workConfig.getClusterConfig(), ClusterConfig.class);
                    List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
                        .findAllByClusterIdAndStatus(clusterConfig.getClusterId(), ClusterNodeStatus.RUNNING);
                    if (allEngineNodes.isEmpty()) {
                        throw errorLogException("申请资源失败 : 集群不存在可用节点，请切换一个集群");
                    }
                    ClusterEntity cluster = clusterRepository.findById(clusterConfig.getClusterId()).get();

                    // 节点选择随机数
                    ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

                    StopWorkReq stopWorkReq = StopWorkReq.builder().appId(wokRunWorkRes.getAppId())
                        .clusterType(cluster.getClusterType()).sparkHomePath(engineNode.getSparkHomePath())
                        .agentHomePath(engineNode.getAgentHomePath()).build();
                    BaseResponse<?> baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(),
                        engineNode.getAgentPort(), SparkAgentUrl.STOP_WORK_URL), stopWorkReq, BaseResponse.class);

                    if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                        throw new IsxAppException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
                    }
                } else {
                    // 先杀死进程
                    WORK_THREAD.get(workInstance.getId()).interrupt();
                }
            }
        } finally {
            locker.clearLock("REQUEST_" + workInstance.getId());
        }
    }

    public Map<String, String> genSparkSubmitConfig(Map<String, String> sparkConfig) {

        // 过滤掉，前缀不包含spark.xxx的配置，spark submit中必须都是spark.xxx
        Map<String, String> sparkSubmitConfig = new HashMap<>();
        sparkConfig.forEach((k, v) -> {
            if (k.startsWith("spark")) {
                sparkSubmitConfig.put(k, v);
            }
        });
        return sparkSubmitConfig;
    }
}
