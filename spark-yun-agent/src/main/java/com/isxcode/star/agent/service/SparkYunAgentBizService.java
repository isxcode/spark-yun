package com.isxcode.star.agent.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.agent.run.AgentFactory;
import com.isxcode.star.agent.run.AgentService;
import com.isxcode.star.api.agent.req.*;
import com.isxcode.star.api.agent.res.*;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SparkYunAgentBizService {

    private final AgentFactory agentFactory;

    public SubmitWorkRes submitWork(SubmitWorkReq submitWorkReq) {

        AgentService agentService = agentFactory.getAgentService(submitWorkReq.getClusterType());
        try {
            SparkLauncher sparkLauncher = agentService.getSparkLauncher(submitWorkReq);
            String appId = agentService.submitWork(sparkLauncher);
            return SubmitWorkRes.builder().appId(appId).build();
        } catch (IsxAppException e) {
            log.error(e.getMsg(), e);
            throw new IsxAppException(e.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetWorkStatusRes getWorkStatus(GetWorkStatusReq getWorkStatusReq) {

        AgentService agentService = agentFactory.getAgentService(getWorkStatusReq.getClusterType());
        try {
            String appStatus =
                agentService.getWorkStatus(getWorkStatusReq.getAppId(), getWorkStatusReq.getSparkHomePath());
            return GetWorkStatusRes.builder().appId(getWorkStatusReq.getAppId()).appStatus(appStatus).build();
        } catch (IsxAppException e) {
            log.error(e.getMsg(), e);
            throw new IsxAppException(e.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetWorkStderrLogRes getWorkStderrLog(GetWorkStderrLogReq getWorkStderrLogReq) {

        AgentService agentService = agentFactory.getAgentService(getWorkStderrLogReq.getClusterType());
        try {
            String appLog =
                agentService.getStderrLog(getWorkStderrLogReq.getAppId(), getWorkStderrLogReq.getSparkHomePath());
            return GetWorkStderrLogRes.builder().log(appLog).build();
        } catch (IsxAppException e) {
            log.error(e.getMsg(), e);
            throw new IsxAppException(e.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetWorkStdoutLogRes getAllWorkStdoutLog(GetWorkStdoutLogReq getWorkStdoutLogReq) {

        AgentService agentService = agentFactory.getAgentService(getWorkStdoutLogReq.getClusterType());
        String appLog;
        try {
            appLog = agentService.getStdoutLog(getWorkStdoutLogReq.getAppId(), getWorkStdoutLogReq.getSparkHomePath());
        } catch (IsxAppException e) {
            log.error(e.getMsg(), e);
            throw new IsxAppException(e.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
        return GetWorkStdoutLogRes.builder().log(appLog).build();
    }

    public GetWorkStdoutLogRes getCustomWorkStdoutLog(GetWorkStdoutLogReq getWorkStdoutLogReq) {

        AgentService agentService = agentFactory.getAgentService(getWorkStdoutLogReq.getClusterType());
        String appLog;
        try {
            appLog = agentService.getCustomWorkStdoutLog(getWorkStdoutLogReq.getAppId(),
                getWorkStdoutLogReq.getSparkHomePath());
        } catch (IsxAppException e) {
            log.error(e.getMsg(), e);
            throw new IsxAppException(e.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
        return GetWorkStdoutLogRes.builder().log(appLog).build();
    }

    public GetWorkStdoutLogRes getWorkStdoutLog(GetWorkStdoutLogReq getWorkStdoutLogReq) {

        AgentService agentService = agentFactory.getAgentService(getWorkStdoutLogReq.getClusterType());
        String appLog;
        try {
            appLog = agentService.getStdoutLog(getWorkStdoutLogReq.getAppId(), getWorkStdoutLogReq.getSparkHomePath());
        } catch (IsxAppException e) {
            log.error(e.getMsg(), e);
            throw new IsxAppException(e.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }

        // 只截取后1行的日志,用于打印
        appLog = appLog.replace("End of LogType:stdout", "").replace("LogType:stdout-start", "");
        String[] split = appLog.split("\n");
        List<String> list = Arrays.asList(split);
        list = list.subList(list.size() > 1 ? list.size() - 1 : 0, list.size());
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : list) {
            stringBuilder.append(str).append("\n");
        }
        return GetWorkStdoutLogRes.builder().log(stringBuilder.toString()).build();
    }

    public GetWorkDataRes getWorkData(GetWorkDataReq getWorkDataReq) {

        AgentService agentService = agentFactory.getAgentService(getWorkDataReq.getClusterType());
        try {
            String workDataStr =
                agentService.getWorkDataStr(getWorkDataReq.getAppId(), getWorkDataReq.getSparkHomePath());
            return GetWorkDataRes.builder().data(JSON.parseArray(workDataStr, List.class)).build();
        } catch (IsxAppException e) {
            log.error(e.getMsg(), e);
            throw new IsxAppException(e.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public void stopWork(StopWorkReq stopWorkReq) {

        AgentService agentService = agentFactory.getAgentService(stopWorkReq.getClusterType());
        try {
            agentService.stopWork(stopWorkReq.getAppId(), stopWorkReq.getSparkHomePath(),
                stopWorkReq.getAgentHomePath());
        } catch (IsxAppException e) {
            log.error(e.getMsg(), e);
            throw new IsxAppException(e.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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

    public ExecuteContainerSqlRes executeContainerSql(ExecuteContainerSqlReq executeContainerSqlReq) {

        ContainerGetDataReq containerGetDataReq =
            ContainerGetDataReq.builder().sql(executeContainerSqlReq.getSql()).build();

        try {
            ResponseEntity<ExecuteContainerSqlRes> forEntity =
                new RestTemplate().postForEntity("http://127.0.0.1:" + executeContainerSqlReq.getPort() + "/getData",
                    containerGetDataReq, ExecuteContainerSqlRes.class);
            return forEntity.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ExecuteContainerSqlRes.builder().code("500").msg(e.getMessage()).build();
        }
    }

    public DeployContainerRes deployContainer(SubmitWorkReq submitWorkReq) {

        AgentService agentService = agentFactory.getAgentService(submitWorkReq.getClusterType());
        try {
            int port = findUnusedPort();
            submitWorkReq.getPluginReq().setContainerPort(port);
            SparkLauncher sparkLauncher = agentService.getSparkLauncher(submitWorkReq);
            String appId = agentService.submitWork(sparkLauncher);
            return DeployContainerRes.builder().appId(appId).port(port).build();
        } catch (IsxAppException e) {
            log.error(e.getMsg(), e);
            throw new IsxAppException(e.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }
}
