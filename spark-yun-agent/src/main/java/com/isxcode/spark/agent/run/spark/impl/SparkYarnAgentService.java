package com.isxcode.spark.agent.run.spark.impl;

import com.alibaba.fastjson2.JSON;
import com.isxcode.spark.agent.properties.SparkYunAgentProperties;
import com.isxcode.spark.agent.run.spark.SparkAgentService;
import com.isxcode.spark.api.agent.constants.AgentType;
import com.isxcode.spark.api.agent.req.spark.SubmitWorkReq;
import com.isxcode.spark.api.agent.res.spark.GetWorkInfoRes;
import com.isxcode.spark.api.work.constants.WorkType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class SparkYarnAgentService implements SparkAgentService {

    private final SparkYunAgentProperties sparkYunAgentProperties;

    public static String YARN_LOG_STDOUT_REGEX = "\nLogType:stdout\\s*([\\s\\S]*?)\\s*End of LogType:stdout";

    public static String YARN_LOG_STDERR_REGEX = "\nLogType:stderr\\s*([\\s\\S]*?)\\s*End of LogType:stderr";

    public static String YARN_LOG_RESULT_REGEX = "LogType:spark-yun\\s*([\\s\\S]*?)\\s*End of LogType:spark-yun";

    @Override
    public String getAgentType() {
        return AgentType.YARN;
    }

    @Override
    public String getMaster(String sparkHomePath) {
        return "yarn";
    }

    @Override
    public SparkLauncher getSparkLauncher(SubmitWorkReq submitWorkReq) throws Exception {


        SparkLauncher sparkLauncher = new SparkLauncher().setVerbose(false)
            .setMainClass(submitWorkReq.getSparkSubmit().getMainClass()).setDeployMode("cluster")
            .setAppName(submitWorkReq.getSparkSubmit().getAppName() + "-" + submitWorkReq.getWorkType() + "-"
                + submitWorkReq.getWorkId() + "-" + submitWorkReq.getWorkInstanceId())
            .setMaster(getMaster(submitWorkReq.getSparkHomePath()))
            .setAppResource(submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                + submitWorkReq.getSparkSubmit().getAppResource())
            .setSparkHome(submitWorkReq.getAgentHomePath() + File.separator + "spark-min");

        if (WorkType.SPARK_JAR.equals(submitWorkReq.getWorkType())) {
            sparkLauncher
                .setAppName(submitWorkReq.getSparkSubmit().getAppName() + "-" + submitWorkReq.getWorkType() + "-"
                    + submitWorkReq.getWorkId() + "-" + submitWorkReq.getWorkInstanceId())
                .setAppResource(submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + submitWorkReq.getSparkSubmit().getAppResource());
        } else if (WorkType.PY_SPARK.equals(submitWorkReq.getWorkType())) {
            sparkLauncher
                .setAppName("zhiqingyun-" + submitWorkReq.getWorkType() + "-" + submitWorkReq.getWorkId() + "-"
                    + submitWorkReq.getWorkInstanceId())
                .setAppResource(submitWorkReq.getAgentHomePath() + File.separator + "works" + File.separator
                    + submitWorkReq.getWorkInstanceId() + ".py");
        } else {
            sparkLauncher
                .setAppName("zhiqingyun-" + submitWorkReq.getWorkType() + "-" + submitWorkReq.getWorkId() + "-"
                    + submitWorkReq.getWorkInstanceId())
                .setAppResource(submitWorkReq.getAgentHomePath() + File.separator + "plugins" + File.separator
                    + submitWorkReq.getSparkSubmit().getAppResource());
        }

        if (!Strings.isEmpty(submitWorkReq.getAgentHomePath())) {
            File[] jarFiles = new File(submitWorkReq.getAgentHomePath() + File.separator + "lib").listFiles();
            if (jarFiles != null) {
                for (File jar : jarFiles) {
                    try {
                        if (jar.getName().contains("hive") || jar.getName().contains("zhiqingyun-agent.jar")) {
                            continue;
                        }
                        sparkLauncher.addJar(jar.toURI().toURL().toString());
                    } catch (MalformedURLException e) {
                        log.error(e.getMessage(), e);
                        throw new Exception("添加lib中文件异常");
                    }
                }
            }
        }

        // 引入excel文件
        if (submitWorkReq.getPluginReq().getCsvFilePath() != null) {
            sparkLauncher.addFile(submitWorkReq.getPluginReq().getCsvFilePath());
        }

        // 添加额外依赖
        if (submitWorkReq.getLibConfig() != null) {
            submitWorkReq.getLibConfig().forEach(e -> sparkLauncher
                .addJar(submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator + e + ".jar"));
        }

        // 添加自定义函数
        if (submitWorkReq.getFuncConfig() != null) {
            submitWorkReq.getFuncConfig().forEach(e -> sparkLauncher.addJar(
                submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator + e.getFileId() + ".jar"));
        }

        if (WorkType.SPARK_JAR.equals(submitWorkReq.getWorkType())) {
            sparkLauncher.addAppArgs(submitWorkReq.getArgs());
        } else {
            sparkLauncher.addAppArgs(Base64.getEncoder()
                .encodeToString(submitWorkReq.getPluginReq() == null ? submitWorkReq.getArgsStr().getBytes()
                    : JSON.toJSONString(submitWorkReq.getPluginReq()).getBytes()));
        }

        String hiveUsername = submitWorkReq.getSparkSubmit().getConf().get("qing.hive.username");
        if (Strings.isNotEmpty(hiveUsername)) {
            sparkLauncher.setConf("spark.yarn.appMasterEnv.HADOOP_USER_NAME", hiveUsername);
            sparkLauncher.setConf("spark.executorEnv.HADOOP_USER_NAME", hiveUsername);
        }

        // 删除自定义属性
        submitWorkReq.getSparkSubmit().getConf().remove("qing.hive.username");

        // 调整spark.yarn.submit.waitAppCompletion，减少资源消耗
        sparkLauncher.setConf("spark.yarn.submit.waitAppCompletion", "false");
        submitWorkReq.getSparkSubmit().getConf().forEach(sparkLauncher::setConf);

        return sparkLauncher;
    }


    @Override
    public String submitWork(SparkLauncher sparkLauncher) throws Exception {

        // 提交作业进程
        Process launch = sparkLauncher.launch();
        InputStream errorStream = launch.getErrorStream();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8));

        // 正则获取applicationId表达式
        Pattern[] patterns = {Pattern.compile("application_\\d+_\\d+")};

        // 配置提交超时时间，防止while死循环
        long timeoutExpiredMs = System.currentTimeMillis() + sparkYunAgentProperties.getSubmitTimeout() * 1000;

        // 记录日志
        StringBuilder errLog = new StringBuilder();
        String line;
        String applicationId = "";
        while ((line = errorReader.readLine()) != null) {
            errLog.append(line).append("\n");

            // 提交超时退出
            long waitMillis = timeoutExpiredMs - System.currentTimeMillis();
            if (waitMillis <= 0) {
                launch.destroy();
                throw new Exception("提交超时:" + errLog);
            }

            // 正则表达式逐个匹配，匹配到立马返回applicationId
            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    applicationId = matcher.group(matcher.groupCount() > 0 ? 1 : 0);
                }
            }
        }

        try {
            int exitCode = launch.waitFor();
            if (exitCode == 1) {
                throw new Exception("提交作业异常:" + errLog);
            } else if (Strings.isNotEmpty(applicationId)) {
                return applicationId;
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new Exception("提交作业中断:" + e.getMessage());
        } finally {
            launch.destroy();
        }

        // 如果获取不到applicationId，返回完整日志
        throw new Exception("无法获取applicationId，请检查提交日志:" + errLog);
    }

    @Override
    public GetWorkInfoRes getWorkInfo(String appId, String sparkHomePath) throws Exception {

        String getStatusCmdFormat = "yarn application -status %s";

        Process process = Runtime.getRuntime().exec(String.format(getStatusCmdFormat, appId));

        InputStream inputStream = process.getInputStream();
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder inputLog = new StringBuilder();
        String line;
        String finalState = "";
        String appState = "";
        while ((line = inputReader.readLine()) != null) {
            inputLog.append(line).append("\n");

            if (finalState.isEmpty()) {
                String finalStatePattern = "Final-State : (\\w+)";
                Pattern finalStateRegex = Pattern.compile(finalStatePattern);
                Matcher finalStateMatcher = finalStateRegex.matcher(line);
                if (finalStateMatcher.find()) {
                    finalState = finalStateMatcher.group(1);
                }
            }

            if (appState.isEmpty()) {
                String appStatePattern = "State : (\\w+)";
                Pattern appStateRegex = Pattern.compile(appStatePattern);
                Matcher appStateMatcher = appStateRegex.matcher(line);
                if (appStateMatcher.find()) {
                    appState = appStateMatcher.group(1);
                }
            }

            if (!finalState.isEmpty() && !appState.isEmpty()) {
                return GetWorkInfoRes.builder().appId(appId).finalState(finalState).appState(appState).build();
            }
        }


        try {
            int exitCode = process.waitFor();
            if (exitCode == 1) {
                throw new Exception("获取作业状态异常:" + inputLog);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new Exception("获取作业状态中断:" + e.getMessage());
        }

        throw new Exception("无法获取作业状态，请检查日志:" + inputLog);
    }

    @Override
    public String getStdoutLog(String appId, String sparkHomePath) throws Exception {

        String getLogCmdFormat = "yarn logs -applicationId %s";
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
                throw new Exception(errLog.toString());
            } else {
                Pattern regex = Pattern.compile(YARN_LOG_STDOUT_REGEX);
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
                return log;
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getCustomJarStdoutLog(String appId, String sparkHomePath) throws Exception {

        String getLogCmdFormat = "yarn logs -applicationId %s";
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
                throw new Exception(errLog.toString());
            } else {
                Pattern regex = Pattern.compile(YARN_LOG_STDOUT_REGEX);
                Matcher matcher = regex.matcher(errLog);
                if (matcher.find()) {
                    return matcher.group(1).replace("LogType:stdout\n", "").replace("\nEnd of LogType:stdout", "");
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
        return "日志未生成";
    }

    @Override
    public String getStderrLog(String appId, String sparkHomePath) throws Exception {

        String getLogCmdFormat = "yarn logs -applicationId %s";
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
                throw new Exception(errLog.toString());
            } else {
                Pattern regex = Pattern.compile(YARN_LOG_STDERR_REGEX);
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
                return log;
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getWorkDataStr(String appId, String sparkHomePath) throws Exception {

        String getLogCmdFormat = "yarn logs -applicationId %s";

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
                throw new Exception(errLog.toString());
            } else {
                Pattern regex = Pattern.compile(YARN_LOG_RESULT_REGEX);
                Matcher matcher = regex.matcher(errLog);
                String log = "";
                while (matcher.find() && Strings.isEmpty(log)) {
                    log = matcher.group().replace("LogType:spark-yun\n", "").replace("\nEnd of LogType:spark-yun", "");
                }
                return log;
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void stopWork(String appId, String sparkHomePath, String agentHomePath) throws Exception {

        String killAppCmdFormat = "yarn application -kill %s";
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
                throw new Exception(errLog.toString());
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Map<String, String> submitWorkForPySpark(SparkLauncher sparkLauncher) throws Exception {
        return Collections.emptyMap();
    }
}
