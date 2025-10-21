package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.agent.constants.AgentType;
import com.isxcode.spark.api.agent.constants.SparkAgentUrl;
import com.isxcode.spark.api.agent.req.spark.*;
import com.isxcode.spark.api.agent.res.spark.GetWorkStderrLogRes;
import com.isxcode.spark.api.api.constants.PathConstants;
import com.isxcode.spark.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.spark.api.datasource.constants.DatasourceType;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.WorkLog;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
import com.isxcode.spark.api.work.dto.ClusterConfig;
import com.isxcode.spark.api.work.dto.DatasourceConfig;
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
import com.isxcode.spark.modules.datasource.service.DatasourceService;
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
import com.isxcode.spark.modules.work.service.WorkService;
import com.isxcode.spark.modules.work.sql.SqlCommentService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.work.sql.SqlValueService;
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
import java.time.LocalDateTime;
import java.util.*;

import static com.isxcode.spark.common.utils.ssh.SshUtils.scpJar;

@Service
@Slf4j
public class SyncWorkExecutor extends WorkExecutor {

    private final WorkInstanceRepository workInstanceRepository;

    private final ClusterRepository clusterRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final WorkRepository workRepository;

    private final WorkConfigRepository workConfigRepository;

    private final Locker locker;

    private final HttpUrlUtils httpUrlUtils;

    private final AesUtils aesUtils;

    private final ClusterNodeMapper clusterNodeMapper;

    private final DatasourceService datasourceService;

    private final IsxAppProperties isxAppProperties;

    private final FuncRepository funcRepository;

    private final FuncMapper funcMapper;

    private final FileRepository fileRepository;

    private final SqlCommentService sqlCommentService;

    private final SqlValueService sqlValueService;

    private final SqlFunctionService sqlFunctionService;

    private final SecretKeyRepository secretKeyRepository;

    public SyncWorkExecutor(WorkInstanceRepository workInstanceRepository, ClusterRepository clusterRepository,
                            ClusterNodeRepository clusterNodeRepository, WorkflowInstanceRepository workflowInstanceRepository,
                            WorkRepository workRepository, WorkConfigRepository workConfigRepository, Locker locker,
                            HttpUrlUtils httpUrlUtils, AesUtils aesUtils, ClusterNodeMapper clusterNodeMapper,
                            DatasourceService datasourceService, IsxAppProperties isxAppProperties, FuncRepository funcRepository,
                            FuncMapper funcMapper, FileRepository fileRepository, SqlCommentService sqlCommentService,
                            SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService alarmService,
                            Scheduler scheduler, WorkEventRepository workEventRepository, WorkRunJobFactory workRunJobFactory,
                            VipWorkVersionRepository vipWorkVersionRepository, WorkService workService,
                            SecretKeyRepository secretKeyRepository) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository,
            workService);
        this.workInstanceRepository = workInstanceRepository;
        this.clusterRepository = clusterRepository;
        this.clusterNodeRepository = clusterNodeRepository;
        this.workRepository = workRepository;
        this.workConfigRepository = workConfigRepository;
        this.locker = locker;
        this.httpUrlUtils = httpUrlUtils;
        this.aesUtils = aesUtils;
        this.clusterNodeMapper = clusterNodeMapper;
        this.datasourceService = datasourceService;
        this.isxAppProperties = isxAppProperties;
        this.funcRepository = funcRepository;
        this.funcMapper = funcMapper;
        this.fileRepository = fileRepository;
        this.sqlCommentService = sqlCommentService;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.secretKeyRepository = secretKeyRepository;
    }

    @Override
    public String getWorkType() {
        return WorkType.DATA_SYNC_JDBC;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
                             WorkEventEntity workEvent) {

        // 获取实例日志
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 打印首行日志
        if (workEvent.getEventProcess() == 0) {
            logBuilder.append(startLog("开始申请集群资源"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检查集群
        if (workEvent.getEventProcess() == 1) {

            // 检测集群是否配置
            if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
                throw errorLogException("申请资源失败 : 计算引擎未配置");
            }

            // 检查集群是否存在
            ClusterEntity cluster = clusterRepository.findById(workRunContext.getClusterConfig().getClusterId())
                .orElseThrow(() -> errorLogException("申请资源失败 : 计算引擎不存在"));

            // 检测集群中是否为空
            List<ClusterNodeEntity> clusterNodes =
                clusterNodeRepository.findAllByClusterIdAndStatus(cluster.getId(), ClusterNodeStatus.RUNNING);
            if (clusterNodes.isEmpty()) {
                throw errorLogException("申请资源失败 : 集群不存在可用节点，请切换一个集群");
            }

            // 随机选择一个节点
            ClusterNodeEntity agentNode = clusterNodes.get(new Random().nextInt(clusterNodes.size()));
            logBuilder.append(endLog("申请资源完成，激活节点: " + agentNode.getName()));

            // 解析请求节点信息
            ScpFileEngineNodeDto scpNode = clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(agentNode);
            scpNode.setPasswd(aesUtils.decrypt(scpNode.getPasswd()));

            // 保存事件
            workRunContext.setClusterType(cluster.getClusterType());
            workRunContext.setScpNodeInfo(scpNode);
            workRunContext.setAgentNode(agentNode);

            // 保存日志
            if (!Strings.isEmpty(workRunContext.getSyncWorkConfig().getQueryCondition())) {
                logBuilder.append(startLog("开始检测过滤条件"));
            }
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 解析条件脚本
        if (workEvent.getEventProcess() == 2) {

            // 检查分区键是否为空
            if (Strings.isEmpty(workRunContext.getSyncWorkConfig().getPartitionColumn())) {
                throw errorLogException("检查作业失败 : 分区键为空");
            }

            // 检测用户是否配置映射关系
            if (workRunContext.getSyncWorkConfig().getColumnMap().isEmpty()) {
                throw errorLogException("检查作业失败 : 请配置字段映射关系");
            }

            // 过滤条件支持系统参数和函数解析
            if (!Strings.isEmpty(workRunContext.getSyncWorkConfig().getQueryCondition())) {

                // 去掉sql中的注释
                String sqlNoComment =
                    sqlCommentService.removeSqlComment(workRunContext.getSyncWorkConfig().getQueryCondition());

                // 解析上游参数
                String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);

                // 翻译sql中的系统变量
                String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

                // 翻译sql中的系统函数
                String script = sqlFunctionService.parseSqlFunction(parseValueSql);

                // 解析全局变量
                List<SecretKeyEntity> allKey = secretKeyRepository.findAll();
                for (SecretKeyEntity secretKeyEntity : allKey) {
                    script = script.replace("${{ secret." + secretKeyEntity.getKeyName() + " }}",
                        secretKeyEntity.getSecretValue());
                }

                // 保存事件
                workRunContext.getSyncWorkConfig().setQueryCondition(script);

                // 保存日志
                logBuilder.append(endLog("解析过滤条件完成:"));
                logBuilder.append(script).append("\n");
            }

            logBuilder.append(startLog("开始上传自定义函数"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 上传自定义函数
        if (workEvent.getEventProcess() == 3) {

            // 上传自定义函数
            if (workRunContext.getFuncConfig() != null) {

                // 上下文中获取参数
                ScpFileEngineNodeDto scpNode = workRunContext.getScpNodeInfo();
                ClusterNodeEntity agentNode = workRunContext.getAgentNode();

                // 函数文件目录
                String funcDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator
                    + "file" + File.separator + workInstance.getTenantId();
                List<FuncEntity> allFunc = funcRepository.findAllById(workRunContext.getFuncConfig());
                allFunc.forEach(e -> {
                    try {
                        scpJar(scpNode, funcDir + File.separator + e.getFileId(),
                            agentNode.getAgentHomePath() + "/zhiqingyun-agent/file/" + e.getFileId() + ".jar");
                    } catch (JSchException | SftpException | InterruptedException | IOException ex) {
                        throw errorLogException("自定义函数jar文件上传失败，请检查文件是否上传或者重新上传");
                    }
                });
            }

            // 保存日志
            logBuilder.append(endLog("上传自定义函数完成"));
            logBuilder.append(startLog("开始上传自定义依赖"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 上传自定义依赖
        if (workEvent.getEventProcess() == 4) {

            // 上传自定义依赖
            if (workRunContext.getLibConfig() != null) {

                // 上下文中获取参数
                ScpFileEngineNodeDto scpNode = workRunContext.getScpNodeInfo();
                ClusterNodeEntity agentNode = workRunContext.getAgentNode();

                // 依赖文件目录
                String libDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator
                    + "file" + File.separator + workInstance.getTenantId();
                List<FileEntity> libFile = fileRepository.findAllById(workRunContext.getLibConfig());
                libFile.forEach(e -> {
                    try {
                        scpJar(scpNode, libDir + File.separator + e.getId(),
                            agentNode.getAgentHomePath() + "/zhiqingyun-agent/file/" + e.getId() + ".jar");
                    } catch (JSchException | SftpException | InterruptedException | IOException ex) {
                        throw errorLogException("自定义依赖jar文件上传失败，请检查文件是否上传或者重新上传");
                    }
                });
            }

            // 保存日志
            logBuilder.append(endLog("上传自定义依赖完成"));
            logBuilder.append(startLog("开始上传同步文件"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 构建作业请求体
        if (workEvent.getEventProcess() == 5) {

            // 上下文中获取参数
            String clusterType = workRunContext.getClusterType();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 构建请求体
            SubmitWorkReq submitWorkReq = new SubmitWorkReq();
            submitWorkReq.setWorkId(workRunContext.getWorkId());
            submitWorkReq.setWorkType(WorkType.DATA_SYNC_JDBC);
            submitWorkReq.setWorkInstanceId(workInstance.getId());
            submitWorkReq.setAgentHomePath(agentNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME);
            submitWorkReq.setSparkHomePath(agentNode.getSparkHomePath());
            submitWorkReq.setClusterType(clusterType);

            // 封装来源Datasource的信息
            DatasourceEntity sourceDatasource =
                datasourceService.getDatasource(workRunContext.getSyncWorkConfig().getSourceDBId());
            DatasourceConfig sourceConfig = DatasourceConfig.builder()
                .driver(datasourceService.getDriverClass(sourceDatasource.getDbType()))
                .url(sourceDatasource.getJdbcUrl()).dbTable(workRunContext.getSyncWorkConfig().getSourceTable())
                .user(sourceDatasource.getUsername()).password(aesUtils.decrypt(sourceDatasource.getPasswd())).build();
            workRunContext.getSyncWorkConfig().setSourceDatabase(sourceConfig);

            // 封装去向Datasource的信息
            DatasourceEntity targetDatasource =
                datasourceService.getDatasource(workRunContext.getSyncWorkConfig().getTargetDBId());
            DatasourceConfig targetConfig = DatasourceConfig.builder()
                .driver(datasourceService.getDriverClass(targetDatasource.getDbType()))
                .url(targetDatasource.getJdbcUrl()).dbTable(workRunContext.getSyncWorkConfig().getTargetTable())
                .user(targetDatasource.getUsername()).password(aesUtils.decrypt(targetDatasource.getPasswd())).build();
            workRunContext.getSyncWorkConfig().setTargetDatabase(targetConfig);

            // 开始构造SparkSubmit
            SparkSubmit sparkSubmit =
                SparkSubmit.builder().verbose(true).mainClass("com.isxcode.spark.plugin.dataSync.jdbc.Execute")
                    .appResource("spark-data-sync-jdbc-plugin.jar")
                    .conf(genSparkSubmitConfig(workRunContext.getClusterConfig().getSparkConfig())).build();
            if (DatasourceType.HIVE.equals(targetDatasource.getDbType())) {
                sparkSubmit.getConf().put("qing.hive.username", targetDatasource.getUsername());
            }
            if (DatasourceType.HIVE.equals(sourceDatasource.getDbType())) {
                sparkSubmit.getConf().put("qing.hive.username", sourceDatasource.getUsername());
            }

            // 构建插件请求体
            PluginReq pluginReq = PluginReq.builder().syncWorkConfig(workRunContext.getSyncWorkConfig())
                .sparkConfig(genSparkConfig(workRunContext.getClusterConfig().getSparkConfig()))
                .syncRule(workRunContext.getSyncRule()).build();

            // 保存请求体
            submitWorkReq.setSparkSubmit(sparkSubmit);
            submitWorkReq.setPluginReq(pluginReq);
            workRunContext.setSubmitWorkReq(submitWorkReq);

            // 保存日志
            logBuilder.append(endLog("构建作业完成"));
            workRunContext.getClusterConfig().getSparkConfig()
                .forEach((k, v) -> logBuilder.append(k).append(":").append(v).append(" \n"));
            logBuilder.append(startLog("开始提交作业"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 提交作业
        if (workEvent.getEventProcess() == 6) {

            // 上下文中获取参数
            SubmitWorkReq submitWorkReq = workRunContext.getSubmitWorkReq();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 开始提交作业
            try {
                BaseResponse<?> baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(agentNode.getHost(),
                    agentNode.getAgentPort(), SparkAgentUrl.SUBMIT_WORK_URL), submitWorkReq, BaseResponse.class);
                if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())
                    || baseResponse.getData() == null) {
                    throw errorLogException("提交作业失败 : " + baseResponse.getMsg());
                }
                RunWorkRes submitWorkRes =
                    JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
                logBuilder.append(endLog("提交作业成功 : " + submitWorkRes.getAppId()));

                // 保存实例
                workInstance.setSparkStarRes(JSON.toJSONString(submitWorkRes));

                // 保存上下文
                workRunContext.setAppId(submitWorkRes.getAppId());
            } catch (ResourceAccessException e) {
                log.error(e.getMessage(), e);
                throw errorLogException("提交作业失败 : " + e.getMessage());
            } catch (HttpServerErrorException e1) {
                log.error(e1.getMessage(), e1);
                if (HttpStatus.BAD_GATEWAY.value() == e1.getRawStatusCode()) {
                    throw errorLogException("提交作业失败 : 无法访问节点服务器,请检查服务器防火墙或者计算集群");
                }
                throw errorLogException("提交作业失败 : " + e1.getMessage());
            }

            // 保存日志
            logBuilder.append(startLog("开始监听状态"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 监听作业状态
        if (workEvent.getEventProcess() == 7) {

            // 提交作业成功后，开始循环判断状态
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

            // 解析返回状态
            RunWorkRes workStatusRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);

            // 如果作业状态发生变化，则保存状态
            if (!preStatus.equals(workStatusRes.getAppStatus())) {
                logBuilder.append(statusLog("运行状态: " + workStatusRes.getAppStatus()));

                // 更新实例
                workInstance.setSparkStarRes(JSON.toJSONString(workStatusRes));
                updateInstance(workInstance, logBuilder);

                // 更新上下文
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
            logBuilder.append(startLog("开始保存作业日志和数据"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 保存作业日志和数据
        if (workEvent.getEventProcess() == 8) {

            // 获取上下文
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
                throw errorLogException("获取作业日志异常 : " + baseResponse.getMsg());
            }

            // 解析日志并保存
            GetWorkStderrLogRes yagGetLogRes =
                JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetWorkStderrLogRes.class);
            workInstance.setYarnLog(yagGetLogRes.getLog());
            logBuilder.append(endLog("日志保存成功"));

            // 如果运行成功，还要继续保存数据
            List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");
            if (!successStatus.contains(preStatus.toUpperCase())) {
                workRunContext.setPreStatus(InstanceStatus.FAIL);
            }

            // 保存日志
            logBuilder.append(startLog("开始清理执行文件"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 清理作业执行文件
        if (workEvent.getEventProcess() == 9) {

            // 获取上下文
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
            logBuilder.append(endLog("清理执行文件完成"));
            updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 如果最终状态为失败，抛出空异常
        if (InstanceStatus.FAIL.equals(workRunContext.getPreStatus())) {
            throw errorLogException("作业最终状态为失败");
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
