package com.isxcode.star.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.agent.constants.AgentUrl;
import com.isxcode.star.api.agent.req.ExecuteContainerSqlReq;
import com.isxcode.star.api.agent.res.ExecuteContainerSqlRes;
import com.isxcode.star.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.star.api.container.constants.ContainerStatus;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.backend.api.base.exceptions.WorkRunException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.modules.alarm.service.AlarmService;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.container.entity.ContainerEntity;
import com.isxcode.star.modules.container.repository.ContainerRepository;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.work.run.WorkExecutor;
import com.isxcode.star.modules.work.run.WorkRunContext;
import com.isxcode.star.modules.work.sql.SqlCommentService;
import com.isxcode.star.modules.work.sql.SqlFunctionService;
import com.isxcode.star.modules.work.sql.SqlValueService;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
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

    public SparkContainerSqlExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, ContainerRepository containerRepository,
        ClusterNodeRepository clusterNodeRepository, IsxAppProperties isxAppProperties,
        WorkInstanceRepository workInstanceRepository1, SqlCommentService sqlCommentService,
        SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService alarmService) {
        super(workInstanceRepository, workflowInstanceRepository, alarmService, sqlFunctionService);
        this.containerRepository = containerRepository;
        this.clusterNodeRepository = clusterNodeRepository;
        this.isxAppProperties = isxAppProperties;
        this.workInstanceRepository = workInstanceRepository1;
        this.sqlCommentService = sqlCommentService;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
    }

    @Override
    public String getWorkType() {
        return WorkType.SPARK_CONTAINER_SQL;
    }

    public void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

        // 将线程存到Map
        WORK_THREAD.put(workInstance.getId(), Thread.currentThread());

        // 获取日志构造器
        StringBuilder logBuilder = workRunContext.getLogBuilder();

        // 检测数据源是否配置
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测运行环境 \n");
        if (Strings.isEmpty(workRunContext.getContainerId())) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效容器  \n");
        }

        // 检查数据源是否存在
        Optional<ContainerEntity> containerEntityOptional =
            containerRepository.findById(workRunContext.getContainerId());
        if (!containerEntityOptional.isPresent()) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 有效容器不存在  \n");
        }
        if (!ContainerStatus.RUNNING.equals(containerEntityOptional.get().getStatus())) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: Spark容器已停止,请重新启动 \n");
        }

        // 容器检查通过
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成  \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 检查脚本是否为空
        if (Strings.isEmpty(workRunContext.getScript())) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Sql内容为空 \n");
        }

        // 脚本检查通过
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始解析作业 \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 调用代理的接口，获取数据
        try {

            // 获取集群节点
            List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
                .findAllByClusterIdAndStatus(containerEntityOptional.get().getClusterId(), ClusterNodeStatus.RUNNING);
            if (allEngineNodes.isEmpty()) {
                throw new WorkRunException("集群不存在可用节点");
            }

            // 节点选择随机数
            ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

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

            // 再次调用容器的check接口，确认容器是否成功启动
            ExecuteContainerSqlReq executeContainerSqlReq = ExecuteContainerSqlReq.builder()
                .port(String.valueOf(containerEntityOptional.get().getPort())).sql(script).build();

            ExecuteContainerSqlRes containerGetDataRes;
            try {
                containerGetDataRes = new RestTemplate().postForEntity(
                    genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentUrl.EXECUTE_CONTAINER_SQL_URL),
                    executeContainerSqlReq, ExecuteContainerSqlRes.class).getBody();
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new WorkRunException(e.getMessage());
            }

            if (!"200".equals(containerGetDataRes.getCode())) {
                if (containerGetDataRes.getMsg().contains("Connection refused (Connection refused)")) {
                    throw new WorkRunException("运行异常: 请检查容器的运行状态");
                }
                String errMsg = containerGetDataRes.getMsg();
                throw new WorkRunException("运行异常" + errMsg.substring(19, errMsg.length() - 1).replace("<EOL>", "\n"));
            }

            // 记录结束执行时间
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("SQL执行成功  \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 讲data转为json存到实例中
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功  \n");
            workInstance.setSubmitLog(logBuilder.toString());
            workInstance.setResultData(JSON.toJSONString(containerGetDataRes.getData()));
            workInstanceRepository.saveAndFlush(workInstance);
        } catch (WorkRunException e) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMsg() + "\n");
        }
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
}
