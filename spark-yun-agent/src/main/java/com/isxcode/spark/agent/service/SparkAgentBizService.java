package com.isxcode.spark.agent.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.agent.run.spark.SparkAgentFactory;
import com.isxcode.spark.agent.run.spark.SparkAgentService;
import com.isxcode.spark.api.agent.req.spark.*;
import com.isxcode.spark.api.agent.res.spark.*;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
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
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SparkAgentBizService {

    private final SparkAgentFactory agentFactory;

    public SubmitWorkRes submitWork(SubmitWorkReq submitWorkReq) {

        try {
            SparkAgentService agentService = agentFactory.getAgentService(submitWorkReq.getClusterType());
            SparkLauncher sparkLauncher = agentService.getSparkLauncher(submitWorkReq);
            String appId = agentService.submitWork(sparkLauncher);
            return SubmitWorkRes.builder().appId(appId).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public SubmitWorkRes submitWorkForPySpark(SubmitWorkReq submitWorkReq) {

        try {
            SparkAgentService agentService = agentFactory.getAgentService(submitWorkReq.getClusterType());
            SparkLauncher sparkLauncher = agentService.getSparkLauncher(submitWorkReq);
            Map<String, String> result = agentService.submitWorkForPySpark(sparkLauncher);
            return SubmitWorkRes.builder().result(result).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetWorkInfoRes getWorkInfo(GetWorkStatusReq getWorkStatusReq) {

        try {
            SparkAgentService agentService = agentFactory.getAgentService(getWorkStatusReq.getClusterType());
            return agentService.getWorkInfo(getWorkStatusReq.getAppId(), getWorkStatusReq.getSparkHomePath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetWorkStderrLogRes getWorkStderrLog(GetWorkStderrLogReq getWorkStderrLogReq) {

        try {
            SparkAgentService agentService = agentFactory.getAgentService(getWorkStderrLogReq.getClusterType());
            String appLog =
                agentService.getStderrLog(getWorkStderrLogReq.getAppId(), getWorkStderrLogReq.getSparkHomePath());
            return GetWorkStderrLogRes.builder().log(appLog).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetWorkStdoutLogRes getWorkStdoutLog(GetWorkStdoutLogReq getWorkStdoutLogReq) {

        try {
            SparkAgentService agentService = agentFactory.getAgentService(getWorkStdoutLogReq.getClusterType());
            String appLog =
                agentService.getStdoutLog(getWorkStdoutLogReq.getAppId(), getWorkStdoutLogReq.getSparkHomePath());
            return GetWorkStdoutLogRes.builder().log(appLog).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetWorkStdoutLogRes getCustomJarWorkStdoutLog(GetWorkStdoutLogReq getWorkStdoutLogReq) {

        try {
            SparkAgentService agentService = agentFactory.getAgentService(getWorkStdoutLogReq.getClusterType());
            String appLog = agentService.getCustomJarStdoutLog(getWorkStdoutLogReq.getAppId(),
                getWorkStdoutLogReq.getSparkHomePath());
            return GetWorkStdoutLogRes.builder().log(appLog).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }

    }

    public GetWorkStdoutLogRes getLastLineWorkStdoutLog(GetWorkStdoutLogReq getWorkStdoutLogReq) {

        try {
            SparkAgentService agentService = agentFactory.getAgentService(getWorkStdoutLogReq.getClusterType());
            String appLog =
                agentService.getStdoutLog(getWorkStdoutLogReq.getAppId(), getWorkStdoutLogReq.getSparkHomePath());

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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetWorkDataRes getWorkData(GetWorkDataReq getWorkDataReq) {

        try {
            SparkAgentService agentService = agentFactory.getAgentService(getWorkDataReq.getClusterType());
            String workDataStr =
                agentService.getWorkDataStr(getWorkDataReq.getAppId(), getWorkDataReq.getSparkHomePath());
            return GetWorkDataRes.builder().data(JSON.parseArray(workDataStr, List.class)).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public void stopWork(StopWorkReq stopWorkReq) {

        try {
            SparkAgentService agentService = agentFactory.getAgentService(stopWorkReq.getClusterType());
            agentService.stopWork(stopWorkReq.getAppId(), stopWorkReq.getSparkHomePath(),
                stopWorkReq.getAgentHomePath());
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
            return new RestTemplate()
                .getForEntity("http://127.0.0.1:" + containerCheckReq.getPort() + "/check", ContainerCheckRes.class)
                .getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ContainerCheckRes.builder().code("500").msg(e.getMessage()).build();
        }
    }

    public ExecuteContainerSqlRes executeContainerSql(ExecuteContainerSqlReq executeContainerSqlReq) {

        try {
            ContainerGetDataReq containerGetDataReq =
                ContainerGetDataReq.builder().sql(executeContainerSqlReq.getSql()).build();
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

        try {
            SparkAgentService agentService = agentFactory.getAgentService(submitWorkReq.getClusterType());
            int port = findUnusedPort();
            submitWorkReq.getPluginReq().setContainerPort(port);
            SparkLauncher sparkLauncher = agentService.getSparkLauncher(submitWorkReq);
            String appId = agentService.submitWork(sparkLauncher);
            return DeployContainerRes.builder().appId(appId).port(port).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }
}
