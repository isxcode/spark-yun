package com.isxcode.spark.modules.work.run.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriteConfig;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.agent.constants.AgentType;
import com.isxcode.spark.api.agent.constants.SparkAgentUrl;
import com.isxcode.spark.api.agent.req.spark.*;
import com.isxcode.spark.api.agent.res.spark.GetWorkStderrLogRes;
import com.isxcode.spark.api.api.constants.PathConstants;
import com.isxcode.spark.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.spark.api.datasource.constants.DatasourceType;
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
import com.isxcode.spark.modules.file.service.FileService;
import com.isxcode.spark.modules.func.entity.FuncEntity;
import com.isxcode.spark.modules.func.mapper.FuncMapper;
import com.isxcode.spark.modules.func.repository.FuncRepository;
import com.isxcode.spark.modules.work.entity.WorkConfigEntity;
import com.isxcode.spark.modules.work.entity.WorkEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
import com.isxcode.spark.modules.work.repository.WorkRepository;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.sql.SqlCommentService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.work.sql.SqlValueService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.repository.VipWorkVersionRepository;
import org.quartz.Scheduler;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static com.isxcode.spark.common.utils.ssh.SshUtils.scpJar;

@Service
@Slf4j
public class ExcelSyncExecutor extends WorkExecutor {

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

    private final WorkEventRepository workEventRepository;

    private final SqlCommentService sqlCommentService;

    private final SqlValueService sqlValueService;

    private final SqlFunctionService sqlFunctionService;

    private final FileService fileService;

    public ExcelSyncExecutor(WorkInstanceRepository workInstanceRepository, ClusterRepository clusterRepository,
        ClusterNodeRepository clusterNodeRepository, WorkflowInstanceRepository workflowInstanceRepository,
        WorkRepository workRepository, WorkConfigRepository workConfigRepository, Locker locker,
        HttpUrlUtils httpUrlUtils, AesUtils aesUtils, ClusterNodeMapper clusterNodeMapper,
        DatasourceService datasourceService, IsxAppProperties isxAppProperties, FuncRepository funcRepository,
        FuncMapper funcMapper, FileRepository fileRepository, SqlCommentService sqlCommentService,
        SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService alarmService,
        FileService fileService, WorkEventRepository workEventRepository, Scheduler scheduler,
        WorkRunJobFactory workRunJobFactory, VipWorkVersionRepository vipWorkVersionRepository) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository);
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
        this.workEventRepository = workEventRepository;
        this.sqlCommentService = sqlCommentService;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.fileService = fileService;
    }

    @Override
    public String getWorkType() {
        return WorkType.EXCEL_SYNC_JDBC;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance, WorkEventEntity workEvent)
        throws Exception {

        WorkRunContext workEventBody = JSON.parseObject(workEvent.getEventContext(), WorkRunContext.class);
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // step1: env + build plugin and upload csv/libs/funcs
        if (processNeverRun(workEvent, 1)) {
            if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在 \n");
            }
            Optional<ClusterEntity> engineOpt =
                clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
            if (!engineOpt.isPresent()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在 \n");
            }
            List<ClusterNodeEntity> nodes =
                clusterNodeRepository.findAllByClusterIdAndStatus(engineOpt.get().getId(), ClusterNodeStatus.RUNNING);
            if (nodes.isEmpty()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群 \n");
            }
            if (workRunContext.getExcelSyncConfig().getColumnMap().isEmpty()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检查作业失败 : 请配置字段映射关系 \n");
            }
            ClusterNodeEntity engineNode = nodes.get(new Random().nextInt(nodes.size()));
            // 固定节点，复用文件上传所在节点
            workEventBody.setNodeId(engineNode.getId());
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("申请资源完成，激活节点:【")
                .append(engineNode.getName()).append("】\n");
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成 \n");
            workInstance = updateInstance(workInstance, logBuilder);

            SubmitWorkReq executeReq = new SubmitWorkReq();
            executeReq.setWorkId(workRunContext.getWorkId());
            executeReq.setWorkType(WorkType.EXCEL_SYNC_JDBC);
            executeReq.setWorkInstanceId(workInstance.getId());

            DatasourceEntity targetDatasource =
                datasourceService.getDatasource(workRunContext.getExcelSyncConfig().getTargetDBId());
            DatasourceConfig targetConfig = DatasourceConfig.builder()
                .driver(datasourceService.getDriverClass(targetDatasource.getDbType()))
                .url(targetDatasource.getJdbcUrl()).dbTable(workRunContext.getExcelSyncConfig().getTargetTable())
                .user(targetDatasource.getUsername()).password(aesUtils.decrypt(targetDatasource.getPasswd())).build();
            workRunContext.getExcelSyncConfig().setTargetDatabase(targetConfig);

            SparkSubmit sparkSubmit =
                SparkSubmit.builder().verbose(true).mainClass("com.isxcode.spark.plugin.excelSync.jdbc.Execute")
                    .appResource("spark-excel-sync-jdbc-plugin.jar")
                    .conf(genSparkSubmitConfig(workRunContext.getClusterConfig().getSparkConfig())).build();

            if (!Strings.isEmpty(workRunContext.getExcelSyncConfig().getQueryCondition())) {
                String sqlNoComment =
                    sqlCommentService.removeSqlComment(workRunContext.getExcelSyncConfig().getQueryCondition());
                String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);
                String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);
                String conditionScript = sqlFunctionService.parseSqlFunction(parseValueSql);
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("过滤条件: \n")
                    .append(conditionScript).append("\n");
                workInstance = updateInstance(workInstance, logBuilder);
                workRunContext.getExcelSyncConfig().setQueryCondition(conditionScript);
            }

            PluginReq pluginReq = PluginReq.builder().excelSyncConfig(workRunContext.getExcelSyncConfig())
                .sparkConfig(genSparkConfig(workRunContext.getClusterConfig().getSparkConfig()))
                .syncRule(workRunContext.getSyncRule()).agentType(engineOpt.get().getClusterType()).build();

            ScpFileEngineNodeDto scp = clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
            scp.setPasswd(aesUtils.decrypt(scp.getPasswd()));

            String sourceFileId;
            if (workRunContext.getExcelSyncConfig().isFileReplace()) {
                String fileName =
                    sqlFunctionService.parseSqlFunction(workRunContext.getExcelSyncConfig().getFilePattern());
                Optional<FileEntity> fileEntityOptional = fileRepository.findByFileName(fileName);
                if (!fileEntityOptional.isPresent()) {
                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + fileName + "不存在 \n");
                }
                sourceFileId = fileEntityOptional.get().getId();
            } else {
                sourceFileId = workRunContext.getExcelSyncConfig().getSourceFileId();
            }

            FileEntity file = fileService.getFile(sourceFileId);
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO)
                .append("当前同步的excel文件:" + file.getFileName() + " \n");
            workInstance = updateInstance(workInstance, logBuilder);

            String csvFileName = sourceFileId + ".csv";
            String filePath = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
                + File.separator + file.getTenantId() + File.separator + file.getId();
            String csvFilePath = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator
                + "file" + File.separator + file.getTenantId() + File.separator + csvFileName;
            ExcelReader reader = ExcelUtil.getReader(FileUtil.file(filePath));
            List<List<Object>> read = reader.read();
            CsvWriteConfig.defaultConfig().setFieldSeparator(',').setTextDelimiter(';');
            CsvWriter writer = CsvUtil.getWriter(FileUtil.file(csvFilePath), StandardCharsets.UTF_8);
            if (!workRunContext.getExcelSyncConfig().isHasHeader()) {
                List<String> row = new ArrayList<>();
                for (int i = 0; i < workRunContext.getExcelSyncConfig().getSourceTableColumn().size(); i++) {
                    row.add("col" + i);
                }
                writer.write(row.toArray(new String[0]));
            }
            for (List<Object> rowMeta : read) {
                List<String> row = new ArrayList<>();
                rowMeta.forEach(e -> row.add(String.valueOf(e)));
                writer.write(row.toArray(new String[0]));
            }
            reader.close();
            writer.close();

            try {
                scpJar(scp, csvFilePath, engineNode.getAgentHomePath() + "/zhiqingyun-agent/file/" + csvFileName);
                FileUtil.del(csvFilePath);
                pluginReq.setCsvFilePath(engineNode.getAgentHomePath() + File.separator + "zhiqingyun-agent"
                    + File.separator + "file" + File.separator + csvFileName);
                pluginReq.setCsvFileName(csvFileName);
            } catch (JSchException | SftpException | InterruptedException | IOException ex) {
                FileUtil.del(csvFilePath);
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "excel文件上传失败\n");
            }

            String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
                + File.separator + file.getTenantId();
            if (workRunContext.getFuncConfig() != null) {
                List<FuncEntity> allFunc = funcRepository.findAllById(workRunContext.getFuncConfig());
                allFunc.forEach(e -> {
                    try {
                        scpJar(scp, fileDir + File.separator + e.getFileId(),
                            engineNode.getAgentHomePath() + "/zhiqingyun-agent/file/" + e.getFileId() + ".jar");
                    } catch (JSchException | SftpException | InterruptedException | IOException ex) {
                        throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO
                            + " : 自定义函数jar文件上传失败，请检查文件是否上传或者重新上传" + ex.getMessage() + "\n");
                    }
                });
                pluginReq.setFuncInfoList(funcMapper.funcEntityListToFuncInfoList(allFunc));
                executeReq.setFuncConfig(funcMapper.funcEntityListToFuncInfoList(allFunc));
            }
            if (workRunContext.getLibConfig() != null) {
                List<FileEntity> libFile = fileRepository.findAllById(workRunContext.getLibConfig());
                libFile.forEach(e -> {
                    try {
                        scpJar(scp, fileDir + File.separator + e.getId(),
                            engineNode.getAgentHomePath() + "/zhiqingyun-agent/file/" + e.getId() + ".jar");
                    } catch (JSchException | SftpException | InterruptedException | IOException ex) {
                        throw new WorkRunException(
                            LocalDateTime.now() + WorkLog.ERROR_INFO + "自定义依赖jar文件上传失败，请检查文件是否上传或者重新上传\n");
                    }
                });
                executeReq.setLibConfig(workRunContext.getLibConfig());
            }

            if (DatasourceType.HIVE.equals(targetDatasource.getDbType())) {
                pluginReq.getSparkConfig().put("hive.metastore.uris", targetDatasource.getMetastoreUris());
                if (Strings.isNotBlank(targetDatasource.getUsername())) {
                    sparkSubmit.getConf().put("qing.hive.username", targetDatasource.getUsername());
                }
            }

            executeReq.setSparkSubmit(sparkSubmit);
            executeReq.setPluginReq(pluginReq);
            executeReq.setAgentHomePath(engineNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME);
            executeReq.setSparkHomePath(engineNode.getSparkHomePath());
            executeReq.setClusterType(engineOpt.get().getClusterType());

            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("构建作业完成 \n");
            workRunContext.getClusterConfig().getSparkConfig().forEach((k, v) -> logBuilder.append(LocalDateTime.now())
                .append(WorkLog.SUCCESS_INFO).append(k).append(":").append(v).append(" \n"));
            workInstance = updateInstance(workInstance, logBuilder);

            // 保存上下文（固定的节点ID等），第二步重建提交参数
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }

        // step2: 提交作业并保存appId
        if (processNeverRun(workEvent, 2)) {
            Optional<ClusterEntity> engineOpt =
                clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
            Optional<ClusterNodeEntity> engineNodeOpt = clusterNodeRepository.findById(workEventBody.getNodeId());
            if (!engineNodeOpt.isPresent()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "固定节点不存在或已下线\n");
            }
            ClusterNodeEntity engineNode = engineNodeOpt.get();

            // 重建提交参数（不再进行scp上传）
            SubmitWorkReq executeReq = new SubmitWorkReq();
            executeReq.setWorkId(workRunContext.getWorkId());
            executeReq.setWorkType(WorkType.EXCEL_SYNC_JDBC);
            executeReq.setWorkInstanceId(workInstance.getId());

            // 目标库配置
            DatasourceEntity targetDatasource =
                datasourceService.getDatasource(workRunContext.getExcelSyncConfig().getTargetDBId());
            DatasourceConfig targetConfig = DatasourceConfig.builder()
                .driver(datasourceService.getDriverClass(targetDatasource.getDbType()))
                .url(targetDatasource.getJdbcUrl())
                .dbTable(workRunContext.getExcelSyncConfig().getTargetTable())
                .user(targetDatasource.getUsername())
                .password(aesUtils.decrypt(targetDatasource.getPasswd())).build();
            workRunContext.getExcelSyncConfig().setTargetDatabase(targetConfig);

            SparkSubmit sparkSubmit =
                SparkSubmit.builder().verbose(true).mainClass("com.isxcode.spark.plugin.excelSync.jdbc.Execute")
                    .appResource("spark-excel-sync-jdbc-plugin.jar")
                    .conf(genSparkSubmitConfig(workRunContext.getClusterConfig().getSparkConfig())).build();

            // 计算csv文件名（与step1一致）
            String sourceFileId;
            if (workRunContext.getExcelSyncConfig().isFileReplace()) {
                String fileName = sqlFunctionService.parseSqlFunction(workRunContext.getExcelSyncConfig().getFilePattern());
                Optional<FileEntity> fileEntityOptional = fileRepository.findByFileName(fileName);
                if (!fileEntityOptional.isPresent()) {
                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + fileName + "不存在 \n");
                }
                sourceFileId = fileEntityOptional.get().getId();
            } else {
                sourceFileId = workRunContext.getExcelSyncConfig().getSourceFileId();
            }
            String csvFileName = sourceFileId + ".csv";

            PluginReq pluginReq = PluginReq.builder().excelSyncConfig(workRunContext.getExcelSyncConfig())
                .sparkConfig(genSparkConfig(workRunContext.getClusterConfig().getSparkConfig()))
                .syncRule(workRunContext.getSyncRule()).agentType(engineOpt.get().getClusterType()).build();
            pluginReq.setCsvFilePath(engineNode.getAgentHomePath() + File.separator + "zhiqingyun-agent" + File.separator + "file" + File.separator + csvFileName);
            pluginReq.setCsvFileName(csvFileName);

            if (workRunContext.getFuncConfig() != null) {
                List<FuncEntity> allFunc = funcRepository.findAllById(workRunContext.getFuncConfig());
                pluginReq.setFuncInfoList(funcMapper.funcEntityListToFuncInfoList(allFunc));
                executeReq.setFuncConfig(funcMapper.funcEntityListToFuncInfoList(allFunc));
            }
            if (workRunContext.getLibConfig() != null) {
                executeReq.setLibConfig(workRunContext.getLibConfig());
            }

            executeReq.setSparkSubmit(sparkSubmit);
            executeReq.setPluginReq(pluginReq);
            executeReq.setAgentHomePath(engineNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME);
            executeReq.setSparkHomePath(engineNode.getSparkHomePath());
            executeReq.setClusterType(engineOpt.get().getClusterType());

            Integer lock = locker.lock("REQUEST_" + workInstance.getId());
            try {
                BaseResponse<?> baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(),
                    engineNode.getAgentPort(), SparkAgentUrl.SUBMIT_WORK_URL), executeReq, BaseResponse.class);
                if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode()) || baseResponse.getData() == null) {
                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
                }
                RunWorkRes submitWorkRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("提交作业成功 : ")
                    .append(submitWorkRes.getAppId()).append("\n");
                workInstance.setSparkStarRes(JSON.toJSONString(submitWorkRes));
                workInstance = updateInstance(workInstance, logBuilder);
                workEventBody.setContainerId(submitWorkRes.getAppId());
                workEventBody.setCurrentStatus("");
                workEvent.setEventContext(JSON.toJSONString(workEventBody));
                workEventRepository.saveAndFlush(workEvent);
            } finally {
                locker.unlock(lock);
            }
            return InstanceStatus.RUNNING;
        }

        // step3: 查询状态（一次心跳）
        Optional<ClusterEntity> engineOpt =
            clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
        Optional<ClusterNodeEntity> engineNodeOpt = clusterNodeRepository.findById(workEventBody.getNodeId());
        if (!engineNodeOpt.isPresent()) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "固定节点不存在或已下线\n");
        }
        ClusterNodeEntity engineNode = engineNodeOpt.get();
        GetWorkStatusReq getWorkStatusReq = GetWorkStatusReq.builder().appId(workEventBody.getContainerId())
            .clusterType(engineOpt.get().getClusterType()).sparkHomePath(engineNode.getSparkHomePath()).build();
        BaseResponse<?> baseResponse = HttpUtils.doPost(
            httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), SparkAgentUrl.GET_WORK_STATUS_URL),
            getWorkStatusReq, BaseResponse.class);
        if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
            throw new WorkRunException(
                LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业状态异常 : " + baseResponse.getMsg() + "\n");
        }
        RunWorkRes workStatusRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
        workInstance.setSparkStarRes(JSON.toJSONString(workStatusRes));
        if (!Objects.equals(workEventBody.getCurrentStatus(), workStatusRes.getAppStatus())) {
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:")
                .append(workStatusRes.getAppStatus()).append("\n");
            workEventBody.setCurrentStatus(workStatusRes.getAppStatus());
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }
        workInstance = updateInstance(workInstance, logBuilder);
        List<String> running = Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING", "PENDING",
            "TERMINATING", "INITIALIZING");
        if (running.contains(workStatusRes.getAppStatus().toUpperCase())) {
            return InstanceStatus.RUNNING;
        }

        // step4: 获取日志并判断最终态
        if ("KILLED".equalsIgnoreCase(workStatusRes.getAppStatus())
            || "TERMINATING".equalsIgnoreCase(workStatusRes.getAppStatus())) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业运行中止\n");
        }
        GetWorkStderrLogReq getWorkStderrLogReq = GetWorkStderrLogReq.builder().appId(workEventBody.getContainerId())
            .clusterType(engineOpt.get().getClusterType()).sparkHomePath(engineNode.getSparkHomePath()).build();
        baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
            SparkAgentUrl.GET_WORK_STDERR_LOG_URL), getWorkStderrLogReq, BaseResponse.class);
        if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
            throw new WorkRunException(
                LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业日志异常 : " + baseResponse.getMsg() + "\n");
        }
        GetWorkStderrLogRes yagGetLogRes =
            JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetWorkStderrLogRes.class);
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("日志保存成功 \n");
        if (yagGetLogRes != null) {
            workInstance.setYarnLog(yagGetLogRes.getLog());
        }
        updateInstance(workInstance, logBuilder);
        if (AgentType.K8S.equals(engineOpt.get().getClusterType())) {
            StopWorkReq stopWorkReq =
                StopWorkReq.builder().appId(workEventBody.getContainerId()).clusterType(AgentType.K8S)
                    .sparkHomePath(engineNode.getSparkHomePath()).agentHomePath(engineNode.getAgentHomePath()).build();
            HttpUtils.doPost(
                httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), SparkAgentUrl.STOP_WORK_URL),
                stopWorkReq, BaseResponse.class);
        }
        List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");
        if (!successStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常\n");
        }
        return InstanceStatus.SUCCESS;
    }

    @Override
    protected void abort(WorkInstanceEntity workInstance) throws Exception {

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
                            LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群 \n");
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
                    Thread t = WORK_THREAD.get(workInstance.getId());
                    if (t != null) {
                        t.interrupt();
                    }
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
