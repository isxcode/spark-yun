package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.agent.constants.AgentType;
import com.isxcode.spark.api.agent.constants.SparkAgentUrl;
import com.isxcode.spark.api.agent.req.spark.*;
import com.isxcode.spark.api.agent.res.spark.GetWorkStderrLogRes;
import com.isxcode.spark.api.api.constants.PathConstants;
import com.isxcode.spark.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.spark.api.datasource.dto.ConnectInfo;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.WorkLog;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
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
import com.isxcode.spark.modules.datasource.entity.DatasourceEntity;
import com.isxcode.spark.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.spark.modules.datasource.repository.DatasourceRepository;
import com.isxcode.spark.modules.datasource.service.DatasourceService;
import com.isxcode.spark.modules.datasource.source.DataSourceFactory;
import com.isxcode.spark.modules.datasource.source.Datasource;
import com.isxcode.spark.modules.file.entity.FileEntity;
import com.isxcode.spark.modules.file.repository.FileRepository;
import com.isxcode.spark.modules.func.entity.FuncEntity;
import com.isxcode.spark.modules.func.mapper.FuncMapper;
import com.isxcode.spark.modules.func.repository.FuncRepository;
import com.isxcode.spark.modules.secret.entity.SecretKeyEntity;
import com.isxcode.spark.modules.secret.repository.SecretKeyRepository;
import com.isxcode.spark.modules.work.entity.WorkConfigEntity;
import com.isxcode.spark.modules.work.entity.WorkEntity;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.quartz.Scheduler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.isxcode.spark.common.utils.ssh.SshUtils.scpJar;

@Service
@Slf4j
public class SparkSqlExecutor extends WorkExecutor {

    private final WorkInstanceRepository workInstanceRepository;

    private final ClusterRepository clusterRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final WorkRepository workRepository;

    private final WorkConfigRepository workConfigRepository;

    private final Locker locker;

    private final HttpUrlUtils httpUrlUtils;

    private final FuncRepository funcRepository;

    private final FuncMapper funcMapper;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final IsxAppProperties isxAppProperties;

    private final FileRepository fileRepository;

    private final DatasourceService datasourceService;

    private final SqlCommentService sqlCommentService;

    private final SqlValueService sqlValueService;

    private final SqlFunctionService sqlFunctionService;

    private final DatasourceMapper datasourceMapper;

    private final DataSourceFactory dataSourceFactory;

    private final SecretKeyRepository secretKeyRepository;

    public SparkSqlExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, DatasourceRepository datasourceRepository,
        SqlCommentService sqlCommentService, SqlValueService sqlValueService, SqlFunctionService sqlFunctionService,
        AlarmService alarmService, DataSourceFactory dataSourceFactory, DatasourceMapper datasourceMapper,
        SecretKeyRepository secretKeyRepository, WorkEventRepository workEventRepository, Scheduler scheduler,
        Locker locker, WorkRepository workRepository, WorkRunJobFactory workRunJobFactory,
        WorkConfigRepository workConfigRepository, VipWorkVersionRepository vipWorkVersionRepository,
        ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, ClusterNodeRepository clusterNodeRepository,
        ClusterRepository clusterRepository, HttpUrlUtils httpUrlUtils, FuncRepository funcRepository,
        FuncMapper funcMapper, IsxAppProperties isxAppProperties, FileRepository fileRepository,
        DatasourceService datasourceService) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository);
        this.workInstanceRepository = workInstanceRepository;
        this.clusterRepository = clusterRepository;
        this.clusterNodeRepository = clusterNodeRepository;
        this.workRepository = workRepository;
        this.workConfigRepository = workConfigRepository;
        this.locker = locker;
        this.httpUrlUtils = httpUrlUtils;
        this.funcRepository = funcRepository;
        this.funcMapper = funcMapper;
        this.clusterNodeMapper = clusterNodeMapper;
        this.aesUtils = aesUtils;
        this.isxAppProperties = isxAppProperties;
        this.fileRepository = fileRepository;
        this.datasourceService = datasourceService;
        this.sqlCommentService = sqlCommentService;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.datasourceMapper = datasourceMapper;
        this.dataSourceFactory = dataSourceFactory;
        this.secretKeyRepository = secretKeyRepository;
    }

    @Override
    public String getWorkType() {
        return WorkType.QUERY_SPARK_SQL;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
        WorkEventEntity workEvent) {

        // 获取日志
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 首行日志
        if (workEvent.getEventProcess() == 0) {
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始申请资源 \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检查集群
        if (workEvent.getEventProcess() == 1) {

            // 检测计算集群是否存在
            if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎未配置  \n");
            }
            Optional<ClusterEntity> calculateEngineEntityOptional =
                clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
            if (!calculateEngineEntityOptional.isPresent()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在  \n");
            }
            // 检测集群中是否有合法节点
            List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
                .findAllByClusterIdAndStatus(calculateEngineEntityOptional.get().getId(), ClusterNodeStatus.RUNNING);
            if (allEngineNodes.isEmpty()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
            }
            // 节点选择随机数
            ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("申请资源完成，激活节点:【")
                .append(engineNode.getName()).append("】\n");

            // 解析节点请求体
            ScpFileEngineNodeDto scpFileEngineNodeDto =
                clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
            scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

            // 保存事件
            workRunContext.setScpNodeInfo(scpFileEngineNodeDto);
            workRunContext.setAgentHomePath(engineNode.getAgentHomePath());
            workRunContext.setClusterType(calculateEngineEntityOptional.get().getClusterType());
            workRunContext.setAgentHost(engineNode.getHost());
            workRunContext.setAgentPort(engineNode.getAgentPort());
            workRunContext.setSparkHomePath(engineNode.getSparkHomePath());

            // 保存日志
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成  \n");
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测脚本 \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 解析Sql脚本
        if (workEvent.getEventProcess() == 2) {

            // 检查脚本是否为空
            if (Strings.isEmpty(workRunContext.getScript())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测脚本失败 : SQL内容为空不能执行  \n");
            }

            // 去掉sql中的注释
            String sqlNoComment = sqlCommentService.removeSqlComment(workRunContext.getScript());

            // 解析上游参数
            String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);

            // 翻译sql中的系统变量
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

            String script;

            // 翻译sql中的系统函数
            try {
                script = sqlFunctionService.parseSqlFunction(parseValueSql);
            } catch (Exception e) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "系统函数异常\n" + e.getMessage() + "\n");
            }

            // 翻译全局变量
            List<SecretKeyEntity> allKey = secretKeyRepository.findAll();
            for (SecretKeyEntity secretKeyEntity : allKey) {
                script = script.replace("${{ secret." + secretKeyEntity.getKeyName() + " }}",
                    secretKeyEntity.getSecretValue());
                script = script.replace("${{ secret." + secretKeyEntity.getKeyName() + " }}", "******");
            }

            // 保存事件
            workRunContext.setScript(script);

            // 保存日志
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测脚本完成 \n");
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("SparkSql:  \n").append(script)
                .append("\n");
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始上传自定义函数 \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 导入自定义函数
        if (workEvent.getEventProcess() == 3) {

            ScpFileEngineNodeDto scpFileEngineNodeDto = workRunContext.getScpNodeInfo();
            String agentHomePath = workRunContext.getAgentHomePath();

            String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
                + File.separator + workInstance.getTenantId();
            if (workRunContext.getFuncConfig() != null) {
                List<FuncEntity> allFunc = funcRepository.findAllById(workRunContext.getFuncConfig());
                allFunc.forEach(e -> {
                    try {
                        scpJar(scpFileEngineNodeDto, fileDir + File.separator + e.getFileId(),
                            agentHomePath + "/zhiqingyun-agent/file/" + e.getFileId() + ".jar");
                    } catch (JSchException | SftpException | InterruptedException | IOException ex) {
                        log.error(ex.getMessage(), ex);
                        throw new WorkRunException(
                            LocalDateTime.now() + WorkLog.ERROR_INFO + "自定义函数jar文件上传失败，请检查文件是否上传或者重新上传\n");
                    }
                });
            }

            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("上传自定义函数完成 \n");
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始上传自定义依赖 \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 上传自定义依赖
        if (workEvent.getEventProcess() == 4) {

            ScpFileEngineNodeDto scpFileEngineNodeDto = workRunContext.getScpNodeInfo();
            String agentHomePath = workRunContext.getAgentHomePath();
            String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
                + File.separator + workInstance.getTenantId();

            if (workRunContext.getLibConfig() != null) {
                List<FileEntity> libFile = fileRepository.findAllById(workRunContext.getLibConfig());
                libFile.forEach(e -> {
                    try {
                        scpJar(scpFileEngineNodeDto, fileDir + File.separator + e.getId(),
                            agentHomePath + "/zhiqingyun-agent/file/" + e.getId() + ".jar");
                    } catch (JSchException | SftpException | InterruptedException | IOException ex) {
                        log.error(ex.getMessage(), ex);
                        throw new WorkRunException(
                            LocalDateTime.now() + WorkLog.ERROR_INFO + "自定义依赖jar文件上传失败，请检查文件是否上传或者重新上传\n");
                    }
                });
            }

            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("上传自定义依赖完成 \n");
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始构建请求体 \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 构建请求体
        if (workEvent.getEventProcess() == 5) {

            String script = workRunContext.getScript();
            String agentHomePath = workRunContext.getAgentHomePath();
            String clusterType = workRunContext.getClusterType();
            String sparkHomePath = workRunContext.getSparkHomePath();

            SubmitWorkReq executeReq = new SubmitWorkReq();
            executeReq.setWorkId(workRunContext.getWorkId());
            executeReq.setWorkType(WorkType.QUERY_SPARK_SQL);
            executeReq.setWorkInstanceId(workInstance.getId());
            executeReq.setLibConfig(workRunContext.getLibConfig());

            // 开始构造SparkSubmit
            SparkSubmit sparkSubmit = SparkSubmit.builder().verbose(true)
                .mainClass("com.isxcode.spark.plugin.query.sql.Execute").appResource("spark-query-sql-plugin.jar")
                .conf(genSparkSubmitConfig(workRunContext.getClusterConfig().getSparkConfig())).build();

            // 开始构造PluginReq
            PluginReq pluginReq = PluginReq.builder().sql(script).limit(200)
                .sparkConfig(genSparkConfig(workRunContext.getClusterConfig().getSparkConfig())).build();

            if (workRunContext.getFuncConfig() != null) {
                List<FuncEntity> allFunc = funcRepository.findAllById(workRunContext.getFuncConfig());
                pluginReq.setFuncInfoList(funcMapper.funcEntityListToFuncInfoList(allFunc));
                executeReq.setFuncConfig(funcMapper.funcEntityListToFuncInfoList(allFunc));
            }

            // 解析db
            if (StringUtils.isNotBlank(workRunContext.getDatasourceId())
                && workRunContext.getClusterConfig().getEnableHive()) {
                DatasourceEntity datasourceEntity = datasourceService.getDatasource(workRunContext.getDatasourceId());
                ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
                Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
                try {
                    String database = datasource.parseDbName(datasourceEntity.getJdbcUrl());
                    if (!Strings.isEmpty(database)) {
                        pluginReq.setDatabase(database);
                    }
                } catch (IsxAppException e) {
                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMsg() + "\n");
                }
                // 如果数据库id不为空,则替换hive的metastore url
                pluginReq.getSparkConfig().put("hive.metastore.uris", datasourceEntity.getMetastoreUris());

                // 添加自定义username
                if (Strings.isNotBlank(datasourceEntity.getUsername())) {
                    sparkSubmit.getConf().put("qing.hive.username", datasourceEntity.getUsername());
                }
            }

            // 开始构造executeReq
            executeReq.setSparkSubmit(sparkSubmit);
            executeReq.setPluginReq(pluginReq);
            executeReq.setAgentHomePath(agentHomePath + "/" + PathConstants.AGENT_PATH_NAME);
            executeReq.setSparkHomePath(sparkHomePath);
            executeReq.setClusterType(clusterType);

            // 保存请求体
            workRunContext.setExecuteReq(executeReq);

            // 保存日志
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("构建作业完成 \n");
            workRunContext.getClusterConfig().getSparkConfig().forEach((k, v) -> logBuilder.append(LocalDateTime.now())
                .append(WorkLog.SUCCESS_INFO).append(k).append(":").append(v).append(" \n"));
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始提交作业  \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 提交作业
        if (workEvent.getEventProcess() == 6) {

            SubmitWorkReq executeReq = workRunContext.getExecuteReq();
            String agentHost = workRunContext.getAgentHost();
            String agentPort = workRunContext.getAgentPort();

            // 开始提交作业
            BaseResponse<?> baseResponse;

            RunWorkRes submitWorkRes;
            try {
                baseResponse =
                    HttpUtils.doPost(httpUrlUtils.genHttpUrl(agentHost, agentPort, SparkAgentUrl.SUBMIT_WORK_URL),
                        executeReq, BaseResponse.class);
                if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
                }
                // 解析返回对象,获取appId
                if (baseResponse.getData() == null) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
                }
                submitWorkRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("提交作业成功 : ")
                    .append(submitWorkRes.getAppId()).append("\n");
                workInstance.setSparkStarRes(JSON.toJSONString(submitWorkRes));
            } catch (ResourceAccessException e) {
                log.error(e.getMessage(), e);
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + e.getMessage() + "\n");
            } catch (HttpServerErrorException e1) {
                log.error(e1.getMessage(), e1);
                if (HttpStatus.BAD_GATEWAY.value() == e1.getRawStatusCode()) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : 无法访问节点服务器,请检查服务器防火墙或者计算集群\n");
                }
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + e1.getMessage() + "\n");
            }

            // 保存appId
            workRunContext.setAppId(submitWorkRes.getAppId());

            // 构建作业完成，并打印作业配置信息
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("提交成功 \n");
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始监听状态  \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 获取状态
        if (workEvent.getEventProcess() == 7) {

            String preStatus = workRunContext.getPreStatus() == null ? "" : workRunContext.getPreStatus();

            // 提交作业成功后，开始循环判断状态
            String appId = workRunContext.getAppId();
            String clusterType = workRunContext.getClusterType();
            String sparkHomePath = workRunContext.getSparkHomePath();
            String agentHost = workRunContext.getAgentHost();
            String agentPort = workRunContext.getAgentPort();

            BaseResponse<?> baseResponse;

            // 获取作业状态并保存
            GetWorkStatusReq getWorkStatusReq =
                GetWorkStatusReq.builder().appId(appId).clusterType(clusterType).sparkHomePath(sparkHomePath).build();
            baseResponse =
                HttpUtils.doPost(httpUrlUtils.genHttpUrl(agentHost, agentPort, SparkAgentUrl.GET_WORK_STATUS_URL),
                    getWorkStatusReq, BaseResponse.class);
            if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业状态异常 : " + baseResponse.getMsg() + "\n");
            }

            // 解析返回状态，并保存
            RunWorkRes workStatusRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
            workInstance.setSparkStarRes(JSON.toJSONString(workStatusRes));

            // 状态发生变化，则添加日志状态
            if (!preStatus.equals(workStatusRes.getAppStatus())) {
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:")
                    .append(workStatusRes.getAppStatus()).append("\n");
                workRunContext.setPreStatus(workStatusRes.getAppStatus());
                updateWorkEvent(workEvent, workRunContext);
                updateInstance(workInstance, logBuilder);
            }

            // 如果状态是运行中，更新日志，继续执行
            List<String> runningStatus =
                Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING", "PENDING");
            if (runningStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {
                return InstanceStatus.RUNNING;
            }

            // 如果是中止，直接退出
            if ("KILLED".equals(workStatusRes.getAppStatus().toUpperCase())
                || "TERMINATING".equals(workStatusRes.getAppStatus().toUpperCase())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业运行中止" + "\n");
            }

            // 状态发生变化，则添加日志状态
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始保存日志和数据 \n");
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 保存日志
        if (workEvent.getEventProcess() == 8) {

            String appId = workRunContext.getAppId();
            String clusterType = workRunContext.getClusterType();
            String agentHomePath = workRunContext.getAgentHomePath();
            String agentHost = workRunContext.getAgentHost();
            String agentPort = workRunContext.getAgentPort();
            String preStatus = workRunContext.getPreStatus();

            BaseResponse<?> baseResponse;

            // 获取日志并保存
            GetWorkStderrLogReq getWorkStderrLogReq = GetWorkStderrLogReq.builder().appId(appId)
                .clusterType(clusterType).sparkHomePath(agentHomePath).build();
            baseResponse =
                HttpUtils.doPost(httpUrlUtils.genHttpUrl(agentHost, agentPort, SparkAgentUrl.GET_WORK_STDERR_LOG_URL),
                    getWorkStderrLogReq, BaseResponse.class);
            if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业日志异常 : " + baseResponse.getMsg() + "\n");
            }

            // 解析日志并保存
            GetWorkStderrLogRes yagGetLogRes =
                JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetWorkStderrLogRes.class);
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("日志保存成功 \n");
            if (yagGetLogRes != null) {
                workInstance.setYarnLog(yagGetLogRes.getLog());
            }

            // 如果运行成功，还要继续保存数据
            List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");
            if (successStatus.contains(preStatus.toUpperCase())) {

                // 获取数据
                GetWorkDataReq getWorkDataReq =
                    GetWorkDataReq.builder().appId(appId).clusterType(clusterType).sparkHomePath(agentHomePath).build();
                baseResponse =
                    HttpUtils.doPost(httpUrlUtils.genHttpUrl(agentHost, agentPort, SparkAgentUrl.GET_WORK_DATA_URL),
                        getWorkDataReq, BaseResponse.class);

                if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业数据异常 : " + baseResponse.getErr() + "\n");
                }

                // 解析数据并保存
                workInstance.setResultData(JSON.toJSONString(baseResponse.getData()));
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功 \n");
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始清理执行文件 \n");
                return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
            }
        }

        // 清楚执行文件
        if (workEvent.getEventProcess() == 9) {

            String appId = workRunContext.getAppId();
            String clusterType = workRunContext.getClusterType();
            String agentHomePath = workRunContext.getAgentHomePath();
            String agentHost = workRunContext.getAgentHost();
            String agentPort = workRunContext.getAgentPort();
            String sparkHomePath = workRunContext.getSparkHomePath();

            if (AgentType.K8S.equals(clusterType)) {
                StopWorkReq stopWorkReq = StopWorkReq.builder().appId(appId).clusterType(AgentType.K8S)
                    .sparkHomePath(sparkHomePath).agentHomePath(agentHomePath).build();
                HttpUtils.doPost(httpUrlUtils.genHttpUrl(agentHost, agentPort, SparkAgentUrl.STOP_WORK_URL),
                    stopWorkReq, BaseResponse.class);
            }

            updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
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
                        throw new WorkRunException(
                            LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
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

    public Map<String, String> genSparkConfig(Map<String, String> sparkConfig) {

        // k8s的配置不能提交到作业中
        sparkConfig.remove("spark.kubernetes.driver.podTemplateFile");
        sparkConfig.remove("spark.kubernetes.executor.podTemplateFile");

        return sparkConfig;
    }
}

