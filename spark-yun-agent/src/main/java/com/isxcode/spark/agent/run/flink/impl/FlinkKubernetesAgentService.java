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
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.*;
import org.apache.flink.kubernetes.KubernetesClusterClientFactory;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.configuration.KubernetesDeploymentTarget;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class FlinkKubernetesAgentService implements FlinkAgentService {

    @Override
    public String getAgentType() {
        return AgentType.K8S;
    }

    public void generatePodTemplate(List<String> volumeMounts, List<String> volumes, String agentHomePath,
        String workInstanceId) throws IOException {

        String podTemplate = "apiVersion: v1 \n" + "kind: Pod \n" + "metadata: \n" + "  name: pod-template \n"
            + "spec:\n" + "  terminationGracePeriodSeconds: 600\n" + "  containers:\n"
            + "    - name: flink-main-container\n" + "      volumeMounts:\n" + " %s" + "  volumes:\n" + " %s";

        String podTemplateContent =
            String.format(podTemplate, Strings.join(volumeMounts, ' '), Strings.join(volumes, ' '));

        // 判断pod文件夹是否存在
        if (!new File(agentHomePath + File.separator + "pod").exists()) {
            Files.createDirectories(Paths.get(agentHomePath + File.separator + "pod"));
        }

        // 判断k8s-logs文件夹是否存在
        if (!new File(agentHomePath + File.separator + "k8s-logs").exists()) {
            Files.createDirectories(Paths.get(agentHomePath + File.separator + "k8s-logs"));
        }

        // 创建日志目录
        Path k8sLog = Paths.get(agentHomePath + File.separator + "k8s-logs" + File.separator + workInstanceId);
        Files.createDirectories(k8sLog);
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OTHERS_WRITE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);
        Files.setPosixFilePermissions(k8sLog, perms);

        // 创建pod文件
        try (InputStream inputStream = new ByteArrayInputStream(podTemplateContent.getBytes())) {
            Files.copy(inputStream, Paths.get(agentHomePath + File.separator + "pod").resolve(workInstanceId + ".yaml"),
                StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public SubmitWorkRes submitWork(SubmitWorkReq submitWorkReq) throws Exception {

        Configuration flinkConfig = GlobalConfiguration.loadConfiguration();

        // flink的args配置
        if (WorkType.FLINK_JAR.equals(submitWorkReq.getWorkType())) {
            flinkConfig.set(ApplicationConfiguration.APPLICATION_ARGS,
                Arrays.asList(submitWorkReq.getPluginReq().getArgs()));
        } else {
            flinkConfig.set(ApplicationConfiguration.APPLICATION_ARGS, Collections.singletonList(
                Base64.getEncoder().encodeToString(JSON.toJSONString(submitWorkReq.getPluginReq()).getBytes())));
        }

        // 配置名称
        flinkConfig.set(PipelineOptions.NAME, submitWorkReq.getFlinkSubmit().getAppName() + "-"
            + submitWorkReq.getWorkType() + "-" + submitWorkReq.getWorkId() + "-" + submitWorkReq.getWorkInstanceId());

        // flink on k8s配置
        flinkConfig.set(ApplicationConfiguration.APPLICATION_MAIN_CLASS,
            submitWorkReq.getFlinkSubmit().getEntryClass());
        flinkConfig.set(DeploymentOptions.TARGET, KubernetesDeploymentTarget.APPLICATION.getName());
        flinkConfig.set(DeploymentOptionsInternal.CONF_DIR, submitWorkReq.getFlinkHome() + "/conf");
        flinkConfig.set(PipelineOptions.JARS, Collections.singletonList("local:///opt/flink/examples/app.jar"));
        flinkConfig.set(KubernetesConfigOptions.CLUSTER_ID, "zhiqingyun-cluster-" + System.currentTimeMillis());
        flinkConfig.set(KubernetesConfigOptions.REST_SERVICE_EXPOSED_TYPE,
            KubernetesConfigOptions.ServiceExposedType.NodePort);
        flinkConfig.set(KubernetesConfigOptions.CONTAINER_IMAGE_PULL_POLICY,
            KubernetesConfigOptions.ImagePullPolicy.IfNotPresent);
        flinkConfig.set(KubernetesConfigOptions.NAMESPACE, "zhiqingyun-space");
        flinkConfig.set(KubernetesConfigOptions.KUBERNETES_SERVICE_ACCOUNT, "zhiqingyun");
        flinkConfig.set(KubernetesConfigOptions.CONTAINER_IMAGE, "flink:1.18.1-scala_2.12");
        flinkConfig.set(KubernetesConfigOptions.TASK_MANAGER_CPU, 2.0);
        flinkConfig.set(KubernetesConfigOptions.KUBERNETES_POD_TEMPLATE, submitWorkReq.getAgentHomePath()
            + File.separator + "pod" + File.separator + submitWorkReq.getWorkInstanceId() + ".yaml");
        flinkConfig.set(KubernetesConfigOptions.KUBERNETES_HOSTNETWORK_ENABLED, true);
        flinkConfig.set(KubernetesConfigOptions.FLINK_LOG_DIR, "/tmp/log");
        flinkConfig.set(JobManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse("1g"));
        flinkConfig.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse("1g"));
        flinkConfig.set(TaskManagerOptions.NUM_TASK_SLOTS, 1);
        flinkConfig.set(RestartStrategyOptions.RESTART_STRATEGY, "disable");

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

        // 映射文件路径
        List<String> volumeMounts = new ArrayList<>();
        String volumeTemplate = "   - name: %s\n      hostPath:\n        path: %s\n";
        List<String> volumes = new ArrayList<>();
        String volumeMountsTemplate = "       - name: %s\n" + "          mountPath: %s\n";

        // app文件映射
        volumeMounts.add(String.format(volumeMountsTemplate, "app", "/opt/flink/examples/app.jar"));
        if (WorkType.FLINK_JAR.equals(submitWorkReq.getWorkType())) {
            volumes.add(String.format(volumeTemplate, "app", submitWorkReq.getAgentHomePath() + File.separator + "file"
                + File.separator + submitWorkReq.getFlinkSubmit().getAppResource()));
        } else {
            volumes.add(String.format(volumeTemplate, "app", submitWorkReq.getAgentHomePath() + File.separator
                + "plugins" + File.separator + submitWorkReq.getFlinkSubmit().getAppResource()));
        }

        // 日志文件映射
        volumeMounts.add(String.format(volumeMountsTemplate, "flink-log", "/tmp/log"));
        volumes.add(String.format(volumeTemplate, "flink-log", submitWorkReq.getAgentHomePath() + File.separator
            + "k8s-logs" + File.separator + submitWorkReq.getWorkInstanceId()));

        // 至流云lib映射
        File[] jarFiles = new File(submitWorkReq.getAgentHomePath() + File.separator + "lib").listFiles();
        if (jarFiles != null) {
            for (int i = 0; i < jarFiles.length; i++) {
                volumeMounts.add(String.format(volumeMountsTemplate, "zhiqingyun-lib-" + i,
                    "/opt/flink/lib/" + jarFiles[i].getName()));
                volumes.add(String.format(volumeTemplate, "zhiqingyun-lib-" + i, submitWorkReq.getAgentHomePath()
                    + File.separator + "lib" + File.separator + jarFiles[i].getName()));
            }
        }

        // 自定义依赖
        if (submitWorkReq.getLibConfig() != null) {
            for (int i = 0; i < submitWorkReq.getLibConfig().size(); i++) {
                volumeMounts.add(String.format(volumeMountsTemplate, "lib-" + i,
                    "/opt/flink/lib/" + submitWorkReq.getLibConfig().get(i) + ".jar"));
                volumes.add(String.format(volumeTemplate, "lib-" + i, submitWorkReq.getAgentHomePath() + File.separator
                    + "file" + File.separator + submitWorkReq.getLibConfig().get(i) + ".jar"));
            }
        }

        // 自定义函数
        if (submitWorkReq.getFuncConfig() != null) {
            for (int i = 0; i < submitWorkReq.getFuncConfig().size(); i++) {
                volumeMounts.add(String.format(volumeMountsTemplate, "func-" + i,
                    "/opt/flink/lib/" + submitWorkReq.getFuncConfig().get(i) + ".jar"));
                volumes.add(String.format(volumeTemplate, "func-" + i, submitWorkReq.getAgentHomePath() + File.separator
                    + "file" + File.separator + submitWorkReq.getFuncConfig().get(i).getFileId() + ".jar"));
            }
        }

        // 生成pod文件
        generatePodTemplate(volumeMounts, volumes, submitWorkReq.getAgentHomePath(), submitWorkReq.getWorkInstanceId());

        // 提交flink作业
        ClusterSpecification clusterSpecification =
            new ClusterSpecification.ClusterSpecificationBuilder().setMasterMemoryMB(1024).setTaskManagerMemoryMB(1024)
                .setSlotsPerTaskManager(2).createClusterSpecification();

        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.fromConfiguration(flinkConfig);
        applicationConfiguration.applyToConfiguration(flinkConfig);
        KubernetesClusterClientFactory kubernetesClusterClientFactory = new KubernetesClusterClientFactory();
        try (KubernetesClusterDescriptor clusterDescriptor =
            kubernetesClusterClientFactory.createClusterDescriptor(flinkConfig)) {
            ClusterClientProvider<String> clusterClientProvider =
                clusterDescriptor.deployApplicationCluster(clusterSpecification, applicationConfiguration);
            return SubmitWorkRes.builder().webUrl(clusterClientProvider.getClusterClient().getWebInterfaceURL())
                .appId(String.valueOf(clusterClientProvider.getClusterClient().getClusterId())).build();
        }
    }

    @Override
    public GetWorkInfoRes getWorkInfo(GetWorkInfoReq getWorkInfoReq) throws Exception {

        String getStatusJobManagerFormat = "kubectl get pods -l app=%s -n zhiqingyun-space";
        String line;
        StringBuilder errLog = new StringBuilder();

        String command = String.format(getStatusJobManagerFormat, getWorkInfoReq.getAppId());
        Process process = Runtime.getRuntime().exec(command);
        try (InputStream inputStream = process.getInputStream();
            InputStream errStream = process.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(errStream, StandardCharsets.UTF_8))) {

            while ((line = reader.readLine()) != null) {
                errLog.append(line).append("\n");
                String pattern = "\\s+\\d/\\d\\s+(\\w+)";
                Pattern regex = Pattern.compile(pattern);
                Matcher matcher = regex.matcher(line);
                if (matcher.find()) {
                    return GetWorkInfoRes.builder().finalState(matcher.group(1)).appId(getWorkInfoReq.getAppId())
                        .build();
                }
            }

            if (errLog.toString().isEmpty()) {
                while ((line = errReader.readLine()) != null) {
                    errLog.append(line).append("\n");
                }
                if (errLog.toString().contains("No resources found in zhiqingyun-space namespace")) {
                    return GetWorkInfoRes.builder().finalState("Over").appId(getWorkInfoReq.getAppId()).build();
                }
            }
            int exitCode = process.waitFor();
            if (exitCode == 1) {
                throw new Exception("Command execution failed:\n" + errLog);
            }
        }

        throw new Exception("获取状态异常");
    }

    @Override
    public GetWorkLogRes getWorkLog(GetWorkLogReq getWorkLogReq) throws Exception {

        File[] logFiles = new File(getWorkLogReq.getAgentHomePath() + File.separator + "k8s-logs" + File.separator
            + getWorkLogReq.getWorkInstanceId()).listFiles();

        StringBuilder logBuilder = new StringBuilder();
        if (logFiles != null) {
            if (!"ERROR".equalsIgnoreCase(getWorkLogReq.getWorkStatus())) {
                if (Arrays.stream(logFiles).allMatch(file -> file.getName().contains("application"))) {
                    return GetWorkLogRes.builder().log("").build();
                }
            }
            for (File logFile : logFiles) {
                if (logFile.getName().contains("taskmanager")) {
                    FileReader fileReader = new FileReader(logFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        logBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    fileReader.close();
                    break;
                }
                if (logFile.getName().contains("application")) {
                    FileReader fileReader = new FileReader(logFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        logBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    fileReader.close();
                    break;
                }
            }
        }

        return GetWorkLogRes.builder().log(logBuilder.toString()).build();
    }

    @Override
    public StopWorkRes stopWork(StopWorkReq stopWorkReq) throws Exception {

        Configuration flinkConfig = GlobalConfiguration.loadConfiguration();
        flinkConfig.set(DeploymentOptions.TARGET, KubernetesDeploymentTarget.APPLICATION.getName());
        flinkConfig.set(KubernetesConfigOptions.NAMESPACE, "zhiqingyun-space");
        flinkConfig.set(KubernetesConfigOptions.KUBERNETES_SERVICE_ACCOUNT, "zhiqingyun");

        KubernetesClusterClientFactory kubernetesClusterClientFactory = new KubernetesClusterClientFactory();
        try (KubernetesClusterDescriptor clusterDescriptor =
            kubernetesClusterClientFactory.createClusterDescriptor(flinkConfig)) {
            clusterDescriptor.killCluster(stopWorkReq.getAppId());
            return StopWorkRes.builder().build();
        }
    }
}
