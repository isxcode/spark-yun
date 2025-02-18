package com.isxcode.star.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.agent.constants.AgentType;
import com.isxcode.star.api.agent.constants.AgentUrl;
import com.isxcode.star.api.agent.req.*;
import com.isxcode.star.api.agent.res.GetWorkStderrLogRes;
import com.isxcode.star.api.agent.res.GetWorkStdoutLogRes;
import com.isxcode.star.api.api.constants.PathConstants;
import com.isxcode.star.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.star.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.api.work.dto.ClusterConfig;
import com.isxcode.star.api.work.res.RunWorkRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.exceptions.WorkRunException;
import com.isxcode.star.backend.api.base.pojos.BaseResponse;
import com.isxcode.star.common.locker.Locker;
import com.isxcode.star.common.utils.aes.AesUtils;
import com.isxcode.star.common.utils.http.HttpUrlUtils;
import com.isxcode.star.common.utils.http.HttpUtils;
import com.isxcode.star.modules.alarm.service.AlarmService;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.work.repository.WorkRepository;
import com.isxcode.star.modules.work.run.WorkExecutor;
import com.isxcode.star.modules.work.run.WorkRunContext;
import com.isxcode.star.modules.work.sql.SqlFunctionService;
import com.isxcode.star.modules.work.sql.SqlValueService;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static com.isxcode.star.common.utils.ssh.SshUtils.scpText;

@Service
@Slf4j
public class PySparkExecutor extends WorkExecutor {

    private final WorkInstanceRepository workInstanceRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final ClusterRepository clusterRepository;

    private final SqlValueService sqlValueService;

    private final Locker locker;

    private final HttpUrlUtils httpUrlUtils;

    private final SqlFunctionService sqlFunctionService;

    private final WorkRepository workRepository;

    private final WorkConfigRepository workConfigRepository;

    public PySparkExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, WorkInstanceRepository workInstanceRepository1,
        ClusterNodeRepository clusterNodeRepository, ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils,
        ClusterRepository clusterRepository, SqlValueService sqlValueService, SqlFunctionService sqlFunctionService,
        AlarmService alarmService, Locker locker, HttpUrlUtils httpUrlUtils, WorkRepository workRepository,
        WorkConfigRepository workConfigRepository) {

        super(workInstanceRepository, workflowInstanceRepository, alarmService, sqlFunctionService);
        this.workInstanceRepository = workInstanceRepository1;
        this.clusterNodeRepository = clusterNodeRepository;
        this.clusterNodeMapper = clusterNodeMapper;
        this.aesUtils = aesUtils;
        this.clusterRepository = clusterRepository;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.locker = locker;
        this.httpUrlUtils = httpUrlUtils;
        this.workRepository = workRepository;
        this.workConfigRepository = workConfigRepository;
    }

    @Override
    public String getWorkType() {
        return WorkType.PY_SPARK;
    }

    public void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

        // 将线程存到Map
        WORK_THREAD.put(workInstance.getId(), Thread.currentThread());

        // 获取日志构造器
        StringBuilder logBuilder = workRunContext.getLogBuilder();

        // 判断执行脚本是否为空
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测脚本内容 \n");
        if (Strings.isEmpty(workRunContext.getScript())) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测脚本失败 : PYTHON内容为空不能执行  \n");
        }

        // 禁用rm指令
        if (Pattern.compile("\\brm\\b", Pattern.CASE_INSENSITIVE).matcher(workRunContext.getScript()).find()) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测语句失败 : PYTHON内容包含rm指令不能执行  \n");
        }

        // 检测计算集群是否存在
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测集群 \n");
        if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测集群失败 : 计算引擎未配置 \n");
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

        // 解析上游参数
        String jsonPathSql = parseJsonPath(workRunContext.getScript(), workInstance);

        // 翻译脚本中的系统变量
        String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

        // 翻译脚本中的系统函数
        String script = sqlFunctionService.parseSqlFunction(parseValueSql);
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("Python脚本: \n").append(script)
            .append("\n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 开始构造PluginReq
        PluginReq pluginReq =
            PluginReq.builder().sparkConfig(workRunContext.getClusterConfig().getSparkConfig()).build();

        // 将脚本推送到指定集群节点中
        ScpFileEngineNodeDto scpFileEngineNodeDto =
            clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));
        try {
            // 上传脚本
            scpText(scpFileEngineNodeDto, script,
                engineNode.getAgentHomePath() + "/zhiqingyun-agent/works/" + workInstance.getId() + ".py");

            workInstance = updateInstance(workInstance, logBuilder);
        } catch (JSchException | SftpException | InterruptedException e) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业异常 : " + e.getMessage() + "\n");
        }

        // 脚本检查通过
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 开始构造SparkSubmit
        SparkSubmit sparkSubmit = SparkSubmit.builder().verbose(true).mainClass("")
            .appResource(engineNode.getAgentHomePath() + "/zhiqingyun-agent/works/" + workInstance.getId() + ".py")
            .conf(genSparkSubmitConfig(workRunContext.getClusterConfig().getSparkConfig())).build();

        // 开始构建作业
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始构建作业  \n");

        // 开始构造executeReq
        SubmitWorkReq executeReq = new SubmitWorkReq();
        executeReq.setWorkId(workRunContext.getWorkId());
        executeReq.setWorkType(WorkType.PY_SPARK);
        executeReq.setWorkInstanceId(workInstance.getId());
        executeReq.setSparkSubmit(sparkSubmit);
        executeReq.setPluginReq(pluginReq);
        executeReq.setAgentHomePath(engineNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME);
        executeReq.setSparkHomePath(engineNode.getSparkHomePath());
        executeReq.setClusterType(calculateEngineEntityOptional.get().getClusterType());

        // 构建作业完成，并打印作业配置信息
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("构建作业完成 \n");
        workRunContext.getClusterConfig().getSparkConfig().forEach((k, v) -> logBuilder.append(LocalDateTime.now())
            .append(WorkLog.SUCCESS_INFO).append(k).append(":").append(v).append(" \n"));
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始提交作业  \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 开始提交作业
        BaseResponse<?> baseResponse;

        // 加锁，必须等待作业提交成功后才能中止
        Integer lock = locker.lock("REQUEST_" + workInstance.getId());
        RunWorkRes submitWorkRes;
        try {
            baseResponse = HttpUtils.doPost(
                httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentUrl.SUBMIT_WORK_URL),
                executeReq, BaseResponse.class);
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
            submitWorkRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("提交作业成功 : ")
                .append(submitWorkRes.getAppId()).append("\n");
            workInstance.setSparkStarRes(JSON.toJSONString(submitWorkRes));
            workInstance = updateInstance(workInstance, logBuilder);
        } catch (ResourceAccessException e) {
            log.error(e.getMessage(), e);
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + e.getMessage() + "\n");
        } catch (HttpServerErrorException e1) {
            log.error(e1.getMessage(), e1);
            if (HttpStatus.BAD_GATEWAY.value() == e1.getRawStatusCode()) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : 无法访问节点服务器,请检查服务器防火墙或者计算集群\n");
            }
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + e1.getMessage() + "\n");
        } finally {
            locker.unlock(lock);
        }

        // 提交作业成功后，开始循环判断状态
        String oldStatus = "";
        while (true) {

            // 获取作业状态并保存
            GetWorkStatusReq getWorkStatusReq = GetWorkStatusReq.builder().appId(submitWorkRes.getAppId())
                .clusterType(calculateEngineEntityOptional.get().getClusterType())
                .sparkHomePath(executeReq.getSparkHomePath()).build();
            baseResponse = HttpUtils.doPost(
                httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentUrl.GET_WORK_STATUS_URL),
                getWorkStatusReq, BaseResponse.class);
            log.debug("获取远程获取状态日志:{}", baseResponse.toString());

            if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业状态异常 : " + baseResponse.getMsg() + "\n");
            }

            // 解析返回状态，并保存
            RunWorkRes workStatusRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
            workInstance.setSparkStarRes(JSON.toJSONString(workStatusRes));

            // 状态发生变化，则添加日志状态
            if (!oldStatus.equals(workStatusRes.getAppStatus())) {
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:")
                    .append(workStatusRes.getAppStatus()).append("\n");
            }
            oldStatus = workStatusRes.getAppStatus();
            workInstance = updateInstance(workInstance, logBuilder);

            // 如果状态是运行中，更新日志，继续执行
            List<String> runningStatus =
                Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING", "PENDING");
            if (runningStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "睡眠线程异常 : " + e.getMessage() + "\n");
                }
            } else {
                // 运行结束逻辑

                // 如果是中止，直接退出
                if ("KILLED".equals(workStatusRes.getAppStatus().toUpperCase())
                    || "TERMINATING".equals(workStatusRes.getAppStatus().toUpperCase())) {
                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业运行中止" + "\n");
                }

                // 获取日志并保存
                GetWorkStderrLogReq getWorkStderrLogReq = GetWorkStderrLogReq.builder().appId(submitWorkRes.getAppId())
                    .clusterType(calculateEngineEntityOptional.get().getClusterType())
                    .sparkHomePath(executeReq.getSparkHomePath()).build();
                baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
                    AgentUrl.GET_WORK_STDERR_LOG_URL), getWorkStderrLogReq, BaseResponse.class);
                log.debug("获取远程返回日志:{}", baseResponse.toString());

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
                workInstance = updateInstance(workInstance, logBuilder);

                // 如果运行成功，则保存返回数据
                List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");
                log.debug("sparkSql返回执行状态:{}", workStatusRes.getAppStatus().toUpperCase());
                if (successStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {
                    // 如果是k8s类型，需要删除k8s容器
                    if (AgentType.K8S.equals(calculateEngineEntityOptional.get().getClusterType())) {
                        StopWorkReq stopWorkReq = StopWorkReq.builder().appId(submitWorkRes.getAppId())
                            .clusterType(AgentType.K8S).sparkHomePath(engineNode.getSparkHomePath())
                            .agentHomePath(engineNode.getAgentHomePath()).build();
                        HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
                            AgentUrl.STOP_WORK_URL), stopWorkReq, BaseResponse.class);
                    }

                    // 获取打印日志
                    GetWorkStdoutLogReq getWorkStdoutLogReq =
                        GetWorkStdoutLogReq.builder().appId(submitWorkRes.getAppId())
                            .clusterType(calculateEngineEntityOptional.get().getClusterType())
                            .sparkHomePath(executeReq.getSparkHomePath()).build();
                    baseResponse =
                        HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
                            AgentUrl.GET_ALL_WORK_STDOUT_LOG_URL), getWorkStdoutLogReq, BaseResponse.class);
                    if (String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                        GetWorkStdoutLogRes getWorkStderrLogRes =
                            JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetWorkStdoutLogRes.class);
                        workInstance.setResultData(getWorkStderrLogRes.getLog());
                    } else {
                        workInstance
                            .setResultData(Strings.isEmpty(baseResponse.getMsg()) ? "请查看运行日志" : baseResponse.getMsg());
                    }

                    updateInstance(workInstance, logBuilder);
                } else {
                    if (AgentType.K8S.equals(calculateEngineEntityOptional.get().getClusterType())) {
                        StopWorkReq stopWorkReq = StopWorkReq.builder().appId(submitWorkRes.getAppId())
                            .clusterType(AgentType.K8S).sparkHomePath(engineNode.getSparkHomePath())
                            .agentHomePath(engineNode.getAgentHomePath()).build();
                        HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
                            AgentUrl.STOP_WORK_URL), stopWorkReq, BaseResponse.class);
                    }

                    // 任务运行错误
                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常" + "\n");
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
                    StopWorkReq stopWorkReq = StopWorkReq.builder().appId(wokRunWorkRes.getAppId())
                        .clusterType(cluster.getClusterType()).sparkHomePath(engineNode.getSparkHomePath())
                        .agentHomePath(engineNode.getAgentHomePath()).build();
                    BaseResponse<?> baseResponse = HttpUtils.doPost(httpUrlUtils.genHttpUrl(engineNode.getHost(),
                        engineNode.getAgentPort(), AgentUrl.STOP_WORK_URL), stopWorkReq, BaseResponse.class);

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
