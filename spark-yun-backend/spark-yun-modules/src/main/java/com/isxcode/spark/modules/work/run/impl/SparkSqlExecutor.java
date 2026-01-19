package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.agent.constants.AgentType;
import com.isxcode.spark.api.agent.constants.SparkAgentUrl;
import com.isxcode.spark.api.agent.req.spark.*;
import com.isxcode.spark.api.api.constants.PathConstants;
import com.isxcode.spark.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.spark.api.datasource.dto.ConnectInfo;
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
import com.isxcode.spark.modules.datasource.entity.DatasourceEntity;
import com.isxcode.spark.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.spark.modules.datasource.service.DatasourceService;
import com.isxcode.spark.modules.datasource.source.DataSourceFactory;
import com.isxcode.spark.modules.datasource.source.Datasource;
import com.isxcode.spark.modules.file.entity.FileEntity;
import com.isxcode.spark.modules.file.entity.LibPackageEntity;
import com.isxcode.spark.modules.file.repository.FileRepository;
import com.isxcode.spark.modules.file.service.FileService;
import com.isxcode.spark.modules.func.entity.FuncEntity;
import com.isxcode.spark.modules.func.mapper.FuncMapper;
import com.isxcode.spark.modules.func.repository.FuncRepository;
import com.isxcode.spark.modules.secret.entity.SecretKeyEntity;
import com.isxcode.spark.modules.secret.repository.SecretKeyRepository;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.*;
import com.isxcode.spark.modules.work.run.AgentLinkUtils;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.isxcode.spark.common.utils.ssh.SshUtils.scpJar;

@Service
@Slf4j
public class SparkSqlExecutor extends WorkExecutor {

    private final ClusterRepository clusterRepository;

    private final ClusterNodeRepository clusterNodeRepository;

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

    private final AgentLinkUtils agentLinkUtils;

    private final FileService fileService;

    public SparkSqlExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, SqlCommentService sqlCommentService,
        SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService alarmService,
        DataSourceFactory dataSourceFactory, DatasourceMapper datasourceMapper, SecretKeyRepository secretKeyRepository,
        WorkEventRepository workEventRepository, Locker locker, WorkRepository workRepository,
        WorkRunJobFactory workRunJobFactory, WorkConfigRepository workConfigRepository,
        VipWorkVersionRepository vipWorkVersionRepository, ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils,
        ClusterNodeRepository clusterNodeRepository, ClusterRepository clusterRepository, FuncRepository funcRepository,
        FuncMapper funcMapper, IsxAppProperties isxAppProperties, FileRepository fileRepository,
        DatasourceService datasourceService, WorkService workService, AgentLinkUtils agentLinkUtils,
        FileService fileService) {

        super(alarmService, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository,
            workService);
        this.clusterRepository = clusterRepository;
        this.clusterNodeRepository = clusterNodeRepository;
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
        this.agentLinkUtils = agentLinkUtils;
        this.fileService = fileService;
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
            logBuilder.append(startLog("检测SparkSQL开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检测SparkSQL
        if (workEvent.getEventProcess() == 2) {

            // 检查脚本是否为空
            if (Strings.isEmpty(workRunContext.getScript())) {
                throw errorLogException("检测SparkSQL异常 : SQL内容不能为空");
            }

            // 去掉sql中的注释
            String sqlNoComment = sqlCommentService.removeSqlComment(workRunContext.getScript());

            // 解析上游结果
            String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);

            // 解析系统变量
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

            // 解析系统函数
            String script = sqlFunctionService.parseSqlFunction(parseValueSql);
            String printSql = script;

            // 解析全局变量
            List<SecretKeyEntity> allKey = secretKeyRepository.findAll();
            for (SecretKeyEntity secretKeyEntity : allKey) {
                script = script.replace("${{ secret." + secretKeyEntity.getKeyName() + " }}",
                    aesUtils.decrypt(secretKeyEntity.getSecretValue()));
                printSql = printSql.replace("${{ secret." + secretKeyEntity.getKeyName() + " }}", "******");
            }

            // 保存上下文
            workRunContext.setScript(script);

            // 保存日志
            logBuilder.append(printSql).append("\n");
            logBuilder.append(endLog("检测SparkSQL完成"));
            logBuilder.append(startLog("上传函数开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 上传函数
        if (workEvent.getEventProcess() == 3) {

            if (workRunContext.getFuncConfig() != null) {

                // 获取上下文参数
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
                        throw errorLogException("上传函数异常 : " + ex.getMessage());
                    }
                });
            }

            // 保存日志
            logBuilder.append(endLog("上传函数完成"));
            logBuilder.append(startLog("上传依赖包开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 上传依赖包
        if (workEvent.getEventProcess() == 4) {

            Set<String> uploadFileSet = new HashSet<>();

            // 合并依赖包依赖
            if (workRunContext.getLibPackageConfig() != null) {
                workRunContext.getLibPackageConfig().forEach(libPackageId -> {
                    LibPackageEntity metaLibPackage = fileService.getLibPackage(libPackageId);
                    if (metaLibPackage.getFileIdList() != null) {
                        uploadFileSet.addAll(JSON.parseArray(metaLibPackage.getFileIdList(), String.class));
                    }
                });
            }

            // 合并依赖包
            if (workRunContext.getLibConfig() != null) {
                uploadFileSet.addAll(workRunContext.getLibConfig());
            }

            if (!uploadFileSet.isEmpty()) {

                // 获取上下文参数
                ScpFileEngineNodeDto scpNode = workRunContext.getScpNodeInfo();
                ClusterNodeEntity agentNode = workRunContext.getAgentNode();

                // 遍历上传到集群节点
                String libDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator
                    + "file" + File.separator + workInstance.getTenantId();
                List<FileEntity> libFile = fileRepository.findAllById(uploadFileSet);
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
        if (workEvent.getEventProcess() == 5) {

            // 获取上下文参数
            String script = workRunContext.getScript();
            String clusterType = workRunContext.getClusterType();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 构造代理请求体
            SubmitWorkReq submitWorkReq = new SubmitWorkReq();
            submitWorkReq.setWorkId(workRunContext.getWorkId());
            submitWorkReq.setWorkType(WorkType.QUERY_SPARK_SQL);
            submitWorkReq.setWorkInstanceId(workInstance.getId());
            submitWorkReq.setAgentHomePath(agentNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME);
            submitWorkReq.setSparkHomePath(agentNode.getSparkHomePath());
            submitWorkReq.setClusterType(clusterType);

            // 构建Spark提交请求体
            SparkSubmit sparkSubmit = SparkSubmit.builder().verbose(true)
                .mainClass("com.isxcode.spark.plugin.query.sql.Execute").appResource("spark-query-sql-plugin.jar")
                .conf(genSparkSubmitConfig(workRunContext.getClusterConfig().getSparkConfig())).build();

            // 构建Spark插件运行请求体
            PluginReq pluginReq = PluginReq.builder().sql(script)
                .sparkConfig(genSparkConfig(workRunContext.getClusterConfig().getSparkConfig())).build();

            // 添加条数限制
            if (workRunContext.getQueryConfig().getEnableLimit()) {
                pluginReq.setLimit(workRunContext.getQueryConfig().getLineLimit());
            }

            // 配置函数
            if (workRunContext.getFuncConfig() != null) {
                List<FuncEntity> allFunc = funcRepository.findAllById(workRunContext.getFuncConfig());
                pluginReq.setFuncInfoList(funcMapper.funcEntityListToFuncInfoList(allFunc));
                submitWorkReq.setFuncConfig(funcMapper.funcEntityListToFuncInfoList(allFunc));
            }

            // 配置依赖包
            if (workRunContext.getLibConfig() != null) {
                submitWorkReq.setLibConfig(workRunContext.getLibConfig());
            }

            // 配置hive数据源
            if (StringUtils.isNotBlank(workRunContext.getDatasourceId())
                && workRunContext.getClusterConfig().getEnableHive()) {

                DatasourceEntity datasourceEntity = datasourceService.getDatasource(workRunContext.getDatasourceId());
                ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
                Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
                pluginReq.setDatabase(datasource.parseDbName(datasourceEntity.getJdbcUrl()));
                pluginReq.getSparkConfig().put("hive.metastore.uris", datasourceEntity.getMetastoreUris());

                // 添加自定义username
                if (Strings.isNotBlank(datasourceEntity.getUsername())) {
                    sparkSubmit.getConf().put("qing.hive.username", datasourceEntity.getUsername());
                }
            }

            // 保存上下文
            submitWorkReq.setSparkSubmit(sparkSubmit);
            submitWorkReq.setPluginReq(pluginReq);
            workRunContext.setSubmitWorkReq(submitWorkReq);

            // 保存日志
            logBuilder.append(endLog("构建请求体完成"));
            workRunContext.getClusterConfig().getSparkConfig()
                .forEach((k, v) -> logBuilder.append(k).append(":").append(v).append(" \n"));
            logBuilder.append(startLog("提交作业开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 提交作业
        if (workEvent.getEventProcess() == 6) {

            // 获取上下文参数
            SubmitWorkReq submitWorkReq = workRunContext.getSubmitWorkReq();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 请求代理
            AgentLinkResponse agentLinkResponse =
                agentLinkUtils.getAgentLinkResponse(agentNode, SparkAgentUrl.SUBMIT_WORK_URL, submitWorkReq);

            // 获取appId
            logBuilder.append(endLog("提交作业成功 : " + agentLinkResponse.getAppId()));

            // 保存实例
            workInstance.setSparkStarRes(JSON.toJSONString(agentLinkResponse));

            // 保存上下文
            workRunContext.setAppId(agentLinkResponse.getAppId());

            // 保存日志
            logBuilder.append(startLog("监听作业状态"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 监听作业状态
        if (workEvent.getEventProcess() == 7) {

            // 获取上下文参数
            String preStatus = workRunContext.getPreStatus() == null ? "" : workRunContext.getPreStatus();
            String appId = workRunContext.getAppId();
            String clusterType = workRunContext.getClusterType();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 获取作业状态并保存
            GetWorkStatusReq getWorkStatusReq = GetWorkStatusReq.builder().appId(appId).clusterType(clusterType)
                .sparkHomePath(agentNode.getSparkHomePath()).build();

            // 请求代理
            AgentLinkResponse agentLinkResponse =
                agentLinkUtils.getAgentLinkResponse(agentNode, SparkAgentUrl.GET_WORK_INFO_URL, getWorkStatusReq);

            // 如果是yarn的话，FinalStatus是undefine的话，使用status状态
            if (AgentType.YARN.equals(clusterType) && "UNDEFINED".equalsIgnoreCase(agentLinkResponse.getFinalState())) {
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
            List<String> runningStatus =
                Arrays.asList("ACCEPTED", "RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING", "PENDING");
            if (runningStatus.contains(agentLinkResponse.getFinalState().toUpperCase())) {
                return InstanceStatus.RUNNING;
            }

            // 如果是中止，直接退出
            List<String> abortStatus = Arrays.asList("KILLED", "TERMINATING");
            if (abortStatus.contains(agentLinkResponse.getFinalState().toUpperCase())) {
                throw errorLogException("作业运行中止");
            }

            // 其他状态则为运行结束
            logBuilder.append(startLog("保存日志和数据开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 保存日志和数据
        if (workEvent.getEventProcess() == 8) {

            // 获取上下文参数
            String appId = workRunContext.getAppId();
            String clusterType = workRunContext.getClusterType();
            String preStatus = workRunContext.getPreStatus();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // 获取日志并保存
            GetWorkStderrLogReq getWorkStderrLogReq = GetWorkStderrLogReq.builder().appId(appId)
                .clusterType(clusterType).sparkHomePath(agentNode.getSparkHomePath()).build();

            // 请求代理
            AgentLinkResponse agentLinkResponse = agentLinkUtils.getAgentLinkResponse(agentNode,
                SparkAgentUrl.GET_WORK_STDERR_LOG_URL, getWorkStderrLogReq);

            // 保存运行日志
            workInstance.setYarnLog(agentLinkResponse.getLog());
            logBuilder.append(endLog("保存日志完成"));

            // 如果运行成功，还要继续保存数据
            List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");
            if (successStatus.contains(preStatus.toUpperCase())) {

                // 获取数据
                GetWorkDataReq getWorkDataReq = GetWorkDataReq.builder().appId(appId).clusterType(clusterType)
                    .sparkHomePath(agentNode.getSparkHomePath()).build();

                // 请求代理
                agentLinkResponse =
                    agentLinkUtils.getAgentLinkResponse(agentNode, SparkAgentUrl.GET_WORK_DATA_URL, getWorkDataReq);

                // 保存数据
                workInstance.setResultData(JSON.toJSONString(agentLinkResponse));

                // 保存日志
                logBuilder.append(endLog("保存数据成功"));
            } else {
                // 其他状态为异常
                workRunContext.setPreStatus(InstanceStatus.FAIL);
            }

            // 保存日志
            logBuilder.append(startLog("清理缓存文件开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 清理缓存文件
        if (workEvent.getEventProcess() == 9) {

            // 获取上下文参数
            String appId = workRunContext.getAppId();
            String clusterType = workRunContext.getClusterType();
            ClusterNodeEntity agentNode = workRunContext.getAgentNode();

            // k8s作业要关闭作业
            if (AgentType.K8S.equals(clusterType)) {
                StopWorkReq stopWorkReq = StopWorkReq.builder().appId(appId).clusterType(AgentType.K8S)
                    .sparkHomePath(agentNode.getAgentHomePath()).agentHomePath(agentNode.getAgentHomePath()).build();

                // 请求代理
                agentLinkUtils.getAgentLinkResponse(agentNode, SparkAgentUrl.STOP_WORK_URL, stopWorkReq);
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
        if (workEvent.getEventProcess() < 6) {
            return true;
        }

        // 运行完毕
        if (workEvent.getEventProcess() > 7) {
            return false;
        }

        // 如果能获取appId则尝试直接杀死
        WorkRunContext workRunContext = JSON.parseObject(workEvent.getEventContext(), WorkRunContext.class);
        if (!Strings.isEmpty(workRunContext.getAppId())) {

            StopWorkReq stopWorkReq =
                StopWorkReq.builder().appId(workRunContext.getAppId()).clusterType(workRunContext.getClusterType())
                    .sparkHomePath(workRunContext.getAgentNode().getSparkHomePath())
                    .agentHomePath(workRunContext.getAgentNode().getAgentHomePath()).build();

            // 请求代理
            agentLinkUtils.getAgentLinkResponse(workRunContext.getAgentNode(), SparkAgentUrl.STOP_WORK_URL,
                stopWorkReq);
        }

        // 可以中止
        return true;
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

