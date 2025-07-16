package com.isxcode.star.agent.run.impl;

import com.alibaba.fastjson2.JSON;
import com.isxcode.star.agent.properties.SparkYunAgentProperties;
import com.isxcode.star.agent.run.AgentService;
import com.isxcode.star.api.agent.constants.AgentType;
import com.isxcode.star.api.agent.req.SubmitWorkReq;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class YarnAgentService implements AgentService {

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
    public SparkLauncher getSparkLauncher(SubmitWorkReq submitWorkReq) {


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
                        throw new IsxAppException("50010", "添加lib中文件异常", e.getMessage());
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

        Process launch = sparkLauncher.launch();

        // 同时读取标准输出和错误输出
        InputStream errorStream = launch.getErrorStream();
        InputStream inputStream = launch.getInputStream();

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8));
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        long timeoutExpiredMs = System.currentTimeMillis() + sparkYunAgentProperties.getSubmitTimeout() * 1000;

        StringBuilder allLog = new StringBuilder();
        String applicationId = null;

        // 改进的正则表达式，支持多种格式
        Pattern[] patterns = {Pattern.compile("Submitted application (application_\\d+_\\d+)"),
                Pattern.compile("Application report for (application_\\d+_\\d+)"),
                Pattern.compile("tracking URL: .*/proxy/(application_\\d+_\\d+)"),
                Pattern.compile("application_\\d+_\\d+")};

        try {
            boolean processRunning = true;
            while (processRunning) {
                // 检查超时
                long waitMillis = timeoutExpiredMs - System.currentTimeMillis();
                if (waitMillis <= 0) {
                    launch.destroy();
                    log.error("提交超时，日志内容：\n{}", allLog.toString());
                    throw new IsxAppException("提交超时：" + allLog.toString());
                }

                // 读取错误流
                if (errorReader.ready()) {
                    String line = errorReader.readLine();
                    if (line != null) {
                        allLog.append("[ERROR] ").append(line).append("\n");
                        log.debug("Error stream: {}", line);

                        // 尝试匹配 applicationId
                        if (applicationId == null) {
                            applicationId = extractApplicationId(line, patterns);
                        }
                    }
                }

                // 读取标准输出流
                if (inputReader.ready()) {
                    String line = inputReader.readLine();
                    if (line != null) {
                        allLog.append("[INFO] ").append(line).append("\n");
                        log.debug("Input stream: {}", line);

                        // 尝试匹配 applicationId
                        if (applicationId == null) {
                            applicationId = extractApplicationId(line, patterns);
                        }
                    }
                }

                // 如果找到了 applicationId，返回结果
                if (applicationId != null) {
                    log.info("成功获取到 applicationId: {}", applicationId);
                    return applicationId;
                }

                // 检查进程是否还在运行
                try {
                    int exitCode = launch.exitValue();
                    processRunning = false;

                    // 读取剩余的输出
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        allLog.append("[ERROR] ").append(line).append("\n");
                        if (applicationId == null) {
                            applicationId = extractApplicationId(line, patterns);
                        }
                    }
                    while ((line = inputReader.readLine()) != null) {
                        allLog.append("[INFO] ").append(line).append("\n");
                        if (applicationId == null) {
                            applicationId = extractApplicationId(line, patterns);
                        }
                    }

                    if (exitCode != 0) {
                        log.error("Spark 提交失败，退出码：{}，日志：\n{}", exitCode, allLog.toString());
                        throw new IsxAppException("Spark 提交失败，退出码：" + exitCode + "，错误信息：" + allLog.toString());
                    }
                } catch (IllegalThreadStateException e) {
                    // 进程还在运行，继续循环
                    Thread.sleep(100); // 短暂休眠避免CPU占用过高
                }
            }

            // 如果进程结束但仍未找到 applicationId
            if (applicationId != null) {
                return applicationId;
            }

        } catch (InterruptedException e) {
            log.error("等待进程被中断：{}", e.getMessage(), e);
            throw new IsxAppException("提交过程被中断：" + e.getMessage());
        } finally {
            try {
                errorReader.close();
                inputReader.close();
            } catch (IOException e) {
                log.warn("关闭流时出错：{}", e.getMessage());
            }
            launch.destroy();
        }

        log.error("无法获取applicationId，完整日志：\n{}", allLog.toString());
        throw new IsxAppException("无法获取applicationId，请检查Spark配置和日志：" + allLog.toString());
    }

    /**
     * 从日志行中提取 applicationId
     */
    private String extractApplicationId(String line, Pattern[] patterns) {
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String result = matcher.group(matcher.groupCount() > 0 ? 1 : 0);
                // 确保结果是 application_xxx_xxx 格式
                if (result.startsWith("application_")) {
                    log.debug("从日志行中提取到 applicationId: {} (行内容: {})", result, line);
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    public String getWorkStatus(String appId, String sparkHomePath) throws Exception {

        String getStatusCmdFormat = "yarn application -status %s";

        Process process = Runtime.getRuntime().exec(String.format(getStatusCmdFormat, appId));

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder errLog = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            errLog.append(line).append("\n");

            String pattern = "Final-State : (\\w+)";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(line);
            if (matcher.find()) {
                String status = matcher.group(1);
                if ("UNDEFINED".equals(status)) {
                    status = "RUNNING";
                }
                return status;
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
                throw new IsxAppException(errLog.toString());
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
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public String getCustomWorkStdoutLog(String appId, String sparkHomePath) throws Exception {

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
                throw new IsxAppException(errLog.toString());
            } else {
                Pattern regex = Pattern.compile(YARN_LOG_STDOUT_REGEX);
                Matcher matcher = regex.matcher(errLog);
                if (matcher.find()) {
                    return matcher.group(1).replace("LogType:stdout\n", "").replace("\nEnd of LogType:stdout", "");
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
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
                throw new IsxAppException(errLog.toString());
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
            throw new IsxAppException(e.getMessage());
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
                throw new IsxAppException(errLog.toString());
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
            throw new IsxAppException(e.getMessage());
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
                throw new IsxAppException(errLog.toString());
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

}
