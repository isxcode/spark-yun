package com.isxcode.spark.agent.run.flink.impl;

import com.alibaba.fastjson2.JSON;
import com.isxcode.spark.agent.run.flink.FlinkAgentService;
import com.isxcode.spark.api.agent.constants.AgentType;
import com.isxcode.spark.api.agent.req.flink.GetWorkInfoReq;
import com.isxcode.spark.api.agent.req.flink.GetWorkLogReq;
import com.isxcode.spark.api.agent.req.flink.StopWorkReq;
import com.isxcode.spark.api.agent.req.flink.SubmitWorkReq;
import com.isxcode.spark.api.agent.res.flink.*;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.client.deployment.StandaloneClusterDescriptor;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class FlinkStandaloneAgentService implements FlinkAgentService {

    @Override
    public String getAgentType() {
        return AgentType.StandAlone;
    }

    public Configuration genConfiguration(String flinkHome) {

        // 获取本地flink的配置，并从中获取rest.port、rest.address，如果获取不到默认8081、localhost
        flinkHome = !Strings.isEmpty(flinkHome) ? flinkHome : System.getenv("FLINK_HOME");
        String flinkConfigPath = flinkHome + File.separator + "conf" + File.separator + "flink-conf.yaml";

        try (InputStream inputStream = Files.newInputStream(new File(flinkConfigPath).toPath())) {
            Yaml yaml = new Yaml();
            Map<String, Object> flinkYaml = yaml.load(inputStream);
            String restAddress = String.valueOf(flinkYaml.getOrDefault("rest.address", "localhost"));
            String restPort = String.valueOf(flinkYaml.getOrDefault("rest.port", "8081"));

            // 添加配置
            Configuration configuration = new Configuration();
            configuration.setString(RestOptions.ADDRESS, restAddress);
            configuration.setInteger(RestOptions.PORT, Integer.parseInt(restPort));
            return configuration;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("获取flink配置文件异常", e);
        }
    }

    @Override
    public SubmitWorkRes submitWork(SubmitWorkReq submitWorkReq) throws Exception {

        Configuration configuration = genConfiguration(submitWorkReq.getFlinkHome());

        // 设置作业名称
        configuration.set(PipelineOptions.NAME, submitWorkReq.getFlinkSubmit().getAppName() + "-"
            + submitWorkReq.getWorkType() + "-" + submitWorkReq.getWorkId() + "-" + submitWorkReq.getWorkInstanceId());

        List<URL> userClassPaths = new ArrayList<>();

        // 添加自定义依赖
        if (submitWorkReq.getLibConfig() != null) {
            for (int i = 0; i < submitWorkReq.getLibConfig().size(); i++) {
                userClassPaths.add(new File(submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + submitWorkReq.getLibConfig().get(i) + ".jar").toURI().toURL());
            }
        }

        // 添加自定义函数
        if (submitWorkReq.getFuncConfig() != null) {
            for (int i = 0; i < submitWorkReq.getFuncConfig().size(); i++) {
                userClassPaths.add(new File(submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + submitWorkReq.getFuncConfig().get(i).getFileId() + ".jar").toURI().toURL());
            }
        }

        submitWorkReq.getFlinkSubmit().getConf().forEach((k, v) -> {
            if (v instanceof String) {
                configuration.setString(k, String.valueOf(v));
            } else if (v instanceof Boolean) {
                configuration.setBoolean(k, Boolean.parseBoolean(String.valueOf(v)));
            } else if (v instanceof Double) {
                configuration.setDouble(k, Double.parseDouble(String.valueOf(v)));
            } else if (v instanceof Integer) {
                configuration.setInteger(k, Integer.parseInt(String.valueOf(v)));
            } else if (v instanceof Long) {
                configuration.setLong(k, Long.parseLong(String.valueOf(v)));
            } else {
                throw new IllegalArgumentException("Unsupported type for key: " + k + ", value: " + v);
            }
        });

        PackagedProgram program;
        if (WorkType.FLINK_JAR.equals(submitWorkReq.getWorkType())) {
            PackagedProgram.Builder builder = PackagedProgram.newBuilder()
                .setJarFile(new File((submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + submitWorkReq.getFlinkSubmit().getAppResource())))
                .setEntryPointClassName(submitWorkReq.getFlinkSubmit().getEntryClass()).setConfiguration(configuration)
                .setArguments(submitWorkReq.getPluginReq().getArgs()).setUserClassPaths(userClassPaths);
            if (configuration.get(SavepointConfigOptions.SAVEPOINT_PATH) != null) {
                program = builder
                    .setSavepointRestoreSettings(
                        SavepointRestoreSettings.forPath(configuration.get(SavepointConfigOptions.SAVEPOINT_PATH)))
                    .build();
            } else {
                program = builder.build();
            }
        } else {
            PackagedProgram.Builder builder = PackagedProgram.newBuilder()
                .setJarFile(new File((submitWorkReq.getAgentHomePath() + File.separator + "plugins" + File.separator
                    + submitWorkReq.getFlinkSubmit().getAppResource())))
                .setEntryPointClassName(submitWorkReq.getFlinkSubmit().getEntryClass()).setConfiguration(configuration)
                .setArguments(
                    Base64.getEncoder().encodeToString(JSON.toJSONString(submitWorkReq.getPluginReq()).getBytes()))
                .setUserClassPaths(userClassPaths);
            if (configuration.get(SavepointConfigOptions.SAVEPOINT_PATH) != null) {
                program = builder.setSavepointRestoreSettings(
                    SavepointRestoreSettings.forPath(configuration.getString(SavepointConfigOptions.SAVEPOINT_PATH)))
                    .build();
            } else {
                program = builder.build();
            }
        }

        try (
            StandaloneClusterDescriptor standaloneClusterDescriptor = new StandaloneClusterDescriptor(configuration);) {
            ClusterClient<StandaloneClusterId> clusterClient =
                standaloneClusterDescriptor.retrieve(StandaloneClusterId.getInstance()).getClusterClient();

            JobGraph jobGraph = PackagedProgramUtils.createJobGraph(program, configuration, 1, false);
            JobID jobID = clusterClient.submitJob(jobGraph).get();
            return SubmitWorkRes.builder().appId(jobID.toHexString()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e.getCause() != null && e.getCause().getCause() != null
                && e.getCause().getCause().getMessage() != null) {
                throw new Exception(e.getCause().getCause().getMessage());
            }
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public GetWorkInfoRes getWorkInfo(GetWorkInfoReq getWorkInfoReq) throws Exception {

        Configuration configuration = genConfiguration(getWorkInfoReq.getFlinkHome());

        try (StandaloneClusterDescriptor standaloneClusterDescriptor = new StandaloneClusterDescriptor(configuration)) {
            ClusterClient<StandaloneClusterId> clusterClient =
                standaloneClusterDescriptor.retrieve(StandaloneClusterId.getInstance()).getClusterClient();

            CompletableFuture<JobStatus> jobStatus =
                clusterClient.getJobStatus(JobID.fromHexString(getWorkInfoReq.getAppId()));

            return GetWorkInfoRes.builder().appId(getWorkInfoReq.getAppId()).finalState(jobStatus.get().name()).build();
        }
    }

    @Override
    public GetWorkLogRes getWorkLog(GetWorkLogReq getWorkLogReq) throws Exception {

        Configuration configuration = genConfiguration(getWorkLogReq.getFlinkHome());
        String restUrl =
            configuration.getString(RestOptions.ADDRESS) + ":" + configuration.getInteger(RestOptions.PORT);

        // 判断作业是否成功
        String status;
        try (StandaloneClusterDescriptor standaloneClusterDescriptor = new StandaloneClusterDescriptor(configuration)) {
            ClusterClient<StandaloneClusterId> clusterClient =
                standaloneClusterDescriptor.retrieve(StandaloneClusterId.getInstance()).getClusterClient();

            CompletableFuture<JobStatus> jobStatus =
                clusterClient.getJobStatus(JobID.fromHexString(getWorkLogReq.getAppId()));
            status = jobStatus.get().name();
        }

        if ("FAILED".equals(status)) {
            String getExceptionUrl = "http://" + restUrl + "/jobs/" + getWorkLogReq.getAppId() + "/exceptions";
            ResponseEntity<FlinkRestExceptionRes> exceptionResult =
                new RestTemplate().getForEntity(getExceptionUrl, FlinkRestExceptionRes.class);
            if (!HttpStatus.OK.equals(exceptionResult.getStatusCode())) {
                throw new IsxAppException("提交作业失败");
            }
            if (exceptionResult.getBody() == null) {
                throw new IsxAppException("提交作业失败");
            }
            return GetWorkLogRes.builder().log(exceptionResult.getBody().getRootException()).build();
        } else {

            String taskManagersUrl = "http://" + restUrl + "/taskmanagers";
            ResponseEntity<FlinkGetTaskManagerRes> forEntity =
                new RestTemplate().getForEntity(taskManagersUrl, FlinkGetTaskManagerRes.class);

            // 查询taskmanager的日志
            String taskmanagerId = forEntity.getBody().getTaskManagers().get(0).getId();
            String getLogUrl = "http://" + restUrl + "/taskmanagers/" + taskmanagerId + "/log";
            ResponseEntity<String> log = new RestTemplate().getForEntity(getLogUrl, String.class);

            String logRegex = "job " + getWorkLogReq.getAppId()
                + " from resource manager with leader id.*?Close JobManager connection for job "
                + getWorkLogReq.getAppId();
            Pattern pattern = Pattern.compile(logRegex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(Objects.requireNonNull(log.getBody()));
            if (matcher.find()) {
                String matchedLog = matcher.group();
                return GetWorkLogRes.builder().log(matchedLog).build();
            }
            return GetWorkLogRes.builder().log("").build();
        }
    }

    @Override
    public StopWorkRes stopWork(StopWorkReq stopWorkReq) throws Exception {

        Configuration configuration = genConfiguration(stopWorkReq.getFlinkHome());

        try (StandaloneClusterDescriptor standaloneClusterDescriptor = new StandaloneClusterDescriptor(configuration)) {
            ClusterClient<StandaloneClusterId> clusterClient =
                standaloneClusterDescriptor.retrieve(StandaloneClusterId.getInstance()).getClusterClient();

            CompletableFuture<Acknowledge> cancel = clusterClient.cancel(JobID.fromHexString(stopWorkReq.getAppId()));
            return StopWorkRes.builder().requestId(cancel.toString()).build();
        }
    }
}
