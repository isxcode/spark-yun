package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.agent.constants.SparkAgentUrl;
import com.isxcode.spark.api.agent.req.spark.ExecuteContainerSqlReq;
import com.isxcode.spark.api.agent.res.spark.ExecuteContainerSqlRes;
import com.isxcode.spark.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.spark.api.container.constants.ContainerStatus;
import com.isxcode.spark.api.work.constants.WorkLog;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.spark.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.spark.modules.container.entity.ContainerEntity;
import com.isxcode.spark.modules.container.repository.ContainerRepository;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.sql.SqlCommentService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.work.sql.SqlValueService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.repository.VipWorkVersionRepository;
import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
import com.isxcode.spark.modules.work.repository.WorkRepository;
import com.isxcode.spark.common.locker.Locker;
import org.quartz.Scheduler;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class SparkContainerSqlExecutor extends WorkExecutor {

    private final ContainerRepository containerRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final IsxAppProperties isxAppProperties;

    private final WorkInstanceRepository workInstanceRepository;

    private final SqlCommentService sqlCommentService;

    private final SqlValueService sqlValueService;

    private final SqlFunctionService sqlFunctionService;

    private final WorkEventRepository workEventRepository;

    private final Scheduler scheduler;

    private final WorkRunJobFactory workRunJobFactory;

    private final VipWorkVersionRepository vipWorkVersionRepository;

    private final WorkConfigRepository workConfigRepository;

    private final WorkRepository workRepository;

    private final Locker locker;

    public SparkContainerSqlExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, ContainerRepository containerRepository,
        ClusterNodeRepository clusterNodeRepository, IsxAppProperties isxAppProperties,
        WorkInstanceRepository workInstanceRepository1, SqlCommentService sqlCommentService,
        SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService alarmService,
        WorkEventRepository workEventRepository, Scheduler scheduler, WorkRunJobFactory workRunJobFactory,
        VipWorkVersionRepository vipWorkVersionRepository, WorkConfigRepository workConfigRepository,
        WorkRepository workRepository, Locker locker) {
        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository);
        this.containerRepository = containerRepository;
        this.clusterNodeRepository = clusterNodeRepository;
        this.isxAppProperties = isxAppProperties;
        this.workInstanceRepository = workInstanceRepository1;
        this.sqlCommentService = sqlCommentService;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.workEventRepository = workEventRepository;
        this.scheduler = scheduler;
        this.workRunJobFactory = workRunJobFactory;
        this.vipWorkVersionRepository = vipWorkVersionRepository;
        this.workConfigRepository = workConfigRepository;
        this.workRepository = workRepository;
        this.locker = locker;
    }

    @Override
    public String getWorkType() {
        return WorkType.SPARK_CONTAINER_SQL;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
        WorkEventEntity workEvent) {

        // 获取作业运行上下文
        SparkContainerSqlExecutorContext workEventBody =
            JSON.parseObject(workEvent.getEventContext(), SparkContainerSqlExecutorContext.class);
        if (workEventBody == null) {
            workEventBody = new SparkContainerSqlExecutorContext();
            workEventBody.setWorkRunContext(workRunContext);
        }
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 检查容器配置是否合法，并保存容器信息
        if (processNeverRun(workEvent, 1)) {

            // 检测数据源是否配置
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测运行环境 \n");
            if (Strings.isEmpty(workRunContext.getContainerId())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效容器 \n");
            }

            // 检查数据源是否存在
            Optional<ContainerEntity> containerEntityOptional =
                containerRepository.findById(workRunContext.getContainerId());
            if (!containerEntityOptional.isPresent()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 有效容器不存在 \n");
            }
            if (!ContainerStatus.RUNNING.equals(containerEntityOptional.get().getStatus())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: Spark容器已停止,请重新启动 \n");
            }

            // 容器检查通过
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成 \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 保存容器信息
            workEventBody.setContainerEntity(containerEntityOptional.get());
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }

        // 检查脚本和集群节点，保存节点信息
        if (processNeverRun(workEvent, 2)) {

            // 检查脚本是否为空
            if (Strings.isEmpty(workRunContext.getScript())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Sql内容为空 \n");
            }

            // 脚本检查通过
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始解析作业 \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 获取集群节点
            ContainerEntity containerEntity = workEventBody.getContainerEntity();
            List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
                .findAllByClusterIdAndStatus(containerEntity.getClusterId(), ClusterNodeStatus.RUNNING);
            if (allEngineNodes.isEmpty()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "集群不存在可用节点 \n");
            }

            // 节点选择随机数
            ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

            // 保存节点信息
            workEventBody.setEngineNode(engineNode);
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }

        // SQL处理，保存处理后的脚本
        if (processNeverRun(workEvent, 3)) {

            // 去掉sql中的注释
            String sqlNoComment = sqlCommentService.removeSqlComment(workRunContext.getScript());

            // 解析上游参数
            String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);

            // 翻译sql中的系统变量
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

            // 翻译sql中的系统函数
            String script = sqlFunctionService.parseSqlFunction(parseValueSql);

            // 打印sql
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行SQL: \n").append(script)
                .append("\n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 保存处理后的脚本
            workEventBody.setScript(script);
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }

        // 执行SQL，保存执行结果
        if (processNeverRun(workEvent, 4)) {

            // 获取处理后的信息
            String script = workEventBody.getScript();
            ContainerEntity containerEntity = workEventBody.getContainerEntity();
            ClusterNodeEntity engineNode = workEventBody.getEngineNode();

            // 调用容器的接口，执行SQL
            ExecuteContainerSqlReq executeContainerSqlReq =
                ExecuteContainerSqlReq.builder().port(String.valueOf(containerEntity.getPort())).sql(script).build();

            ExecuteContainerSqlRes containerGetDataRes;
            try {
                containerGetDataRes =
                    new RestTemplate().postForEntity(
                        genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(),
                            SparkAgentUrl.EXECUTE_CONTAINER_SQL_URL),
                        executeContainerSqlReq, ExecuteContainerSqlRes.class).getBody();
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMessage() + "\n");
            }

            if (!"200".equals(containerGetDataRes.getCode())) {
                if (containerGetDataRes.getMsg().contains("Connection refused (Connection refused)")) {
                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "运行异常: 请检查容器的运行状态 \n");
                }
                String errMsg = containerGetDataRes.getMsg();
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "运行异常"
                    + errMsg.substring(19, errMsg.length() - 1).replace("<EOL>", "\n") + "\n");
            }

            // 记录结束执行时间
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("SQL执行成功 \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 将data转为json存到实例中
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功 \n");
            workInstance.setSubmitLog(logBuilder.toString());
            workInstance.setResultData(JSON.toJSONString(containerGetDataRes.getData()));
            workInstanceRepository.saveAndFlush(workInstance);

            // 保存执行结果
            workEventBody.setContainerGetDataRes(containerGetDataRes);
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }

        return InstanceStatus.SUCCESS;
    }

    @Override
    protected void abort(WorkInstanceEntity workInstance) {

        Thread thread = WORK_THREAD.get(workInstance.getId());
        thread.interrupt();
    }

    public String genHttpUrl(String host, String port, String path) {

        String httpProtocol = isxAppProperties.isUseSsl() ? "https://" : "http://";
        String httpHost = isxAppProperties.isUsePort() ? host + ":" + port : host;

        return httpProtocol + httpHost + path;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SparkContainerSqlExecutorContext {

        private WorkRunContext workRunContext;

        private ContainerEntity containerEntity;

        private ClusterNodeEntity engineNode;

        private String script;

        private ExecuteContainerSqlRes containerGetDataRes;
    }
}
