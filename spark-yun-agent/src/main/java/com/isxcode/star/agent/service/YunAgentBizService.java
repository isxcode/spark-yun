package com.isxcode.star.agent.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.agent.run.AgentService;
import com.isxcode.star.api.agent.pojos.req.*;
import com.isxcode.star.api.agent.pojos.res.*;
import com.isxcode.star.api.agent.pojos.req.YagExecuteWorkReq;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 代理服务层.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YunAgentBizService {

    private final ApplicationContext applicationContext;

    public AgentService getAgentService(String agentType) {

        Optional<AgentService> agentServiceOptional = applicationContext.getBeansOfType(AgentService.class).values()
            .stream().filter(agent -> agent.getAgentName().equals(agentType)).findFirst();

        if (!agentServiceOptional.isPresent()) {
            throw new IsxAppException("agent类型不支持");
        }

        return agentServiceOptional.get();
    }

    public ExecuteWorkRes executeWork(YagExecuteWorkReq yagExecuteWorkReq) {

        AgentService agentService = getAgentService(yagExecuteWorkReq.getAgentType());
        try {
            SparkLauncher sparkLauncher = agentService.genSparkLauncher(yagExecuteWorkReq);
            String appId = agentService.executeWork(sparkLauncher);
            return ExecuteWorkRes.builder().appId(appId).build();
        } catch (IOException e) {
            throw new IsxAppException(e.getMessage());
        }
    }

    public YagGetStatusRes getStatus(String appId, String agentType, String sparkHomePath) {

        AgentService agentService = getAgentService(agentType);
        try {
            String appStatus = agentService.getAppStatus(appId, sparkHomePath);
            return YagGetStatusRes.builder().appId(appId).appStatus(appStatus).build();
        } catch (IOException e) {
            throw new IsxAppException(e.getMessage());
        }
    }

    public YagGetLogRes getLog(String appId, String agentType, String sparkHomePath) {

        AgentService agentService = getAgentService(agentType);
        try {
            String appLog = agentService.getAppLog(appId, sparkHomePath);
            return YagGetLogRes.builder().log(appLog).build();
        } catch (IOException e) {
            throw new IsxAppException(e.getMessage());
        }
    }

    public YagGetStdoutLogRes getStdoutLog(String appId, String agentType, String sparkHomePath) {

        AgentService agentService = getAgentService(agentType);
        String appLog;
        try {
            appLog = agentService.getStdoutLog(appId, sparkHomePath);
        } catch (IOException e) {
            throw new IsxAppException(e.getMessage());
        }

        // 只截取后1行的日志
        appLog = appLog.replace("End of LogType:stdout", "").replace("LogType:stdout-start", "");
        String[] split = appLog.split("\n");
        List<String> list = Arrays.asList(split);
        if (list.size() > 1) {
            list = list.subList(list.size() - 1, list.size());
        } else {
            list = list.subList(0, list.size());
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : list) {
            stringBuilder.append(str).append("\n");
        }
        return YagGetStdoutLogRes.builder().log(stringBuilder.toString()).build();
    }

    public YagGetDataRes getData(String appId, String agentType, String sparkHomePath) {

        AgentService agentService = getAgentService(agentType);
        String stdoutLog;
        try {
            stdoutLog = agentService.getAppData(appId, sparkHomePath);
        } catch (IOException e) {
            throw new IsxAppException(e.getMessage());
        }

        return YagGetDataRes.builder().data(JSON.parseArray(stdoutLog, List.class)).build();
    }

    public void stopJob(String appId, String agentType, String sparkHomePath, String agentHomePath) {

        AgentService agentService = getAgentService(agentType);
        try {
            agentService.killApp(appId, sparkHomePath, agentHomePath);
        } catch (IOException e) {
            throw new IsxAppException(e.getMessage());
        }
    }

    public static int findUnusedPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("未存在可使用端口号");
        }
    }

    public DeployContainerRes deployContainer(DeployContainerReq deployContainerReq) {

        SparkLauncher sparkLauncher;
        String appId;

        // 获取开放的端口号
        int port = findUnusedPort();
        deployContainerReq.getPluginReq().setContainerPort(port);

        AgentService agentService = getAgentService(deployContainerReq.getAgentType());
        try {
            sparkLauncher = agentService.genSparkLauncher(deployContainerReq);
            appId = agentService.executeWork(sparkLauncher);
            return DeployContainerRes.builder().appId(appId).port(port).build();
        } catch (IOException e) {
            throw new IsxAppException(e.getMessage());
        }
    }

    public ContainerCheckRes containerCheck(ContainerCheckReq containerCheckReq) {

        try {
            ResponseEntity<ContainerCheckRes> forEntity = new RestTemplate()
                .getForEntity("http://127.0.0.1:" + containerCheckReq.getPort() + "/check", ContainerCheckRes.class);
            return forEntity.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ContainerCheckRes.builder().code("500").msg(e.getMessage()).build();
        }
    }

    public ContainerGetDataRes executeContainerSql(ExecuteContainerSqlReq executeContainerSqlReq) {

        ContainerGetDataReq containerGetDataReq =
            ContainerGetDataReq.builder().sql(executeContainerSqlReq.getSql()).build();

        try {
            ResponseEntity<ContainerGetDataRes> forEntity =
                new RestTemplate().postForEntity("http://127.0.0.1:" + executeContainerSqlReq.getPort() + "/getData",
                    containerGetDataReq, ContainerGetDataRes.class);
            return forEntity.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            return ContainerGetDataRes.builder().code("500").msg(e.getMessage()).build();
        }
    }
}
