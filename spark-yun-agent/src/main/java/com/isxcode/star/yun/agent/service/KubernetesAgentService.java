package com.isxcode.star.yun.agent.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import com.isxcode.star.api.pojos.yun.agent.req.SparkSubmit;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KubernetesAgentService implements AgentService {
  @Override
  public SparkLauncher genSparkLauncher(
      PluginReq pluginReq, SparkSubmit sparkSubmit, String agentHomePath) {

    SparkLauncher sparkLauncher =
        new SparkLauncher()
            .setVerbose(sparkSubmit.isVerbose())
            .setMainClass(sparkSubmit.getMainClass())
            .setDeployMode(sparkSubmit.getDeployMode())
            .setAppName(sparkSubmit.getAppName())
            .setMaster(sparkSubmit.getMaster())
            .setAppResource("local:///opt/spark/examples/jars/" + sparkSubmit.getAppResource())
            .setSparkHome(sparkSubmit.getSparkHome());

    if (!Strings.isEmpty(agentHomePath)) {
      File[] jarFiles = new File(agentHomePath + File.separator + "lib").listFiles();
      if (jarFiles != null) {
        for (int i = 0; i < jarFiles.length; i++) {
          sparkLauncher.addJar("local:///opt/spark/examples/jars/lib/" + jarFiles[i].getName());
          sparkLauncher.setConf(
              "spark.kubernetes.driver.volumes.hostPath." + i + ".mount.path",
              "/opt/spark/examples/jars/lib/" + jarFiles[i].getName());
          sparkLauncher.setConf(
              "spark.kubernetes.driver.volumes.hostPath." + i + ".mount.readOnly", "false");
          sparkLauncher.setConf(
              "spark.kubernetes.driver.volumes.hostPath." + i + ".options.path",
              jarFiles[i].getPath());
        }
      }
    }

    if (sparkSubmit.getAppArgs().isEmpty()) {
      sparkLauncher.addAppArgs(
          Base64.getEncoder().encodeToString(JSON.toJSONString(pluginReq).getBytes()));
    } else {
      sparkLauncher.addAppArgs(String.valueOf(sparkSubmit.getAppArgs()));
    }

    sparkSubmit.getConf().forEach(sparkLauncher::setConf);

    sparkLauncher.setConf("spark.kubernetes.container.image", "zhiqingyun/spark:latest");
    sparkLauncher.setConf(
        "spark.kubernetes.driver.volumes.hostPath.jar.mount.path",
        "/opt/spark/examples/jars/" + sparkSubmit.getAppResource());
    sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.jar.mount.readOnly", "false");
    sparkLauncher.setConf(
        "spark.kubernetes.driver.volumes.hostPath.jar.options.path",
        agentHomePath + File.separator + "plugins" + File.separator + sparkSubmit.getAppResource());
    sparkLauncher.setConf("spark.kubernetes.authenticate.driver.serviceAccountName", "zhiqingyun");
    sparkLauncher.setConf("spark.kubernetes.namespace", "spark-yun");

    return sparkLauncher;
  }

  @Override
  public String executeWork(SparkLauncher sparkLauncher) throws IOException {

    Process launch = sparkLauncher.launch();
    InputStream inputStream = launch.getErrorStream();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

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
      launch.waitFor();
    } catch (InterruptedException e) {
      throw new SparkYunException(e.getMessage());
    }

    if (launch.exitValue() == 1) {
      throw new SparkYunException(errLog.toString());
    }

    throw new SparkYunException("无法获取submissionID");
  }

  @Override
  public String getAppStatus(String appId) throws IOException {

    String getStatusCmdFormat = "kubectl get pod %s -n spark-yun";

    // 使用命令获取状态
    Process process = Runtime.getRuntime().exec(String.format(getStatusCmdFormat, appId));

    InputStream inputStream = process.getInputStream();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

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

    try {
      process.waitFor();
    } catch (InterruptedException e) {
      throw new SparkYunException(e.getMessage());
    }

    if (process.exitValue() == 1) {
      throw new SparkYunException(errLog.toString());
    }

    throw new SparkYunException("获取状态异常");
  }

  @Override
  public String getAppLog(String appId) throws IOException {

    String getLogCmdFormat = "kubectl logs -f %s -n spark-yun";

    // 使用命令获取状态
    Process process = Runtime.getRuntime().exec(String.format(getLogCmdFormat, appId));

    InputStream inputStream = process.getInputStream();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

    StringBuilder errLog = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      errLog.append(line).append("\n");
    }

    try {
      process.waitFor();
    } catch (InterruptedException e) {
      throw new SparkYunException(e.getMessage());
    }

    if (process.exitValue() == 1) {
      throw new SparkYunException(errLog.toString());
    } else {
      Pattern regex =
          Pattern.compile("LogType:spark-yun\\s*([\\s\\S]*?)\\s*End of LogType:spark-yun");
      Matcher matcher = regex.matcher(errLog);
      String log = errLog.toString();
      if (matcher.find()) {
        log = log.replace(matcher.group(), "");
      }
      return log;
    }
  }

  @Override
  public String getAppData(String appId) throws IOException {

    String getLogCmdFormat = "kubectl logs -f %s -n spark-yun";

    // 使用命令获取状态
    Process process = Runtime.getRuntime().exec(String.format(getLogCmdFormat, appId));

    InputStream inputStream = process.getInputStream();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

    StringBuilder errLog = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      errLog.append(line).append("\n");
    }

    try {
      process.waitFor();
    } catch (InterruptedException e) {
      throw new SparkYunException(e.getMessage());
    }

    if (process.exitValue() == 1) {
      throw new SparkYunException(errLog.toString());
    } else {
      Pattern regex =
          Pattern.compile("LogType:spark-yun\\s*([\\s\\S]*?)\\s*End of LogType:spark-yun");
      Matcher matcher = regex.matcher(errLog);
      String log = "";
      while (matcher.find() && Strings.isEmpty(log)) {
        log =
            matcher
                .group()
                .replace("LogType:spark-yun\n", "")
                .replace("\nEnd of LogType:spark-yun", "");
      }
      return log;
    }
  }

  @Override
  public void killApp(String appId) throws IOException {

    String killAppCmdFormat = "kubectl delete pod %s -n spark-yun";

    // 使用命令获取状态
    Process process = Runtime.getRuntime().exec(String.format(killAppCmdFormat, appId));

    InputStream inputStream = process.getInputStream();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

    StringBuilder errLog = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      errLog.append(line).append("\n");
    }

    try {
      process.waitFor();
    } catch (InterruptedException e) {
      throw new SparkYunException(e.getMessage());
    }

    if (process.exitValue() == 1) {
      throw new SparkYunException(errLog.toString());
    }
  }
}
