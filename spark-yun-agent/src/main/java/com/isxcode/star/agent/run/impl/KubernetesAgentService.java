package com.isxcode.star.agent.run.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.isxcode.star.agent.run.AgentService;
import com.isxcode.star.api.agent.constants.AgentKubernetes;
import com.isxcode.star.api.agent.constants.AgentType;
import com.isxcode.star.api.agent.pojos.req.SubmitWorkReq;
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
    public String getAgentType() {
        return AgentType.K8S;
    }

    @Override
    public String getMaster(String sparkHomePath) throws Exception {

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
    public SparkLauncher getSparkLauncher(SubmitWorkReq submitWorkReq) throws Exception {

        // 初始化sparkLauncher
        SparkLauncher sparkLauncher = new SparkLauncher().setVerbose(false).setDeployMode("cluster")
            .setMainClass(submitWorkReq.getSparkSubmit().getMainClass())
            .setMaster(getMaster(submitWorkReq.getSparkHomePath()))
            .setAppResource("local:///opt/spark/examples/jars/" + submitWorkReq.getSparkSubmit().getAppResource())
            .setSparkHome(submitWorkReq.getAgentHomePath() + File.separator + "spark-min");
        sparkLauncher.setConf("spark.kubernetes.container.image", AgentKubernetes.SPARK_DOCKER_IMAGE);
        sparkLauncher.setConf("spark.kubernetes.namespace", AgentKubernetes.NAMESPACE);
        sparkLauncher.setConf("spark.kubernetes.authenticate.driver.serviceAccountName",
            AgentKubernetes.SERVICE_ACCOUNT_NAME);
        sparkLauncher.setConf("spark.kubernetes.authenticate.executor.serviceAccountName",
            AgentKubernetes.SERVICE_ACCOUNT_NAME);
        sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.jar.mount.path",
            "/opt/spark/examples/jars/" + submitWorkReq.getSparkSubmit().getAppResource());
        sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.jar.mount.path",
            "/opt/spark/examples/jars/" + submitWorkReq.getSparkSubmit().getAppResource());
        sparkLauncher.setConf("spark.kubernetes.container.image.pullPolicy", AgentKubernetes.PULL_POLICY);
        sparkLauncher.setConf("spark.kubernetes.appKillPodDeletionGracePeriod", "600s");

        // 判断是否为自定义任务
        if (WorkType.SPARK_JAR.equals(submitWorkReq.getWorkType())) {
            sparkLauncher.setAppName(submitWorkReq.getSparkSubmit().getAppName() + "-" + submitWorkReq.getWorkType()
                + "-" + submitWorkReq.getWorkId() + "-" + submitWorkReq.getWorkInstanceId());
            sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.jar.options.path",
                submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + submitWorkReq.getSparkSubmit().getAppResource());
            sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.jar.options.path",
                submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + submitWorkReq.getSparkSubmit().getAppResource());
        } else {
            sparkLauncher.setAppName("zhiqingyun-" + submitWorkReq.getWorkType() + "-" + submitWorkReq.getWorkId() + "-"
                + submitWorkReq.getWorkInstanceId());
            sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.jar.options.path",
                submitWorkReq.getAgentHomePath() + File.separator + "plugins" + File.separator
                    + submitWorkReq.getSparkSubmit().getAppResource());
            sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.jar.options.path",
                submitWorkReq.getAgentHomePath() + File.separator + "plugins" + File.separator
                    + submitWorkReq.getSparkSubmit().getAppResource());
        }

        // 引入至轻云的jar
        if (!Strings.isEmpty(submitWorkReq.getAgentHomePath())) {
            File[] jarFiles = new File(submitWorkReq.getAgentHomePath() + File.separator + "lib").listFiles();
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

        // 引入实时计算的jar
        if (WorkType.REAL_WORK.equals(submitWorkReq.getWorkType())) {
            File[] kafkaFiles = new File(submitWorkReq.getSparkHomePath() + File.separator + "jars").listFiles();
            if (kafkaFiles != null) {
                List<String> kafkaFileList =
                    Arrays.asList("spark-sql-kafka-0-10_2.12-3.4.1.jar", "spark-streaming-kafka-0-10_2.12-3.4.1.jar",
                        "spark-token-provider-kafka-0-10_2.12-3.4.1.jar", "commons-pool2-2.11.1.jar",
                        "kafka-clients-3.1.2.jar", "commons-dbutils-1.7.jar", "HikariCP-4.0.3.jar");
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

        // 引入用户上传的jar
        if (submitWorkReq.getLibConfig() != null) {
            for (int i = 0; i < submitWorkReq.getLibConfig().size(); i++) {
                sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.lib" + i + ".mount.path",
                    "/opt/spark/jars/" + submitWorkReq.getLibConfig().get(i) + ".jar");
                sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.lib" + i + ".options.path",
                    submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                        + submitWorkReq.getLibConfig().get(i) + ".jar");
                sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.lib" + i + ".mount.path",
                    "/opt/spark/jars/" + submitWorkReq.getLibConfig().get(i) + ".jar");
                sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.lib" + i + ".options.path",
                    submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                        + submitWorkReq.getLibConfig().get(i) + ".jar");
            }
        }

        // 引入excel/csv文件
        String csvFilePath = submitWorkReq.getPluginReq().getCsvFilePath();
        if (submitWorkReq.getPluginReq().getCsvFilePath() != null) {
            sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.excel.mount.path", csvFilePath);
            sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.excel.options.path", csvFilePath);
            sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.excel.mount.path", csvFilePath);
            sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.excel.options.path", csvFilePath);
            sparkLauncher.addFile("local://" + csvFilePath);
        }

        // 引入自定义函数
        if (submitWorkReq.getFuncConfig() != null) {
            for (int i = 0; i < submitWorkReq.getFuncConfig().size(); i++) {
                sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.func" + i + ".mount.path",
                    "/opt/spark/jars/" + submitWorkReq.getFuncConfig().get(i).getFileId() + ".jar");
                sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.func" + i + ".options.path",
                    submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                        + submitWorkReq.getFuncConfig().get(i).getFileId() + ".jar");
                sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.func" + i + ".mount.path",
                    "/opt/spark/jars/" + submitWorkReq.getFuncConfig().get(i).getFileId() + ".jar");
                sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.func" + i + ".options.path",
                    submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                        + submitWorkReq.getFuncConfig().get(i).getFileId() + ".jar");
            }
        }

        // 获取sparkConfig配置
        Map<String, String> pluginSparkConfig = submitWorkReq.getPluginReq().getSparkConfig();

        // 从sparkConfig中解析出域名映射
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

        // 拼接podTemplate文件
        AtomicReference<String> podTemplate = new AtomicReference<>(
            "apiVersion: v1 \n" + "kind: Pod \n" + "metadata: \n" + "  name: pod-template \n" + "spec:\n"
                + "  hostNetwork: true\n" + "  ttlSecondsAfterFinished: 600\n" + "  terminationGracePeriodSeconds: 600\n"
                + "  activeDeadlineSeconds: 600\n" + "  dnsPolicy: Default\n");

        if (!hostMapping.isEmpty()) {
            podTemplate.set(podTemplate + "  hostAliases:\n");
            hostMapping.forEach((k, v) -> podTemplate
                .set(podTemplate + "    - ip: \"" + v + "\"\n      hostnames:\n" + "        - \"" + k + "\"\n"));
        }

        // 将文本写到pod-init.yaml中
        String podFileName = submitWorkReq.getWorkInstanceId() + ".yml";
        String podPath = submitWorkReq.getAgentHomePath() + File.separator + "pods" + File.separator + podFileName;
        FileUtil.writeUtf8String(podTemplate.get(), podPath);

        // 配置pod-init.yaml
        sparkLauncher.setConf("spark.kubernetes.driver.podTemplateFile", podPath);
        sparkLauncher.setConf("spark.kubernetes.executor.podTemplateFile", podPath);

        // 获取hive操作人
        String hiveUsername = "";
        if (Strings.isNotEmpty(pluginSparkConfig.get("qing.hive.username"))) {
            hiveUsername = pluginSparkConfig.get("qing.hive.username");
        }

        // sparkLauncher配置操作人
        if (Strings.isNotEmpty(hiveUsername)) {
            sparkLauncher.setConf("spark.kubernetes.driverEnv.SPARK_USER", hiveUsername);
            sparkLauncher.setConf("spark.kubernetes.driverEnv.HADOOP_USER_NAME", hiveUsername);
            sparkLauncher.setConf("spark.executorEnv.SPARK_USER", hiveUsername);
            sparkLauncher.setConf("spark.executorEnv.HADOOP_USER_NAME", hiveUsername);
        }

        // 删除至轻云的自定义参数
        pluginSparkConfig.remove("qing.host1.name");
        pluginSparkConfig.remove("qing.host1.value");
        pluginSparkConfig.remove("qing.host2.name");
        pluginSparkConfig.remove("qing.host2.value");
        pluginSparkConfig.remove("qing.host3.name");
        pluginSparkConfig.remove("qing.host3.value");
        pluginSparkConfig.remove("qing.hive.username");
        submitWorkReq.getPluginReq().setSparkConfig(pluginSparkConfig);

        // 把删除后的sparkConfig，再使用base64压缩一下
        if (WorkType.SPARK_JAR.equals(submitWorkReq.getWorkType())) {
            sparkLauncher.addAppArgs(submitWorkReq.getArgs());
        } else {
            sparkLauncher.addAppArgs(Base64.getEncoder()
                .encodeToString(submitWorkReq.getPluginReq() == null ? submitWorkReq.getArgsStr().getBytes()
                    : JSON.toJSONString(submitWorkReq.getPluginReq()).getBytes()));
        }

        // 把提交的spark配置，塞到sparkLauncher中
        submitWorkReq.getSparkSubmit().getConf().forEach(sparkLauncher::setConf);

        return sparkLauncher;
    }

    @Override
    public String submitWork(SparkLauncher sparkLauncher) throws Exception {

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
    public String getWorkStatus(String podName, String sparkHomePath) throws Exception {

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
    public String getStderrLog(String appId, String sparkHomePath) throws Exception {

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
    public String getStdoutLog(String appId, String sparkHomePath) throws Exception {

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
    public String getWorkDataStr(String appId, String sparkHomePath) throws Exception {

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
    public void stopWork(String appId, String sparkHomePath, String agentHomePath) throws Exception {

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
}
