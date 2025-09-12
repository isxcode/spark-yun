//package com.isxcode.spark.modules.work.run.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.isxcode.spark.api.agent.constants.AgentType;
//import com.isxcode.spark.api.agent.constants.SparkAgentUrl;
//import com.isxcode.spark.api.agent.req.spark.*;
//import com.isxcode.spark.api.agent.res.spark.GetWorkStderrLogRes;
//import com.isxcode.spark.api.api.constants.PathConstants;
//import com.isxcode.spark.api.cluster.constants.ClusterNodeStatus;
//import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
//import com.isxcode.spark.api.datasource.constants.DatasourceType;
//import com.isxcode.spark.api.work.constants.WorkLog;
//import com.isxcode.spark.api.work.constants.WorkType;
//import com.isxcode.spark.api.instance.constants.InstanceStatus;
//import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
//import com.isxcode.spark.api.work.dto.ClusterConfig;
//import com.isxcode.spark.api.work.dto.DatasourceConfig;
//import com.isxcode.spark.api.work.res.RunWorkRes;
//import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
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
//import com.isxcode.spark.modules.datasource.entity.DatasourceEntity;
//import com.isxcode.spark.modules.datasource.service.DatasourceService;
//import com.isxcode.spark.modules.file.entity.FileEntity;
//import com.isxcode.spark.modules.file.repository.FileRepository;
//import com.isxcode.spark.modules.func.entity.FuncEntity;
//import com.isxcode.spark.modules.func.mapper.FuncMapper;
//import com.isxcode.spark.modules.func.repository.FuncRepository;
//import com.isxcode.spark.modules.work.entity.WorkConfigEntity;
//import com.isxcode.spark.modules.work.entity.WorkEntity;
//import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
//import com.isxcode.spark.modules.work.entity.WorkEventEntity;
//import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
//import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
//import com.isxcode.spark.modules.work.repository.WorkRepository;
//import com.isxcode.spark.modules.work.repository.WorkEventRepository;
//import com.isxcode.spark.modules.work.repository.VipWorkVersionRepository;
//import com.isxcode.spark.modules.work.run.WorkExecutor;
//import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
//import com.isxcode.spark.modules.work.run.WorkRunContext;
//import com.isxcode.spark.modules.work.sql.SqlCommentService;
//import com.isxcode.spark.modules.work.sql.SqlFunctionService;
//import com.isxcode.spark.modules.work.sql.SqlValueService;
//import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
//import com.jcraft.jsch.JSchException;
//import com.jcraft.jsch.SftpException;
//import lombok.extern.slf4j.Slf4j;
//import org.quartz.Scheduler;
//import lombok.Data;
//import lombok.Builder;
//import lombok.NoArgsConstructor;
//import lombok.AllArgsConstructor;
//import org.apache.logging.log4j.util.Strings;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.ResourceAccessException;
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
//public class SyncWorkExecutor extends WorkExecutor {
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
//    private final AesUtils aesUtils;
//
//    private final ClusterNodeMapper clusterNodeMapper;
//
//    private final DatasourceService datasourceService;
//
//    private final IsxAppProperties isxAppProperties;
//
//    private final FuncRepository funcRepository;
//
//    private final FuncMapper funcMapper;
//
//    private final FileRepository fileRepository;
//
//    private final SqlCommentService sqlCommentService;
//
//    private final SqlValueService sqlValueService;
//
//    private final SqlFunctionService sqlFunctionService;
//
//    private final WorkEventRepository workEventRepository;
//
//    private final VipWorkVersionRepository vipWorkVersionRepository;
//
//    private final WorkRunJobFactory workRunJobFactory;
//
//    private final Scheduler scheduler;
//
//    public SyncWorkExecutor(WorkInstanceRepository workInstanceRepository, ClusterRepository clusterRepository,
//        ClusterNodeRepository clusterNodeRepository, WorkflowInstanceRepository workflowInstanceRepository,
//        WorkRepository workRepository, WorkConfigRepository workConfigRepository, Locker locker,
//        HttpUrlUtils httpUrlUtils, AesUtils aesUtils, ClusterNodeMapper clusterNodeMapper,
//        DatasourceService datasourceService, IsxAppProperties isxAppProperties, FuncRepository funcRepository,
//        FuncMapper funcMapper, FileRepository fileRepository, SqlCommentService sqlCommentService,
//        SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService alarmService,
//        WorkEventRepository workEventRepository, Scheduler scheduler, WorkRunJobFactory workRunJobFactory,
//        VipWorkVersionRepository vipWorkVersionRepository) {
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
//        this.aesUtils = aesUtils;
//        this.clusterNodeMapper = clusterNodeMapper;
//        this.datasourceService = datasourceService;
//        this.isxAppProperties = isxAppProperties;
//        this.funcRepository = funcRepository;
//        this.funcMapper = funcMapper;
//        this.fileRepository = fileRepository;
//        this.sqlCommentService = sqlCommentService;
//        this.sqlValueService = sqlValueService;
//        this.sqlFunctionService = sqlFunctionService;
//        this.workEventRepository = workEventRepository;
//        this.vipWorkVersionRepository = vipWorkVersionRepository;
//        this.workRunJobFactory = workRunJobFactory;
//        this.scheduler = scheduler;
//    }
//
//    @Override
//    public String getWorkType() {
//        return WorkType.DATA_SYNC_JDBC;
//    }
//
//    @Override
//    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
//        WorkEventEntity workEvent) {
//
//        // 获取作业运行上下文
//        SyncWorkExecutorContext workEventBody =
//            JSON.parseObject(workEvent.getEventContext(), SyncWorkExecutorContext.class);
//        if (workEventBody == null) {
//            workEventBody = new SyncWorkExecutorContext();
//        }
//        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());
//
//        // 将线程存到Map
//        WORK_THREAD.put(workInstance.getId(), Thread.currentThread());
//
//        // 步骤1：校验集群与作业配置并选择节点
//        if (processNeverRun(workEvent, 1)) {
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始申请资源 \n");
//            if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在 \n");
//            }
//            Optional<ClusterEntity> clusterOpt =
//                clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
//            if (!clusterOpt.isPresent()) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在 \n");
//            }
//            List<ClusterNodeEntity> allEngineNodes =
//                clusterNodeRepository.findAllByClusterIdAndStatus(clusterOpt.get().getId(), ClusterNodeStatus.RUNNING);
//            if (allEngineNodes.isEmpty()) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群 \n");
//            }
//            if (Strings.isEmpty(workRunContext.getSyncWorkConfig().getPartitionColumn())) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检查作业失败 : 分区键为空 \n");
//            }
//            if (workRunContext.getSyncWorkConfig().getColumnMap().isEmpty()) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检查作业失败 : 请配置字段映射关系 \n");
//            }
//            ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("申请资源完成，激活节点:【")
//                .append(engineNode.getName()).append("】\n");
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成 \n");
//            workInstance = updateInstance(workInstance, logBuilder);
//
//            workEventBody.setClusterId(clusterOpt.get().getId());
//            workEventBody.setClusterType(clusterOpt.get().getClusterType());
//            workEventBody.setEngineNodeId(engineNode.getId());
//            workEventBody.setAgentHomePath(engineNode.getAgentHomePath());
//            workEventBody.setSparkHomePath(engineNode.getSparkHomePath());
//            workEvent.setEventContext(JSON.toJSONString(workEventBody));
//            workEventRepository.saveAndFlush(workEvent);
//        }
//
//        // 步骤2：构建请求并提交作业
//        if (processNeverRun(workEvent, 2)) {
//            ClusterNodeEntity engineNode = clusterNodeRepository.findById(workEventBody.getEngineNodeId()).orElseThrow(
//                () -> new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 节点不存在 \n"));
//            ClusterEntity cluster = clusterRepository.findById(workEventBody.getClusterId()).orElseThrow(
//                () -> new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在 \n"));
//
//            SubmitWorkReq executeReq = new SubmitWorkReq();
//            executeReq.setWorkId(workRunContext.getWorkId());
//            executeReq.setWorkType(WorkType.DATA_SYNC_JDBC);
//            executeReq.setWorkInstanceId(workInstance.getId());
//
//            DatasourceEntity sourceDatasource =
//                datasourceService.getDatasource(workRunContext.getSyncWorkConfig().getSourceDBId());
//            DatasourceConfig sourceConfig = DatasourceConfig.builder()
//                .driver(datasourceService.getDriverClass(sourceDatasource.getDbType()))
//                .url(sourceDatasource.getJdbcUrl()).dbTable(workRunContext.getSyncWorkConfig().getSourceTable())
//                .user(sourceDatasource.getUsername()).password(aesUtils.decrypt(sourceDatasource.getPasswd())).build();
//            workRunContext.getSyncWorkConfig().setSourceDatabase(sourceConfig);
//
//            DatasourceEntity targetDatasource =
//                datasourceService.getDatasource(workRunContext.getSyncWorkConfig().getTargetDBId());
//            DatasourceConfig targetConfig = DatasourceConfig.builder()
//                .driver(datasourceService.getDriverClass(targetDatasource.getDbType()))
//                .url(targetDatasource.getJdbcUrl()).dbTable(workRunContext.getSyncWorkConfig().getTargetTable())
//                .user(targetDatasource.getUsername()).password(aesUtils.decrypt(targetDatasource.getPasswd())).build();
//            workRunContext.getSyncWorkConfig().setTargetDatabase(targetConfig);
//
//            SparkSubmit sparkSubmit =
//                SparkSubmit.builder().verbose(true).mainClass("com.isxcode.spark.plugin.dataSync.jdbc.Execute")
//                    .appResource("spark-data-sync-jdbc-plugin.jar")
//                    .conf(genSparkSubmitConfig(workRunContext.getClusterConfig().getSparkConfig())).build();
//
//            if (!Strings.isEmpty(workRunContext.getSyncWorkConfig().getQueryCondition())) {
//                String sqlNoComment =
//                    sqlCommentService.removeSqlComment(workRunContext.getSyncWorkConfig().getQueryCondition());
//                String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);
//                String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);
//                String conditionScript = sqlFunctionService.parseSqlFunction(parseValueSql);
//                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("过滤条件: \n")
//                    .append(conditionScript).append("\n");
//                workInstance = updateInstance(workInstance, logBuilder);
//                workRunContext.getSyncWorkConfig().setQueryCondition(conditionScript);
//            }
//
//            PluginReq pluginReq = PluginReq.builder().syncWorkConfig(workRunContext.getSyncWorkConfig())
//                .sparkConfig(genSparkConfig(workRunContext.getClusterConfig().getSparkConfig()))
//                .syncRule(workRunContext.getSyncRule()).build();
//
//            if (DatasourceType.HIVE.equals(targetDatasource.getDbType())) {
//                sparkSubmit.getConf().put("qing.hive.username", targetDatasource.getUsername());
//            }
//            if (DatasourceType.HIVE.equals(sourceDatasource.getDbType())) {
//                sparkSubmit.getConf().put("qing.hive.username", sourceDatasource.getUsername());
//            }
//
//            ScpFileEngineNodeDto scpFileEngineNodeDto =
//                clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
//            scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));
//            String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
//                + File.separator + engineNode.getTenantId();
//            if (workRunContext.getFuncConfig() != null) {
//                List<FuncEntity> allFunc = funcRepository.findAllById(workRunContext.getFuncConfig());
//                allFunc.forEach(e -> {
//                    try {
//                        scpJar(scpFileEngineNodeDto, fileDir + File.separator + e.getFileId(),
//                            engineNode.getAgentHomePath() + "/zhiqingyun-agent/file/" + e.getFileId() + ".jar");
//                    } catch (JSchException | SftpException | InterruptedException | IOException ex) {
//                        throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO
//                            + " : 自定义函数jar文件上传失败，请检查文件是否上传或者重新上传" + ex.getMessage() + "\n");
//                    }
//                });
//                pluginReq.setFuncInfoList(funcMapper.funcEntityListToFuncInfoList(allFunc));
//                executeReq.setFuncConfig(funcMapper.funcEntityListToFuncInfoList(allFunc));
//            }
//
//            if (workRunContext.getLibConfig() != null) {
//                List<FileEntity> libFile = fileRepository.findAllById(workRunContext.getLibConfig());
//                libFile.forEach(e -> {
//                    try {
//                        scpJar(scpFileEngineNodeDto, fileDir + File.separator + e.getId(),
//                            engineNode.getAgentHomePath() + "/zhiqingyun-agent/file/" + e.getId() + ".jar");
//                    } catch (JSchException | SftpException | InterruptedException | IOException ex) {
//                        throw new WorkRunException(
//                            LocalDateTime.now() + WorkLog.ERROR_INFO + "自定义依赖jar文件上传失败，请检查文件是否上传或者重新上传\n");
//                    }
//                });
//                executeReq.setLibConfig(workRunContext.getLibConfig());
//            }
//
//            executeReq.setSparkSubmit(sparkSubmit);
//            executeReq.setPluginReq(pluginReq);
//            executeReq.setAgentHomePath(engineNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME);
//            executeReq.setSparkHomePath(engineNode.getSparkHomePath());
//            executeReq.setClusterType(cluster.getClusterType());
//
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("构建作业完成 \n");
//            workRunContext.getClusterConfig().getSparkConfig().forEach((k, v) -> logBuilder.append(LocalDateTime.now())
//                .append(WorkLog.SUCCESS_INFO).append(k).append(":").append(v).append(" \n"));
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始提交作业 \n");
//            workInstance = updateInstance(workInstance, logBuilder);
//
//            BaseResponse<?> baseResponse;
//            Integer lock = locker.lock("REQUEST_" + workInstance.getId());
//            try {
//                baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
//                    SparkAgentUrl.SUBMIT_WORK_URL), executeReq, BaseResponse.class);
//                if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
//                    throw new WorkRunException(
//                        LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
//                }
//                if (baseResponse.getData() == null) {
//                    throw new WorkRunException(
//                        LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
//                }
//                RunWorkRes submitWorkRes =
//                    JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
//                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("提交作业成功 : ")
//                    .append(submitWorkRes.getAppId()).append("\n");
//                workInstance.setSparkStarRes(JSON.toJSONString(submitWorkRes));
//                workInstance = updateInstance(workInstance, logBuilder);
//
//                workEventBody.setAppId(submitWorkRes.getAppId());
//                workEvent.setEventContext(JSON.toJSONString(workEventBody));
//                workEventRepository.saveAndFlush(workEvent);
//            } catch (ResourceAccessException e) {
//                throw new WorkRunException(
//                    LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + e.getMessage() + "\n");
//            } catch (HttpServerErrorException e1) {
//                if (HttpStatus.BAD_GATEWAY.value() == e1.getRawStatusCode()) {
//                    throw new WorkRunException(
//                        LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : 无法访问节点服务器,请检查服务器防火墙或者计算集群\n");
//                }
//                throw new WorkRunException(
//                    LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + e1.getMessage() + "\n");
//            } finally {
//                locker.unlock(lock);
//            }
//        }
//
//        // 步骤3：查询作业状态（一次）
//        if (processNeverRun(workEvent, 3)) {
//            ClusterNodeEntity engineNode = clusterNodeRepository.findById(workEventBody.getEngineNodeId()).orElseThrow(
//                () -> new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 节点不存在 \n"));
//            ClusterEntity cluster = clusterRepository.findById(workEventBody.getClusterId()).orElseThrow(
//                () -> new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在 \n"));
//
//            BaseResponse<?> baseResponse = HttpUtils.doPost(
//                httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
//                    SparkAgentUrl.GET_WORK_STATUS_URL),
//                GetWorkStatusReq.builder().appId(workEventBody.getAppId()).clusterType(cluster.getClusterType())
//                    .sparkHomePath(engineNode.getSparkHomePath()).build(),
//                BaseResponse.class);
//            if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
//                throw new WorkRunException(
//                    LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业状态异常 : " + baseResponse.getMsg() + "\n");
//            }
//            RunWorkRes workStatusRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
//            workInstance.setSparkStarRes(JSON.toJSONString(workStatusRes));
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:")
//                .append(workStatusRes.getAppStatus()).append("\n");
//            workInstance = updateInstance(workInstance, logBuilder);
//
//            workEventBody.setStatus(workStatusRes.getAppStatus());
//            workEvent.setEventContext(JSON.toJSONString(workEventBody));
//            workEventRepository.saveAndFlush(workEvent);
//
//            List<String> runningStatus =
//                Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING", "PENDING");
//            if (workStatusRes.getAppStatus() != null
//                && runningStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {
//                return InstanceStatus.RUNNING;
//            }
//        }
//
//        // 步骤4：获取日志并清理（如需）
//        if (processNeverRun(workEvent, 4)) {
//            ClusterNodeEntity engineNode = clusterNodeRepository.findById(workEventBody.getEngineNodeId()).orElseThrow(
//                () -> new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 节点不存在 \n"));
//            ClusterEntity cluster = clusterRepository.findById(workEventBody.getClusterId()).orElseThrow(
//                () -> new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在 \n"));
//
//            BaseResponse<?> baseResponse = HttpUtils.doPost(
//                httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
//                    SparkAgentUrl.GET_WORK_STDERR_LOG_URL),
//                GetWorkStderrLogReq.builder().appId(workEventBody.getAppId()).clusterType(cluster.getClusterType())
//                    .sparkHomePath(engineNode.getSparkHomePath()).build(),
//                BaseResponse.class);
//            if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
//                throw new WorkRunException(
//                    LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业日志异常 : " + baseResponse.getMsg() + "\n");
//            }
//            GetWorkStderrLogRes logRes =
//                JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetWorkStderrLogRes.class);
//            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("日志保存成功 \n");
//            if (logRes != null) {
//                workInstance.setYarnLog(logRes.getLog());
//            }
//            updateInstance(workInstance, logBuilder);
//
//            if (AgentType.K8S.equals(workEventBody.getClusterType())) {
//                StopWorkReq stopWorkReq = StopWorkReq.builder().appId(workEventBody.getAppId())
//                    .clusterType(AgentType.K8S).sparkHomePath(engineNode.getSparkHomePath())
//                    .agentHomePath(engineNode.getAgentHomePath()).build();
//                HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
//                    SparkAgentUrl.STOP_WORK_URL), stopWorkReq, BaseResponse.class);
//            }
//        }
//
//        // 步骤5：判断成功失败
//        if (processNeverRun(workEvent, 5)) {
//            List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");
//            if (workEventBody.getStatus() == null || !successStatus.contains(workEventBody.getStatus().toUpperCase())) {
//                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常\n");
//            }
//            return InstanceStatus.SUCCESS;
//        }
//
//        return InstanceStatus.SUCCESS;
//
//
//
//    }
//
//    @Override
//    protected void abort(WorkInstanceEntity workInstance) {
//
//        // 判断作业有没有提交成功
//        locker.lock("REQUEST_" + workInstance.getId());
//        try {
//            workInstance = workInstanceRepository.findById(workInstance.getId()).get();
//            if (!Strings.isEmpty(workInstance.getSparkStarRes())) {
//                RunWorkRes wokRunWorkRes = JSON.parseObject(workInstance.getSparkStarRes(), RunWorkRes.class);
//                if (!Strings.isEmpty(wokRunWorkRes.getAppId())) {
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
//                    StopWorkReq stopWorkReq = StopWorkReq.builder().appId(wokRunWorkRes.getAppId())
//                        .clusterType(cluster.getClusterType()).sparkHomePath(engineNode.getSparkHomePath())
//                        .agentHomePath(engineNode.getAgentHomePath()).build();
//                    BaseResponse<?> baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(),
//                        engineNode.getAgentPort(), SparkAgentUrl.STOP_WORK_URL), stopWorkReq, BaseResponse.class);
//
//                    if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
//                        throw new IsxAppException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
//                    }
//                } else {
//                    // 先杀死进程
//                    WORK_THREAD.get(workInstance.getId()).interrupt();
//                }
//            }
//        } finally {
//            locker.clearLock("REQUEST_" + workInstance.getId());
//        }
//    }
//
//    public Map<String, String> genSparkSubmitConfig(Map<String, String> sparkConfig) {
//
//        // 过滤掉，前缀不包含spark.xxx的配置，spark submit中必须都是spark.xxx
//        Map<String, String> sparkSubmitConfig = new HashMap<>();
//        sparkConfig.forEach((k, v) -> {
//            if (k.startsWith("spark")) {
//                sparkSubmitConfig.put(k, v);
//            }
//        });
//        return sparkSubmitConfig;
//    }
//
//    public Map<String, String> genSparkConfig(Map<String, String> sparkConfig) {
//
//        // k8s的配置不能提交到作业中
//        sparkConfig.remove("spark.kubernetes.driver.podTemplateFile");
//        sparkConfig.remove("spark.kubernetes.executor.podTemplateFile");
//
//        return sparkConfig;
//    }
//
//    @Data
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class SyncWorkExecutorContext {
//        private String clusterId;
//        private String clusterType;
//        private String engineNodeId;
//        private String agentHomePath;
//        private String sparkHomePath;
//        private String appId;
//        private String status;
//    }
//
//}
