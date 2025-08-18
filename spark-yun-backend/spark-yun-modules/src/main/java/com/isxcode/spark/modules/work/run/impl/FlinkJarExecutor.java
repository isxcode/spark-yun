package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.agent.constants.AgentType;
import com.isxcode.spark.api.agent.constants.FlinkAgentUrl;
import com.isxcode.spark.api.agent.req.flink.*;
import com.isxcode.spark.api.agent.res.flink.GetWorkInfoRes;
import com.isxcode.spark.api.agent.res.flink.GetWorkLogRes;
import com.isxcode.spark.api.agent.res.flink.SubmitWorkRes;
import com.isxcode.spark.api.api.constants.PathConstants;
import com.isxcode.spark.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.spark.api.work.constants.WorkLog;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.api.work.dto.ClusterConfig;
import com.isxcode.spark.api.work.dto.JarJobConfig;
import com.isxcode.spark.api.work.res.RunWorkRes;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
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
import com.isxcode.spark.modules.datasource.service.DatasourceService;
import com.isxcode.spark.modules.file.entity.FileEntity;
import com.isxcode.spark.modules.file.repository.FileRepository;
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
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.isxcode.spark.common.utils.ssh.SshUtils.scpJar;

@Service
@Slf4j
public class FlinkJarExecutor extends WorkExecutor {

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

    private final SqlFunctionService sqlFunctionService;

    public FlinkJarExecutor(WorkInstanceRepository workInstanceRepository, ClusterRepository clusterRepository,
        ClusterNodeRepository clusterNodeRepository, WorkflowInstanceRepository workflowInstanceRepository,
        WorkRepository workRepository, WorkConfigRepository workConfigRepository, Locker locker,
        HttpUrlUtils httpUrlUtils, FuncRepository funcRepository, FuncMapper funcMapper,
        ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, IsxAppProperties isxAppProperties,
        FileRepository fileRepository, DatasourceService datasourceService, AlarmService alarmService,
        SqlFunctionService sqlFunctionService) {

        super(workInstanceRepository, workflowInstanceRepository, alarmService, sqlFunctionService);
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
        this.sqlFunctionService = sqlFunctionService;
    }

    @Override
    public String getWorkType() {
        return WorkType.FLINK_JAR;
    }

    @Override
    protected void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

        // 将线程存到Map
        WORK_THREAD.put(workInstance.getId(), Thread.currentThread());

        // 获取日志构造器
        StringBuilder logBuilder = workRunContext.getLogBuilder();

        // 获取作业jar与lib文件信息并scp至所选节点
        if (workRunContext.getJarJobConfig() == null) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业配置异常 : 请检查作业配置 \n");
        }

        // 检测计算集群是否存在
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始申请资源 \n");
        if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎未配置  \n");
        }

        // 检查计算集群是否存在
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
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成  \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 开始构建作业
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始构建作业  \n");

        // 开始构造flinkSubmit
        SubmitWorkReq submitJobReq = new SubmitWorkReq();
        submitJobReq.setClusterType(calculateEngineEntityOptional.get().getClusterType());
        submitJobReq.setAgentHomePath(engineNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME);
        submitJobReq.setFlinkHome(engineNode.getFlinkHomePath());
        submitJobReq.setWorkInstanceId(workInstance.getId());
        submitJobReq.setWorkType(workRunContext.getWorkType());

        JarJobConfig jarJobConfig = workRunContext.getJarJobConfig();

        PluginReq pluginReq = PluginReq.builder().args(jarJobConfig.getArgs()).build();
        submitJobReq.setPluginReq(pluginReq);

        // 获取jar
        Optional<FileEntity> fileEntityOptional = fileRepository.findById(jarJobConfig.getJarFileId());
        if (!fileEntityOptional.isPresent()) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Jar文件异常 : 请检查文件是否存在 \n");
        }
        FileEntity jarFile = fileEntityOptional.get();

        FlinkSubmit flinkSubmit =
            FlinkSubmit.builder().appName(jarJobConfig.getAppName()).entryClass(jarJobConfig.getMainClass())
                .appResource(jarFile.getId() + ".jar").conf(workRunContext.getClusterConfig().getFlinkConfig()).build();
        submitJobReq.setFlinkSubmit(flinkSubmit);
        submitJobReq.setWorkId(workInstance.getWorkId());

        // 上传文件到制定节点路径 /file/id.jar
        ScpFileEngineNodeDto scpFileEngineNodeDto =
            clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));
        String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
            + File.separator + engineNode.getTenantId();
        try {
            scpJar(scpFileEngineNodeDto, fileDir + File.separator + jarFile.getId(),
                engineNode.getAgentHomePath() + "/zhiliuyun-agent/file/" + jarFile.getId() + ".jar");
        } catch (JSchException | SftpException | InterruptedException | IOException e) {
            log.debug(e.getMessage());
            throw new WorkRunException(
                LocalDateTime.now() + WorkLog.ERROR_INFO + "自定义作业jar文件上传失败，请检查文件是否上传或者重新上传\n" + e.getMessage());
        }

        // 上传依赖到制定节点路径
        if (workRunContext.getLibConfig() != null) {
            List<FileEntity> libFile = fileRepository.findAllById(workRunContext.getLibConfig());
            libFile.forEach(e -> {
                try {
                    scpJar(scpFileEngineNodeDto, fileDir + File.separator + e.getId(),
                        engineNode.getAgentHomePath() + "/zhiliuyun-agent/file/" + e.getId() + ".jar");
                } catch (JSchException | SftpException | InterruptedException | IOException ex) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "自定义依赖jar文件上传失败，请检查文件是否上传或者重新上传\n");
                }
            });
            submitJobReq.setLibConfig(workRunContext.getLibConfig());
        }

        // 构建作业完成，并打印作业配置信息
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("构建作业完成 \n");
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始提交作业 \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 开始提交作业
        BaseResponse<?> baseResponse;

        // 加锁，必须等待作业提交成功后才能中止
        Integer lock = locker.lock("REQUEST_" + workInstance.getId());
        SubmitWorkRes submitJobRes;
        try {
            baseResponse = HttpUtils.doPost(
                httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), FlinkAgentUrl.SUBMIT_WORK_URL),
                submitJobReq, BaseResponse.class);

            log.debug("获取远程提交作业日志:{}", baseResponse.toString());
            if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
            }
            // 解析返回对象,获取appId
            if (baseResponse.getData() == null) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
            }
            submitJobRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), SubmitWorkRes.class);
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("提交作业成功 : ")
                .append(submitJobRes.getAppId()).append("\n");
            workInstance.setSparkStarRes(JSON.toJSONString(submitJobRes));
            workInstance = updateInstance(workInstance, logBuilder);
        } catch (WorkRunException exception) {
            throw new WorkRunException(exception.getMsg());
        } catch (Exception e) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + e.getMessage() + "\n");
        } finally {
            locker.unlock(lock);
        }

        // 提交作业成功后，开始循环判断状态
        String oldStatus = "";
        while (true) {

            // 获取作业状态并保存
            GetWorkInfoReq jobInfoReq = GetWorkInfoReq.builder().agentHome(engineNode.getAgentHomePath())
                .flinkHome(engineNode.getFlinkHomePath()).appId(submitJobRes.getAppId())
                .clusterType(calculateEngineEntityOptional.get().getClusterType()).build();
            GetWorkInfoRes getJobInfoRes;
            try {
                baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
                    FlinkAgentUrl.GET_WORK_INFO_URL), jobInfoReq, BaseResponse.class);
            } catch (Exception e) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业状态异常 : " + e.getMessage() + "\n");
            }

            if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业状态异常 : " + baseResponse.getMsg() + "\n");
            }

            getJobInfoRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetWorkInfoRes.class);

            // 解析返回状态，并保存
            workInstance.setSparkStarRes(JSON.toJSONString(getJobInfoRes));

            // 状态发生变化，则添加日志状态
            if (!oldStatus.equals(getJobInfoRes.getStatus())) {
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:")
                    .append(getJobInfoRes.getStatus()).append("\n");
            }
            oldStatus = getJobInfoRes.getStatus();

            workInstance = updateInstance(workInstance, logBuilder);

            // 如果状态是运行中，更新日志，继续执行
            List<String> runningStatus = Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING",
                "PENDING", "TERMINATING", "INITIALIZING");
            if (runningStatus.contains(getJobInfoRes.getStatus().toUpperCase())) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "睡眠线程异常 : " + e.getMessage() + "\n");
                }
            } else {
                // 运行结束逻辑

                // 获取日志并保存
                GetWorkLogReq getJobLogReq = GetWorkLogReq.builder()
                    .agentHomePath(engineNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME)
                    .appId(submitJobRes.getAppId()).workInstanceId(workInstance.getId())
                    .flinkHome(engineNode.getFlinkHomePath()).workStatus(getJobInfoRes.getStatus())
                    .clusterType(calculateEngineEntityOptional.get().getClusterType()).build();

                baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
                    FlinkAgentUrl.GET_WORK_LOG_URL), getJobLogReq, BaseResponse.class);

                if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业日志异常 : " + baseResponse.getMsg() + "\n");
                }

                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("日志保存成功 \n");

                GetWorkLogRes getWorkLogRes =
                    JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetWorkLogRes.class);
                if (getWorkLogRes != null) {
                    workInstance.setYarnLog(getWorkLogRes.getLog());
                }
                updateInstance(workInstance, logBuilder);

                // 如果是中止，直接退出
                if ("KILLED".equalsIgnoreCase(getJobInfoRes.getStatus())
                    || "TERMINATED".equalsIgnoreCase(getJobInfoRes.getStatus())) {
                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业运行中止" + "\n");
                }

                // 如果运行成功，则保存返回数据
                List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED", "OVER");
                if (successStatus.contains(getJobInfoRes.getStatus().toUpperCase())) {
                    // 如果是k8s容器部署需要注意，成功状态需要通过日志中内容判断
                    if (AgentType.K8S.equals(calculateEngineEntityOptional.get().getClusterType())) {
                        if (Strings.isEmpty(workInstance.getYarnLog())) {
                            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业运行中止" + "\n");
                        } else if (workInstance.getYarnLog().contains("Caused by:")) {
                            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常" + "\n");
                        }
                    }
                    // yarn 状态是finish但是实际可能失败
                    if (AgentType.YARN.equals(calculateEngineEntityOptional.get().getClusterType())) {
                        if (workInstance.getYarnLog().contains("Caused by:")) {
                            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常" + "\n");
                        }
                    }
                } else {
                    if (AgentType.K8S.equals(calculateEngineEntityOptional.get().getClusterType())) {
                        if ("ERROR".equalsIgnoreCase(getJobInfoRes.getStatus())) {
                            // k8s失败主动停止，否则一直重试
                            StopWorkReq stopWorkReq = StopWorkReq.builder().flinkHome(engineNode.getFlinkHomePath())
                                .appId(submitJobRes.getAppId())
                                .clusterType(calculateEngineEntityOptional.get().getClusterType()).build();
                            new RestTemplate().postForObject(httpUrlUtils.genHttpUrl(engineNode.getHost(),
                                engineNode.getAgentPort(), FlinkAgentUrl.STOP_WORK_URL), stopWorkReq,
                                BaseResponse.class);
                        }
                    } else {
                        // 任务运行错误
                        throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常" + "\n");
                    }
                }

                // 运行结束，则退出死循环
                break;
            }
        }
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

                    StopWorkReq stopWorkReq = StopWorkReq.builder().flinkHome(engineNode.getFlinkHomePath())
                        .appId(wokRunWorkRes.getAppId()).clusterType(cluster.getClusterType()).build();

                    BaseResponse<?> baseResponse =
                        new RestTemplate().postForObject(httpUrlUtils.genHttpUrl(engineNode.getHost(),
                            engineNode.getAgentPort(), FlinkAgentUrl.STOP_WORK_URL), stopWorkReq, BaseResponse.class);

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
}
