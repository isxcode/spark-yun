package com.isxcode.spark.agent.run.flink.impl;

import com.alibaba.fastjson2.JSON;
import com.isxcode.spark.agent.run.flink.FlinkAgentService;
import com.isxcode.spark.api.agent.constants.AgentType;
import com.isxcode.spark.api.agent.req.flink.GetWorkInfoReq;
import com.isxcode.spark.api.agent.req.flink.GetWorkLogReq;
import com.isxcode.spark.api.agent.req.flink.StopWorkReq;
import com.isxcode.spark.api.agent.req.flink.SubmitWorkReq;
import com.isxcode.spark.api.agent.res.flink.GetWorkInfoRes;
import com.isxcode.spark.api.agent.res.flink.GetWorkLogRes;
import com.isxcode.spark.api.agent.res.flink.StopWorkRes;
import com.isxcode.spark.api.agent.res.flink.SubmitWorkRes;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.*;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlinkYarnAgentService implements FlinkAgentService {

    @Override
    public String getAgentType() {

        return AgentType.YARN;
    }

    @Override
    public SubmitWorkRes submitWork(SubmitWorkReq submitWorkReq) throws Exception {

        String appName = submitWorkReq.getFlinkSubmit().getAppName() + "-" + submitWorkReq.getWorkType() + "-"
            + submitWorkReq.getWorkId() + "-" + submitWorkReq.getWorkInstanceId();

        Configuration flinkConfig = GlobalConfiguration.loadConfiguration();
        flinkConfig.set(PipelineOptions.NAME, appName);
        flinkConfig.set(ApplicationConfiguration.APPLICATION_MAIN_CLASS,
            submitWorkReq.getFlinkSubmit().getEntryClass());
        if (WorkType.FLINK_JAR.equals(submitWorkReq.getWorkType())) {
            flinkConfig.set(PipelineOptions.JARS, singletonList(submitWorkReq.getAgentHomePath() + File.separator
                + "file" + File.separator + submitWorkReq.getFlinkSubmit().getAppResource()));
            flinkConfig.set(ApplicationConfiguration.APPLICATION_ARGS,
                Arrays.asList(submitWorkReq.getPluginReq().getArgs()));
        } else {
            flinkConfig.set(PipelineOptions.JARS, singletonList(submitWorkReq.getAgentHomePath() + File.separator
                + "plugins" + File.separator + submitWorkReq.getFlinkSubmit().getAppResource()));
            flinkConfig.set(ApplicationConfiguration.APPLICATION_ARGS, singletonList(
                Base64.getEncoder().encodeToString(JSON.toJSONString(submitWorkReq.getPluginReq()).getBytes())));
        }

        flinkConfig.set(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName());
        flinkConfig.set(DeploymentOptionsInternal.CONF_DIR, submitWorkReq.getFlinkHome() + "/conf");
        flinkConfig.set(JobManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse("1g"));
        flinkConfig.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse("1g"));
        flinkConfig.set(TaskManagerOptions.NUM_TASK_SLOTS, 1);
        flinkConfig.set(YarnConfigOptions.APPLICATION_NAME, appName);

        submitWorkReq.getFlinkSubmit().getConf().forEach((k, v) -> {
            if (v instanceof String) {
                flinkConfig.setString(k, String.valueOf(v));
            } else if (v instanceof Boolean) {
                flinkConfig.setBoolean(k, Boolean.parseBoolean(String.valueOf(v)));
            } else if (v instanceof Double) {
                flinkConfig.setDouble(k, Double.parseDouble(String.valueOf(v)));
            } else if (v instanceof Integer) {
                flinkConfig.setInteger(k, Integer.parseInt(String.valueOf(v)));
            } else if (v instanceof Long) {
                flinkConfig.setLong(k, Long.parseLong(String.valueOf(v)));
            } else {
                throw new IllegalArgumentException("Unsupported type for key: " + k + ", value: " + v);
            }
        });

        // 加载yarn配置文件
        String hadoopConfDir = System.getenv("HADOOP_CONF_DIR");
        if (hadoopConfDir == null) {
            throw new Exception("Hadoop conf dir is null");
        }
        Path path = new Path(hadoopConfDir + "/yarn-site.xml");
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.addResource(path);
        Map<String, String> yarn = conf.getPropsWithPrefix("yarn");
        yarn.forEach((k, v) -> flinkConfig.setString("flink.yarn" + k, v));

        // 添加flink dist
        flinkConfig.set(YarnConfigOptions.FLINK_DIST_JAR, submitWorkReq.getFlinkHome() + "/lib/flink-dist-1.18.1.jar");

        // 初始化要加载到lib包
        List<String> libFile = new ArrayList<>();

        // 加载flink的lib
        libFile.add(submitWorkReq.getFlinkHome() + "/lib");

        // 加载至流云的依赖
        File[] jarFiles = new File(submitWorkReq.getAgentHomePath() + File.separator + "lib").listFiles();
        if (jarFiles != null) {
            for (File jarFile : jarFiles) {
                if (!"hive-jdbc-3.1.3-standalone.jar".equals(jarFile.getName())
                    && !"hive-jdbc-uber-2.6.3.0-235.jar".equals(jarFile.getName())) {
                    libFile.add(jarFile.getPath());
                }
            }
        }

        // 添加自定义依赖
        if (submitWorkReq.getLibConfig() != null) {
            for (int i = 0; i < submitWorkReq.getLibConfig().size(); i++) {
                libFile.add(submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + submitWorkReq.getLibConfig().get(i) + ".jar");
            }
        }

        // 添加自定义函数
        if (submitWorkReq.getFuncConfig() != null) {
            for (int i = 0; i < submitWorkReq.getFuncConfig().size(); i++) {
                libFile.add(submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + submitWorkReq.getFuncConfig().get(i).getFileId() + ".jar");
            }
        }

        flinkConfig.set(YarnConfigOptions.SHIP_FILES, libFile);

        // 提交作业到yarn中
        ClusterSpecification clusterSpecification =
            new ClusterSpecification.ClusterSpecificationBuilder().setMasterMemoryMB(1024).setTaskManagerMemoryMB(1024)
                .setSlotsPerTaskManager(1).createClusterSpecification();

        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.fromConfiguration(flinkConfig);
        YarnClusterClientFactory yarnClusterClientFactory = new YarnClusterClientFactory();
        try (YarnClusterDescriptor clusterDescriptor = yarnClusterClientFactory.createClusterDescriptor(flinkConfig)) {
            ClusterClientProvider<ApplicationId> applicationIdClusterClientProvider =
                clusterDescriptor.deployApplicationCluster(clusterSpecification, applicationConfiguration);
            return SubmitWorkRes.builder()
                .appId(String.valueOf(applicationIdClusterClientProvider.getClusterClient().getClusterId())).build();
        } catch (ClusterDeploymentException clusterDeploymentException) {
            log.error(clusterDeploymentException.getMessage(), clusterDeploymentException);
            Pattern pattern = Pattern.compile("application_\\d+_\\d+");
            Matcher matcher = pattern.matcher(clusterDeploymentException.getCause().getMessage());
            if (matcher.find()) {
                return SubmitWorkRes.builder().appId(matcher.group()).build();
            }
            throw new Exception(clusterDeploymentException.getCause().getMessage());
        }
    }

    @Override
    public GetWorkInfoRes getWorkInfo(GetWorkInfoReq getWorkInfoReq) throws Exception {

        Configuration flinkConfig = GlobalConfiguration.loadConfiguration();
        flinkConfig.set(DeploymentOptionsInternal.CONF_DIR, getWorkInfoReq.getFlinkHome() + "/conf");
        Path path = new Path(System.getenv("HADOOP_CONF_DIR") + "/yarn-site.xml");
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.addResource(path);
        Map<String, String> yarn = conf.getPropsWithPrefix("yarn");
        yarn.forEach((k, v) -> {
            flinkConfig.setString("flink.yarn" + k, v);
        });

        YarnClusterClientFactory yarnClusterClientFactory = new YarnClusterClientFactory();
        try (YarnClusterDescriptor clusterDescriptor = yarnClusterClientFactory.createClusterDescriptor(flinkConfig)) {
            ApplicationReport applicationReport = clusterDescriptor.getYarnClient()
                .getApplicationReport(ApplicationId.fromString(getWorkInfoReq.getAppId()));
            return GetWorkInfoRes.builder().appId(getWorkInfoReq.getAppId())
                .status(applicationReport.getYarnApplicationState().name())
                .finalStatus(applicationReport.getFinalApplicationStatus().name()).build();
        }
    }

    @Override
    public GetWorkLogRes getWorkLog(GetWorkLogReq getWorkLogReq) throws Exception {

        String getLogCmdFormat = "yarn logs -applicationId %s";
        Process process = Runtime.getRuntime().exec(String.format(getLogCmdFormat, getWorkLogReq.getAppId()));

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder errLog = new StringBuilder();
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) == null)
                    break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            errLog.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode == 1) {
            throw new IsxAppException(errLog.toString());
        } else {
            Pattern regex =
                Pattern.compile("LogType:taskmanager.log\\s*([\\s\\S]*?)\\s*End of LogType:taskmanager.log");
            Matcher matcher = regex.matcher(errLog);
            String log = "";
            while (matcher.find()) {
                String tmpLog = matcher.group();
                if (tmpLog.contains("ERROR")) {
                    log = tmpLog;
                    break;
                }
                if (tmpLog.length() > log.length()) {
                    log = tmpLog;
                }
            }
            if (Strings.isEmpty(log)) {
                regex = Pattern.compile("LogType:jobmanager.log\\s*([\\s\\S]*?)\\s*End of LogType:jobmanager.log");
                matcher = regex.matcher(errLog);
                while (matcher.find()) {
                    String tmpLog = matcher.group();
                    if (tmpLog.contains("ERROR")) {
                        log = tmpLog;
                        break;
                    }
                    if (tmpLog.length() > log.length()) {
                        log = tmpLog;
                    }
                }
            }
            return GetWorkLogRes.builder().log(log).build();
        }
    }

    @Override
    public StopWorkRes stopWork(StopWorkReq stopWorkReq) throws Exception {

        Configuration flinkConfig = GlobalConfiguration.loadConfiguration();
        flinkConfig.set(DeploymentOptionsInternal.CONF_DIR, stopWorkReq.getFlinkHome() + "/conf");
        Path path = new Path(System.getenv("HADOOP_CONF_DIR") + "/yarn-site.xml");
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.addResource(path);
        Map<String, String> yarn = conf.getPropsWithPrefix("yarn");
        yarn.forEach((k, v) -> flinkConfig.setString("flink.yarn" + k, v));

        YarnClusterClientFactory yarnClusterClientFactory = new YarnClusterClientFactory();
        try (YarnClusterDescriptor clusterDescriptor = yarnClusterClientFactory.createClusterDescriptor(flinkConfig)) {
            clusterDescriptor.getYarnClient().killApplication(ApplicationId.fromString(stopWorkReq.getAppId()));
            return StopWorkRes.builder().build();
        }
    }
}
