//package com.isxcode.spark.modules.work.run.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.isxcode.spark.api.agent.constants.AgentType;
//import com.isxcode.spark.api.agent.constants.FlinkAgentUrl;
//import com.isxcode.spark.api.agent.req.flink.*;
//import com.isxcode.spark.api.agent.res.flink.GetWorkInfoRes;
//import com.isxcode.spark.api.agent.res.flink.GetWorkLogRes;
//import com.isxcode.spark.api.agent.res.flink.SubmitWorkRes;
//import com.isxcode.spark.api.api.constants.PathConstants;
//import com.isxcode.spark.api.cluster.constants.ClusterNodeStatus;
//import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
//import com.isxcode.spark.api.work.constants.WorkLog;
//import com.isxcode.spark.api.work.constants.WorkType;
//import com.isxcode.spark.api.work.dto.ClusterConfig;
//import com.isxcode.spark.api.work.dto.JarJobConfig;
//import com.isxcode.spark.api.work.res.RunWorkRes;
//import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
//import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
//import com.isxcode.spark.backend.api.base.pojos.BaseResponse;
//import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
//import com.isxcode.spark.common.locker.Locker;
//import com.isxcode.spark.common.utils.aes.AesUtils;
//import com.isxcode.spark.common.utils.http.HttpUrlUtils;
//import com.isxcode.spark.common.utils.http.HttpUtils;
//import com.isxcode.spark.common.utils.path.PathUtils;
//import com.isxcode.spark.modules.alarm.service.AlarmService;
//import com.isxcode.spark.modules.cluster.entity.ClusterEntity;
//import com.isxcode.spark.modules.cluster.entity.ClusterNodeEntity;
//import com.isxcode.spark.modules.cluster.mapper.ClusterNodeMapper;
//import com.isxcode.spark.modules.cluster.repository.ClusterNodeRepository;
//import com.isxcode.spark.modules.cluster.repository.ClusterRepository;
//import com.isxcode.spark.modules.datasource.service.DatasourceService;
//import com.isxcode.spark.modules.file.entity.FileEntity;
//import com.isxcode.spark.modules.file.repository.FileRepository;
//import com.isxcode.spark.modules.func.mapper.FuncMapper;
//import com.isxcode.spark.modules.func.repository.FuncRepository;
//import com.isxcode.spark.modules.work.entity.WorkConfigEntity;
//import com.isxcode.spark.modules.work.entity.WorkEntity;
//import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
//import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
//import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
//import com.isxcode.spark.modules.work.repository.WorkRepository;
//import com.isxcode.spark.modules.work.run.WorkExecutor;
//import com.isxcode.spark.modules.work.run.WorkRunContext;
//import com.isxcode.spark.modules.work.sql.SqlFunctionService;
//import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
//import com.jcraft.jsch.JSchException;
//import com.jcraft.jsch.SftpException;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.logging.log4j.util.Strings;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import com.isxcode.spark.api.instance.constants.InstanceStatus;
//import com.isxcode.spark.modules.work.entity.WorkEventEntity;
//import com.isxcode.spark.modules.work.repository.WorkEventRepository;
//import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
//import com.isxcode.spark.modules.work.repository.VipWorkVersionRepository;
//import org.quartz.Scheduler;
//
//import java.io.File;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static com.isxcode.spark.common.utils.ssh.SshUtils.scpJar;
//
//@Service
//@Slf4j
//public class FlinkJarExecutor extends WorkExecutor {
//
//    private final WorkInstanceRepository workInstanceRepository;
//
//    private final ClusterRepository clusterRepository;
//
//    private final ClusterNodeRepository clusterNodeRepository;
//
//    private final WorkRepository workRepository;
//
//    private final WorkConfigRepository workConfigRepository;
//
//    private final Locker locker;
//
//    private final HttpUrlUtils httpUrlUtils;
//
//    private final FuncRepository funcRepository;
//
//    private final FuncMapper funcMapper;
//
//    private final ClusterNodeMapper clusterNodeMapper;
//    private final WorkEventRepository workEventRepository;
//
//
//    private final AesUtils aesUtils;
//
//    private final IsxAppProperties isxAppProperties;
//
//    private final FileRepository fileRepository;
//
//    private final DatasourceService datasourceService;
//
//    private final SqlFunctionService sqlFunctionService;
//
//    public FlinkJarExecutor(WorkInstanceRepository workInstanceRepository, ClusterRepository clusterRepository,
//        ClusterNodeRepository clusterNodeRepository, WorkflowInstanceRepository workflowInstanceRepository,
//        WorkRepository workRepository, WorkConfigRepository workConfigRepository, Locker locker,
//        HttpUrlUtils httpUrlUtils, FuncRepository funcRepository, FuncMapper funcMapper,
//        ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, IsxAppProperties isxAppProperties,
//        FileRepository fileRepository, DatasourceService datasourceService, AlarmService alarmService,
//        SqlFunctionService sqlFunctionService, WorkEventRepository workEventRepository, Scheduler scheduler,
//        WorkRunJobFactory workRunJobFactory, VipWorkVersionRepository vipWorkVersionRepository) {
//
//        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
//            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository);
//        this.workInstanceRepository = workInstanceRepository;
//        this.clusterRepository = clusterRepository;
//        this.clusterNodeRepository = clusterNodeRepository;
//        this.workRepository = workRepository;
//        this.workConfigRepository = workConfigRepository;
//        this.locker = locker;
//        this.httpUrlUtils = httpUrlUtils;
//        this.funcRepository = funcRepository;
//        this.funcMapper = funcMapper;
//        this.clusterNodeMapper = clusterNodeMapper;
//        this.aesUtils = aesUtils;
//        this.isxAppProperties = isxAppProperties;
//        this.fileRepository = fileRepository;
//        this.datasourceService = datasourceService;
//        this.sqlFunctionService = sqlFunctionService;
//        this.workEventRepository = workEventRepository;
//    }
//
//    @Override
//    public String getWorkType() {
//        return WorkType.FLINK_JAR;
//    }
//
//    @Override
//    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance, WorkEventEntity workEvent)
//        throws Exception {
//
//        // 获取事件上下文与日志
//        WorkRunContext workEventBody = JSON.parseObject(workEvent.getEventContext(), WorkRunContext.class);
//        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());
//
//        // 步骤1：校验环境、上传Jar/依赖
//        if (processNeverRun(workEvent, 1)) {
//            if (workRunContext.getJarJobConfig() == null) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业配置异常 : 请检查作业配置 \n");
//            }
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始申请资源 \n");
//            if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎未配置 \n");
//            }
//            Optional<ClusterEntity> engineOpt =
//                clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
//            if (!engineOpt.isPresent()) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在 \n");
//            }
//            List<ClusterNodeEntity> nodes =
//                clusterNodeRepository.findAllByClusterIdAndStatus(engineOpt.get().getId(), ClusterNodeStatus.RUNNING);
//            if (nodes.isEmpty()) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群 \n");
//            }
//            ClusterNodeEntity engineNode = nodes.get(new Random().nextInt(nodes.size()));
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("申请资源完成，激活节点:【")
//                .append(engineNode.getName()).append("】\n");
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成 \n");
//            workInstance = updateInstance(workInstance, logBuilder);
//
//            // 上传Jar与依赖
//            JarJobConfig jarJobConfig = workRunContext.getJarJobConfig();
//            Optional<FileEntity> fileEntityOptional = fileRepository.findById(jarJobConfig.getJarFileId());
//            if (!fileEntityOptional.isPresent()) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Jar文件异常 : 请检查文件是否存在 \n");
//            }
//            FileEntity jarFile = fileEntityOptional.get();
//            ScpFileEngineNodeDto scp = clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
//            scp.setPasswd(aesUtils.decrypt(scp.getPasswd()));
//            String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
//                + File.separator + engineNode.getTenantId();
//            try {
//                scpJar(scp, fileDir + File.separator + jarFile.getId(),
//                    engineNode.getAgentHomePath() + "/zhiqingyun-agent/file/" + jarFile.getId() + ".jar");
//            } catch (JSchException | SftpException | InterruptedException | IOException e) {
//                log.debug(e.getMessage());
//                throw new WorkRunException(
//                    LocalDateTime.now() + WorkLog.ERROR_INFO + "自定义作业jar文件上传失败，请检查文件是否上传或者重新上传\n" + e.getMessage());
//            }
//            if (workRunContext.getLibConfig() != null) {
//                List<FileEntity> libFile = fileRepository.findAllById(workRunContext.getLibConfig());
//                libFile.forEach(e -> {
//                    try {
//                        scpJar(scp, fileDir + File.separator + e.getId(),
//                            engineNode.getAgentHomePath() + "/zhiqingyun-agent/file/" + e.getId() + ".jar");
//                    } catch (JSchException | SftpException | InterruptedException | IOException ex) {
//                        throw new WorkRunException(
//                            LocalDateTime.now() + WorkLog.ERROR_INFO + "自定义依赖jar文件上传失败，请检查文件是否上传或者重新上传\n");
//                    }
//                });
//            }
//            // 保存上下文
//            workEvent.setEventContext(JSON.toJSONString(workEventBody));
//            workEventRepository.saveAndFlush(workEvent);
//        }
//
//        // 步骤2：提交作业，保存 appId
//        if (processNeverRun(workEvent, 2)) {
//            Optional<ClusterEntity> engineOpt =
//                clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
//            List<ClusterNodeEntity> nodes =
//                clusterNodeRepository.findAllByClusterIdAndStatus(engineOpt.get().getId(), ClusterNodeStatus.RUNNING);
//            ClusterNodeEntity engineNode = nodes.get(new Random().nextInt(nodes.size()));
//
//            SubmitWorkReq submitJobReq = new SubmitWorkReq();
//            submitJobReq.setClusterType(engineOpt.get().getClusterType());
//            submitJobReq.setAgentHomePath(engineNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME);
//            submitJobReq.setFlinkHome(engineNode.getFlinkHomePath());
//            submitJobReq.setWorkInstanceId(workInstance.getId());
//            submitJobReq.setWorkType(workRunContext.getWorkType());
//            submitJobReq.setWorkId(workInstance.getWorkId());
//
//            JarJobConfig jarJobConfig = workRunContext.getJarJobConfig();
//            submitJobReq.setPluginReq(PluginReq.builder().args(jarJobConfig.getArgs()).build());
//            submitJobReq.setFlinkSubmit(FlinkSubmit.builder().appName(jarJobConfig.getAppName())
//                .entryClass(jarJobConfig.getMainClass()).appResource(jarJobConfig.getJarFileId() + ".jar")
//                .conf(workRunContext.getClusterConfig().getFlinkConfig()).build());
//            if (workRunContext.getLibConfig() != null) {
//                submitJobReq.setLibConfig(workRunContext.getLibConfig());
//            }
//
//            Integer lock = locker.lock("REQUEST_" + workInstance.getId());
//            try {
//                BaseResponse<?> baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(),
//                    engineNode.getAgentPort(), FlinkAgentUrl.SUBMIT_WORK_URL), submitJobReq, BaseResponse.class);
//                if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())
//                    || baseResponse.getData() == null) {
//                    throw new WorkRunException(
//                        LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
//                }
//                SubmitWorkRes submitJobRes =
//                    JSON.parseObject(JSON.toJSONString(baseResponse.getData()), SubmitWorkRes.class);
//                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("提交作业成功 : ")
//                    .append(submitJobRes.getAppId()).append("\n");
//                workInstance.setSparkStarRes(JSON.toJSONString(submitJobRes));
//                workInstance = updateInstance(workInstance, logBuilder);
//
//                workEventBody.setContainerId(submitJobRes.getAppId());
//                workEventBody.setCurrentStatus("");
//                workEvent.setEventContext(JSON.toJSONString(workEventBody));
//                workEventRepository.saveAndFlush(workEvent);
//            } finally {
//                locker.unlock(lock);
//            }
//            return InstanceStatus.RUNNING;
//        }
//
//        // 步骤3：查询状态（一次心跳）
//        Optional<ClusterEntity> engineOpt =
//            clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
//        List<ClusterNodeEntity> nodes =
//            clusterNodeRepository.findAllByClusterIdAndStatus(engineOpt.get().getId(), ClusterNodeStatus.RUNNING);
//        ClusterNodeEntity engineNode = nodes.get(new Random().nextInt(nodes.size()));
//        BaseResponse<?> baseResponse;
//        GetWorkInfoReq jobInfoReq =
//            GetWorkInfoReq.builder().agentHome(engineNode.getAgentHomePath()).flinkHome(engineNode.getFlinkHomePath())
//                .appId(workEventBody.getContainerId()).clusterType(engineOpt.get().getClusterType()).build();
//        GetWorkInfoRes getJobInfoRes;
//        try {
//            baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
//                FlinkAgentUrl.GET_WORK_INFO_URL), jobInfoReq, BaseResponse.class);
//        } catch (Exception e) {
//            throw new WorkRunException(
//                LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业状态异常 : " + e.getMessage() + "\n");
//        }
//        if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
//            throw new WorkRunException(
//                LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业状态异常 : " + baseResponse.getMsg() + "\n");
//        }
//        getJobInfoRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetWorkInfoRes.class);
//        workInstance.setSparkStarRes(JSON.toJSONString(getJobInfoRes));
//        if (!Objects.equals(workEventBody.getCurrentStatus(), getJobInfoRes.getStatus())) {
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:")
//                .append(getJobInfoRes.getStatus()).append("\n");
//            workEventBody.setCurrentStatus(getJobInfoRes.getStatus());
//            workEvent.setEventContext(JSON.toJSONString(workEventBody));
//            workEventRepository.saveAndFlush(workEvent);
//        }
//        workInstance = updateInstance(workInstance, logBuilder);
//        List<String> running = Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING", "PENDING",
//            "TERMINATING", "INITIALIZING", "RESTARTING");
//        if (running.contains(getJobInfoRes.getStatus().toUpperCase())) {
//            return InstanceStatus.RUNNING;
//        }
//
//        // 步骤4：获取日志并完成收尾
//        GetWorkLogReq getJobLogReq =
//            GetWorkLogReq.builder().agentHomePath(engineNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME)
//                .appId(workEventBody.getContainerId()).workInstanceId(workInstance.getId())
//                .flinkHome(engineNode.getFlinkHomePath()).workStatus(getJobInfoRes.getStatus())
//                .clusterType(engineOpt.get().getClusterType()).build();
//        baseResponse = HttpUtils.doPost(
//            httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), FlinkAgentUrl.GET_WORK_LOG_URL),
//            getJobLogReq, BaseResponse.class);
//        if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
//            throw new WorkRunException(
//                LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业日志异常 : " + baseResponse.getMsg() + "\n");
//        }
//        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("日志保存成功 \n");
//        GetWorkLogRes getWorkLogRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetWorkLogRes.class);
//        if (getWorkLogRes != null) {
//            workInstance.setYarnLog(getWorkLogRes.getLog());
//        }
//        updateInstance(workInstance, logBuilder);
//
//        if ("KILLED".equalsIgnoreCase(getJobInfoRes.getStatus())
//            || "TERMINATED".equalsIgnoreCase(getJobInfoRes.getStatus())) {
//            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业运行中止\n");
//        }
//        List<String> success = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED", "OVER");
//        if (success.contains(getJobInfoRes.getStatus().toUpperCase())) {
//            if (AgentType.K8S.equals(engineOpt.get().getClusterType())) {
//                if (Strings.isEmpty(workInstance.getYarnLog())) {
//                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业运行中止\n");
//                } else if (workInstance.getYarnLog().contains("Caused by:")) {
//                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常\n");
//                }
//            }
//            if (AgentType.YARN.equals(engineOpt.get().getClusterType())) {
//                if (workInstance.getYarnLog().contains("Caused by:")) {
//                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常\n");
//                }
//            }
//        } else {
//            if (AgentType.K8S.equals(engineOpt.get().getClusterType())
//                && "ERROR".equalsIgnoreCase(getJobInfoRes.getStatus())) {
//                StopWorkReq stopWorkReq = StopWorkReq.builder().flinkHome(engineNode.getFlinkHomePath())
//                    .appId(workEventBody.getContainerId()).clusterType(engineOpt.get().getClusterType()).build();
//                new RestTemplate().postForObject(httpUrlUtils.genHttpUrl(engineNode.getHost(),
//                    engineNode.getAgentPort(), FlinkAgentUrl.STOP_WORK_URL), stopWorkReq, BaseResponse.class);
//            } else {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常\n");
//            }
//        }
//
//        return InstanceStatus.SUCCESS;
//    }
//
//    @Override
//    protected void abort(WorkInstanceEntity workInstance) throws Exception {
//
//        // 判断作业有没有提交成功
//        locker.lock("REQUEST_" + workInstance.getId());
//        try {
//            workInstance = workInstanceRepository.findById(workInstance.getId()).get();
//            if (!Strings.isEmpty(workInstance.getSparkStarRes())) {
//                RunWorkRes wokRunWorkRes = JSON.parseObject(workInstance.getSparkStarRes(), RunWorkRes.class);
//                if (!Strings.isEmpty(wokRunWorkRes.getAppId())) {
//
//                    // 关闭远程线程
//                    WorkEntity work = workRepository.findById(workInstance.getWorkId()).get();
//                    WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();
//                    ClusterConfig clusterConfig = JSON.parseObject(workConfig.getClusterConfig(), ClusterConfig.class);
//                    List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
//                        .findAllByClusterIdAndStatus(clusterConfig.getClusterId(), ClusterNodeStatus.RUNNING);
//                    if (allEngineNodes.isEmpty()) {
//                        throw new WorkRunException(
//                            LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群 \n");
//                    }
//                    ClusterEntity cluster = clusterRepository.findById(clusterConfig.getClusterId()).get();
//
//                    // 节点选择随机数
//                    ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));
//
//                    StopWorkReq stopWorkReq = StopWorkReq.builder().flinkHome(engineNode.getFlinkHomePath())
//                        .appId(wokRunWorkRes.getAppId()).clusterType(cluster.getClusterType()).build();
//
//                    BaseResponse<?> baseResponse =
//                        new RestTemplate().postForObject(httpUrlUtils.genHttpUrl(engineNode.getHost(),
//                            engineNode.getAgentPort(), FlinkAgentUrl.STOP_WORK_URL), stopWorkReq, BaseResponse.class);
//
//                    if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
//                        throw new IsxAppException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
//                    }
//                } else {
//                    // 先杀死进程
//                    Thread t = WORK_THREAD.get(workInstance.getId());
//                    if (t != null) {
//                        t.interrupt();
//                    }
//                }
//            }
//        } finally {
//            locker.clearLock("REQUEST_" + workInstance.getId());
//        }
//    }
//}
