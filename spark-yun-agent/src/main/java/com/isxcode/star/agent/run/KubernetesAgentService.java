package com.isxcode.star.agent.run;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.isxcode.star.api.agent.constants.AgentType;
import com.isxcode.star.api.agent.pojos.req.DeployContainerReq;
import com.isxcode.star.api.agent.pojos.req.YagExecuteWorkReq;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class KubernetesAgentService implements AgentService {

    @Override
    public String getMaster(String sparkHomePath) throws IOException {

        String getMasterCmd = "kubectl cluster-info";

        Process process = Runtime.getRuntime().exec(getMasterCmd);
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder errLog = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            errLog.append(line).append("\n");
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode == 1) {
                throw new IsxAppException(errLog.toString());
            } else {
                int startIndex = errLog.indexOf("https://") + "https://".length();
                int endIndex = errLog.indexOf("\n", startIndex);
                // k8s命令会返回特殊字符
                return ("k8s://" + errLog.substring(startIndex, endIndex)).replaceAll("0m", "").replaceAll("\u001B", "")
                    .replaceAll("\\[", "");
            }
        } catch (InterruptedException e) {

            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public SparkLauncher genSparkLauncher(YagExecuteWorkReq yagExecuteWorkReq) throws IOException {

        String appName = "zhiqingyun-" + yagExecuteWorkReq.getWorkType() + "-" + yagExecuteWorkReq.getWorkId() + "-"
            + yagExecuteWorkReq.getWorkInstanceId();

        SparkLauncher sparkLauncher;
        if (WorkType.SPARK_JAR.equals(yagExecuteWorkReq.getWorkType())) {
            // 如果是自定义作业
            sparkLauncher = new SparkLauncher().setVerbose(false)
                .setMainClass(yagExecuteWorkReq.getSparkSubmit().getMainClass()).setDeployMode("cluster")
                .setAppName(yagExecuteWorkReq.getSparkSubmit().getAppName() + "-" + yagExecuteWorkReq.getWorkType()
                    + "-" + yagExecuteWorkReq.getWorkId() + "-" + yagExecuteWorkReq.getWorkInstanceId())
                .setMaster(getMaster(yagExecuteWorkReq.getSparkHomePath()))
                .setAppResource(
                    "local:///opt/spark/examples/jars/" + yagExecuteWorkReq.getSparkSubmit().getAppResource())
                .setSparkHome(yagExecuteWorkReq.getAgentHomePath() + File.separator + "spark-min");
        } else {
            sparkLauncher = new SparkLauncher().setVerbose(false)
                .setMainClass(yagExecuteWorkReq.getSparkSubmit().getMainClass()).setDeployMode("cluster")
                .setAppName(appName).setMaster(getMaster(yagExecuteWorkReq.getSparkHomePath()))
                .setAppResource(
                    "local:///opt/spark/examples/jars/" + yagExecuteWorkReq.getSparkSubmit().getAppResource())
                .setSparkHome(yagExecuteWorkReq.getAgentHomePath() + File.separator + "spark-min");
        }

        if (!Strings.isEmpty(yagExecuteWorkReq.getAgentHomePath())) {
            File[] jarFiles = new File(yagExecuteWorkReq.getAgentHomePath() + File.separator + "lib").listFiles();
            if (jarFiles != null) {
                for (int i = 0; i < jarFiles.length; i++) {
                    if (!jarFiles[i].getName().contains("hive")
                        && !jarFiles[i].getName().contains("zhiqingyun-agent.jar")) {
                        sparkLauncher.addJar("local:///opt/spark/examples/jars/lib/" + jarFiles[i].getName());
                        sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath." + i + ".mount.path",
                            "/opt/spark/examples/jars/lib/" + jarFiles[i].getName());
                        sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath." + i + ".options.path",
                            jarFiles[i].getPath());
                        sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath." + i + ".mount.path",
                            "/opt/spark/examples/jars/lib/" + jarFiles[i].getName());
                        sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath." + i + ".options.path",
                            jarFiles[i].getPath());
                    }
                }
            }
        }

        // 引入实时同步的包
        if ("REAL_WORK".equals(yagExecuteWorkReq.getWorkType())) {
            File[] kafkaFiles = new File(yagExecuteWorkReq.getSparkHomePath() + File.separator + "jars").listFiles();
            if (kafkaFiles != null) {
                List<String> kafkaFileList = Arrays.asList("spark-sql-kafka-0-10_2.12-3.4.1.jar",
                    "spark-streaming-kafka-0-10_2.12-3.4.1.jar", "spark-token-provider-kafka-0-10_2.12-3.4.1.jar",
                    "commons-pool2-2.11.1.jar", "kafka-clients-3.1.2.jar");
                for (int i = 0; i < kafkaFiles.length; i++) {
                    if (kafkaFileList.contains(kafkaFiles[i].getName())) {
                        sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.kafka" + i + ".mount.path",
                            "/opt/spark/jars/" + kafkaFiles[i].getName());
                        sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.kafka" + i + ".options.path",
                            kafkaFiles[i].getPath());
                        sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.kafka" + i + ".mount.path",
                            "/opt/spark/jars/" + kafkaFiles[i].getName());
                        sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.kafka" + i + ".options.path",
                            kafkaFiles[i].getPath());
                    }
                }
            }
        }

        // 添加额外依赖
        if (yagExecuteWorkReq.getLibConfig() != null) {
            for (int i = 0; i < yagExecuteWorkReq.getLibConfig().size(); i++) {
                sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.lib" + i + ".mount.path",
                    "/opt/spark/jars/" + yagExecuteWorkReq.getLibConfig().get(i) + ".jar");
                sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.lib" + i + ".options.path",
                    yagExecuteWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                        + yagExecuteWorkReq.getLibConfig().get(i) + ".jar");
                sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.lib" + i + ".mount.path",
                    "/opt/spark/jars/" + yagExecuteWorkReq.getLibConfig().get(i) + ".jar");
                sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.lib" + i + ".options.path",
                    yagExecuteWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                        + yagExecuteWorkReq.getLibConfig().get(i) + ".jar");
            }
        }

        // 添加自定义函数
        if (yagExecuteWorkReq.getFuncConfig() != null) {
            for (int i = 0; i < yagExecuteWorkReq.getFuncConfig().size(); i++) {
                sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.func" + i + ".mount.path",
                    "/opt/spark/jars/" + yagExecuteWorkReq.getFuncConfig().get(i).getFileId() + ".jar");
                sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.func" + i + ".options.path",
                    yagExecuteWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                        + yagExecuteWorkReq.getFuncConfig().get(i).getFileId() + ".jar");
                sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.func" + i + ".mount.path",
                    "/opt/spark/jars/" + yagExecuteWorkReq.getFuncConfig().get(i).getFileId() + ".jar");
                sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.func" + i + ".options.path",
                    yagExecuteWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                        + yagExecuteWorkReq.getFuncConfig().get(i).getFileId() + ".jar");
            }
        }

        Map<String, String> pluginSparkConfig = yagExecuteWorkReq.getPluginReq().getSparkConfig();

        // 配置域名映射
        Map<String, String> hostMapping = new HashMap<>();
        if (Strings.isNotEmpty(pluginSparkConfig.get("qing.host1.name"))
            && Strings.isNotEmpty(pluginSparkConfig.get("qing.host1.value"))) {
            hostMapping.put(pluginSparkConfig.get("qing.host1.name"), pluginSparkConfig.get("qing.host1.value"));
        }
        if (Strings.isNotEmpty(pluginSparkConfig.get("qing.host2.name"))
            && Strings.isNotEmpty(pluginSparkConfig.get("qing.host2.value"))) {
            hostMapping.put(pluginSparkConfig.get("qing.host2.name"), pluginSparkConfig.get("qing.host2.value"));
        }
        if (Strings.isNotEmpty(pluginSparkConfig.get("qing.host3.name"))
            && Strings.isNotEmpty(pluginSparkConfig.get("qing.host3.value"))) {
            hostMapping.put(pluginSparkConfig.get("qing.host3.name"), pluginSparkConfig.get("qing.host3.value"));
        }
        AtomicReference<String> podContent;
        if (!hostMapping.isEmpty()) {
            podContent = new AtomicReference<>("apiVersion: v1\n" + "kind: Pod\n" + "metadata:\n" + "  name: host-pod\n"
                + "spec:\n" + "  hostAliases:\n");
            hostMapping.forEach((k, v) -> {
                podContent
                    .set(podContent + "    - ip: \"" + v + "\"\n      hostnames:\n" + "        - \"" + k + "\"\n");
            });
        } else {
            podContent = null;
        }

        // 配置hive操作人
        String hiveUsername = "";
        if (Strings.isNotEmpty(pluginSparkConfig.get("qing.hive.username"))) {
            hiveUsername = pluginSparkConfig.get("qing.hive.username");
        }

        // 删除自定义参数
        pluginSparkConfig.remove("qing.host1.name");
        pluginSparkConfig.remove("qing.host1.value");
        pluginSparkConfig.remove("qing.host2.name");
        pluginSparkConfig.remove("qing.host2.value");
        pluginSparkConfig.remove("qing.host3.name");
        pluginSparkConfig.remove("qing.host3.value");
        pluginSparkConfig.remove("qing.hive.username");
        yagExecuteWorkReq.getPluginReq().setSparkConfig(pluginSparkConfig);

        if (WorkType.SPARK_JAR.equals(yagExecuteWorkReq.getWorkType())) {
            sparkLauncher.addAppArgs(yagExecuteWorkReq.getArgs());
        } else {
            sparkLauncher.addAppArgs(Base64.getEncoder()
                .encodeToString(yagExecuteWorkReq.getPluginReq() == null ? yagExecuteWorkReq.getArgsStr().getBytes()
                    : JSON.toJSONString(yagExecuteWorkReq.getPluginReq()).getBytes()));
        }

        yagExecuteWorkReq.getSparkSubmit().getConf().forEach(sparkLauncher::setConf);

        sparkLauncher.setConf("spark.kubernetes.container.image", "spark:3.4.1");
        sparkLauncher.setConf("spark.kubernetes.authenticate.driver.serviceAccountName", "zhiqingyun");
        sparkLauncher.setConf("spark.kubernetes.authenticate.executor.serviceAccountName", "zhiqingyun");
        sparkLauncher.setConf("spark.kubernetes.namespace", "zhiqingyun-space");
        sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.jar.mount.path",
            "/opt/spark/examples/jars/" + yagExecuteWorkReq.getSparkSubmit().getAppResource());
        sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.jar.mount.path",
            "/opt/spark/examples/jars/" + yagExecuteWorkReq.getSparkSubmit().getAppResource());

        // 映射appResource文件
        if (WorkType.SPARK_JAR.equals(yagExecuteWorkReq.getWorkType())) {
            sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.jar.options.path",
                yagExecuteWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + yagExecuteWorkReq.getSparkSubmit().getAppResource());
            sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.jar.options.path",
                yagExecuteWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + yagExecuteWorkReq.getSparkSubmit().getAppResource());
        } else {
            sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.jar.options.path",
                yagExecuteWorkReq.getAgentHomePath() + File.separator + "plugins" + File.separator
                    + yagExecuteWorkReq.getSparkSubmit().getAppResource());
            sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.jar.options.path",
                yagExecuteWorkReq.getAgentHomePath() + File.separator + "plugins" + File.separator
                    + yagExecuteWorkReq.getSparkSubmit().getAppResource());
        }

        // 配置操作人
        if (Strings.isNotEmpty(hiveUsername)) {
            sparkLauncher.setConf("spark.kubernetes.driverEnv.SPARK_USER", hiveUsername);
            sparkLauncher.setConf("spark.kubernetes.driverEnv.HADOOP_USER_NAME", hiveUsername);
            sparkLauncher.setConf("spark.executorEnv.SPARK_USER", hiveUsername);
            sparkLauncher.setConf("spark.executorEnv.HADOOP_USER_NAME", hiveUsername);
        }

        // 将文本写到pod-init.yaml中
        if (!hostMapping.isEmpty()) {
            String podFileName = yagExecuteWorkReq.getWorkInstanceId() + ".yml";
            String podPath =
                yagExecuteWorkReq.getAgentHomePath() + File.separator + "pods" + File.separator + podFileName;
            FileUtil.writeUtf8String(podContent.get(), podPath);

            // 配置pod-init.yaml
            sparkLauncher.setConf("spark.kubernetes.driver.podTemplateFile", podPath);
            sparkLauncher.setConf("spark.kubernetes.executor.podTemplateFile", podPath);
        }

        return sparkLauncher;
    }

    @Override
    public String executeWork(SparkLauncher sparkLauncher) throws IOException {

        Process launch = sparkLauncher.launch();
        InputStream inputStream = launch.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder errLog = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            errLog.append(line).append("\n");

            String pattern = "pod name: (\\S+)";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(line);
            if (matcher.find()) {
                return matcher.group().replace("pod name: ", "");
            }
        }

        try {
            int exitCode = launch.waitFor();
            if (exitCode == 1) {
                throw new IsxAppException(errLog.toString());
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        } finally {
            launch.destroy();
        }

        throw new IsxAppException("无法获取podName");
    }

    @Override
    public String getAppStatus(String podName, String sparkHomePath) throws IOException {

        String getStatusCmdFormat = "kubectl get pod %s -n zhiqingyun-space";

        Process process = Runtime.getRuntime().exec(String.format(getStatusCmdFormat, podName));
        InputStream inputStream = process.getInputStream();
        InputStream errStream = process.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        BufferedReader errReader = new BufferedReader(new InputStreamReader(errStream, StandardCharsets.UTF_8));
        StringBuilder errLog = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            errLog.append(line).append("\n");
            String pattern = "\\s+\\d/\\d\\s+(\\w+)";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        if (errLog.toString().isEmpty()) {
            while ((line = errReader.readLine()) != null) {
                errLog.append(line).append("\n");
                if (errLog.toString().contains("not found")) {
                    return "KILLED";
                }
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode == 1) {
                throw new IsxAppException(errLog.toString());
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }

        throw new IsxAppException("获取状态异常");
    }

    @Override
    public String getAppLog(String appId, String sparkHomePath) throws IOException {

        String getLogCmdFormat = "kubectl logs %s -n zhiqingyun-space";

        Process process = Runtime.getRuntime().exec(String.format(getLogCmdFormat, appId));
        InputStream inputStream = process.getInputStream();
        InputStream errStream = process.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        BufferedReader errReader = new BufferedReader(new InputStreamReader(errStream, StandardCharsets.UTF_8));

        StringBuilder errLog = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            errLog.append(line).append("\n");
        }

        if (Strings.isEmpty(errLog)) {
            while ((line = errReader.readLine()) != null) {
                errLog.append(line).append("\n");
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode == 1) {
                throw new IsxAppException(errLog.toString());
            } else {
                if (errLog.toString().contains("Error")) {
                    return errLog.toString();
                }
                Pattern regex = Pattern.compile("LogType:spark-yun\\s*([\\s\\S]*?)\\s*End of LogType:spark-yun");
                Matcher matcher = regex.matcher(errLog);
                String log = errLog.toString();
                if (matcher.find()) {
                    log = log.replace(matcher.group(), "");
                }
                return log;
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public String getStdoutLog(String appId, String sparkHomePath) throws IOException {

        String getLogCmdFormat = "kubectl logs %s -n zhiqingyun-space";

        Process process = Runtime.getRuntime().exec(String.format(getLogCmdFormat, appId));
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder errLog = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("累计处理条数")) {
                errLog.append(line).append("\n");
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode == 1) {
                throw new IsxAppException(errLog.toString());
            } else {
                return errLog.toString();
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public String getAppData(String appId, String sparkHomePath) throws IOException {

        String getLogCmdFormat = "kubectl logs -f %s -n zhiqingyun-space";

        Process process = Runtime.getRuntime().exec(String.format(getLogCmdFormat, appId));
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder errLog = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            errLog.append(line).append("\n");
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode == 1) {
                throw new IsxAppException(errLog.toString());
            } else {
                Pattern regex = Pattern.compile("LogType:spark-yun\\s*([\\s\\S]*?)\\s*End of LogType:spark-yun");
                Matcher matcher = regex.matcher(errLog);
                String log = "";
                while (matcher.find() && Strings.isEmpty(log)) {
                    log = matcher.group().replace("LogType:spark-yun\n", "").replace("\nEnd of LogType:spark-yun", "");
                }
                return log;
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public void killApp(String appId, String sparkHomePath, String agentHomePath) throws IOException {

        String killAppCmdFormat = "kubectl delete pod %s -n zhiqingyun-space";
        Process process = Runtime.getRuntime().exec(String.format(killAppCmdFormat, appId));

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder errLog = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            errLog.append(line).append("\n");
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode == 1) {
                throw new IsxAppException(errLog.toString());
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public String getAgentName() {
        return AgentType.K8S;
    }

    @Override
    public SparkLauncher genSparkLauncher(DeployContainerReq deployContainerReq) throws IOException {

        String appName = "zhiqingyun-SPARK_CONTAINER-" + deployContainerReq.getContainerId();

        SparkLauncher sparkLauncher = new SparkLauncher().setVerbose(false)
            .setMainClass(deployContainerReq.getSparkSubmit().getMainClass()).setDeployMode("cluster")
            .setAppName(appName).setMaster(getMaster(deployContainerReq.getSparkHomePath()))
            .setAppResource("local:///opt/spark/examples/jars/" + deployContainerReq.getSparkSubmit().getAppResource())
            .setSparkHome(deployContainerReq.getAgentHomePath() + File.separator + "spark-min");

        if (!Strings.isEmpty(deployContainerReq.getAgentHomePath())) {
            File[] jarFiles = new File(deployContainerReq.getAgentHomePath() + File.separator + "lib").listFiles();
            if (jarFiles != null) {
                for (int i = 0; i < jarFiles.length; i++) {
                    if (!jarFiles[i].getName().contains("hive")
                        && !jarFiles[i].getName().contains("zhiqingyun-agent.jar")) {
                        sparkLauncher.addJar("local:///opt/spark/examples/jars/lib/" + jarFiles[i].getName());
                        sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath." + i + ".mount.path",
                            "/opt/spark/examples/jars/lib/" + jarFiles[i].getName());
                        sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath." + i + ".options.path",
                            jarFiles[i].getPath());
                        sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath." + i + ".mount.path",
                            "/opt/spark/examples/jars/lib/" + jarFiles[i].getName());
                        sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath." + i + ".options.path",
                            jarFiles[i].getPath());
                    }
                }
            }
        }

        Map<String, String> pluginSparkConfig = deployContainerReq.getPluginReq().getSparkConfig();

        // 配置域名映射
        Map<String, String> hostMapping = new HashMap<>();
        if (Strings.isNotEmpty(pluginSparkConfig.get("qing.host1.name"))
            && Strings.isNotEmpty(pluginSparkConfig.get("qing.host1.value"))) {
            hostMapping.put(pluginSparkConfig.get("qing.host1.name"), pluginSparkConfig.get("qing.host1.value"));
        }
        if (Strings.isNotEmpty(pluginSparkConfig.get("qing.host2.name"))
            && Strings.isNotEmpty(pluginSparkConfig.get("qing.host2.value"))) {
            hostMapping.put(pluginSparkConfig.get("qing.host2.name"), pluginSparkConfig.get("qing.host2.value"));
        }
        if (Strings.isNotEmpty(pluginSparkConfig.get("qing.host3.name"))
            && Strings.isNotEmpty(pluginSparkConfig.get("qing.host3.value"))) {
            hostMapping.put(pluginSparkConfig.get("qing.host3.name"), pluginSparkConfig.get("qing.host3.value"));
        }
        AtomicReference<String> podContent;
        if (!hostMapping.isEmpty()) {
            podContent = new AtomicReference<>("apiVersion: v1\n" + "kind: Pod\n" + "metadata:\n" + "  name: host-pod\n"
                + "spec:\n" + "  hostAliases:\n");
            hostMapping.forEach((k, v) -> {
                podContent
                    .set(podContent + "    - ip: \"" + v + "\"\n      hostnames:\n" + "        - \"" + k + "\"\n");
            });
        } else {
            podContent = null;
        }

        // deployContainerReq.getPluginReq().setContainerPort(port);

        // 配置hive操作人
        String hiveUsername = "";
        if (Strings.isNotEmpty(pluginSparkConfig.get("qing.hive.username"))) {
            hiveUsername = pluginSparkConfig.get("qing.hive.username");
        }

        // 删除自定义参数
        pluginSparkConfig.remove("qing.host1.name");
        pluginSparkConfig.remove("qing.host1.value");
        pluginSparkConfig.remove("qing.host2.name");
        pluginSparkConfig.remove("qing.host2.value");
        pluginSparkConfig.remove("qing.host3.name");
        pluginSparkConfig.remove("qing.host3.value");
        pluginSparkConfig.remove("qing.hive.username");
        deployContainerReq.getPluginReq().setSparkConfig(pluginSparkConfig);

        sparkLauncher.addAppArgs(Base64.getEncoder()
            .encodeToString(deployContainerReq.getPluginReq() == null ? deployContainerReq.getArgs().getBytes()
                : JSON.toJSONString(deployContainerReq.getPluginReq()).getBytes()));

        deployContainerReq.getSparkSubmit().getConf().forEach(sparkLauncher::setConf);

        sparkLauncher.setConf("spark.kubernetes.container.image", "spark:3.4.1");
        sparkLauncher.setConf("spark.kubernetes.authenticate.driver.serviceAccountName", "zhiqingyun");
        sparkLauncher.setConf("spark.kubernetes.authenticate.executor.serviceAccountName", "zhiqingyun");
        sparkLauncher.setConf("spark.kubernetes.namespace", "zhiqingyun-space");
        sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.jar.mount.path",
            "/opt/spark/examples/jars/" + deployContainerReq.getSparkSubmit().getAppResource());
        sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.jar.mount.path",
            "/opt/spark/examples/jars/" + deployContainerReq.getSparkSubmit().getAppResource());

        // 映射appResource文件
        sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.jar.options.path",
            deployContainerReq.getAgentHomePath() + File.separator + "plugins" + File.separator
                + deployContainerReq.getSparkSubmit().getAppResource());
        sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.jar.options.path",
            deployContainerReq.getAgentHomePath() + File.separator + "plugins" + File.separator
                + deployContainerReq.getSparkSubmit().getAppResource());

        // 配置操作人
        if (Strings.isNotEmpty(hiveUsername)) {
            sparkLauncher.setConf("spark.kubernetes.driverEnv.SPARK_USER", hiveUsername);
            sparkLauncher.setConf("spark.kubernetes.driverEnv.HADOOP_USER_NAME", hiveUsername);
            sparkLauncher.setConf("spark.executorEnv.SPARK_USER", hiveUsername);
            sparkLauncher.setConf("spark.executorEnv.HADOOP_USER_NAME", hiveUsername);
        }

        // 将文本写到pod-init.yaml中
        if (!hostMapping.isEmpty()) {
            String podFileName = deployContainerReq.getContainerId() + ".yml";
            String podPath =
                deployContainerReq.getAgentHomePath() + File.separator + "pods" + File.separator + podFileName;
            FileUtil.writeUtf8String(podContent.get(), podPath);

            // 配置pod-init.yaml
            sparkLauncher.setConf("spark.kubernetes.driver.podTemplateFile", podPath);
            sparkLauncher.setConf("spark.kubernetes.executor.podTemplateFile", podPath);
        }

        return sparkLauncher;
    }
}
