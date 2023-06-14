package com.isxcode.star.yun.agent.service;

import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import com.isxcode.star.api.pojos.yun.agent.req.SparkSubmit;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class YarnAgentService implements AgentService {

  @Override
  public String getMaster() {
    return "yarn";
  }

  @Override
  public SparkLauncher genSparkLauncher(
      PluginReq pluginReq, SparkSubmit sparkSubmit, String agentHomePath) {

    SparkLauncher sparkLauncher =
        new SparkLauncher()
            .setVerbose(sparkSubmit.isVerbose())
            .setMainClass(sparkSubmit.getMainClass())
            .setDeployMode("cluster")
            .setAppName("zhiqingyun-job")
            .setMaster(getMaster())
            .setAppResource(
                agentHomePath
                    + File.separator
                    + "plugins"
                    + File.separator
                    + sparkSubmit.getAppResource())
            .setSparkHome(agentHomePath + File.separator + "spark-min");

    if (!Strings.isEmpty(agentHomePath)) {
      File[] jarFiles = new File(agentHomePath + File.separator + "lib").listFiles();
      if (jarFiles != null) {
        for (File jar : jarFiles) {
          try {
            sparkLauncher.addJar(jar.toURI().toURL().toString());
          } catch (MalformedURLException e) {
            log.error(e.getMessage());
            throw new SparkYunException("50010", "添加lib中文件异常", e.getMessage());
          }
        }
      }
    }

    sparkLauncher.addAppArgs(String.valueOf(sparkSubmit.getAppArgs()));

    sparkSubmit.getConf().forEach(sparkLauncher::setConf);

    return sparkLauncher;
  }

  @Override
  public String executeWork(SparkLauncher sparkLauncher) throws IOException {

    Process launch = sparkLauncher.launch();
    InputStream inputStream = launch.getErrorStream();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

    // 设置超时时间


    StringBuilder errLog = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      errLog.append(line).append("\n");

      String pattern = "Submitted application application_\\d+_\\d+";
      Pattern regex = Pattern.compile(pattern);
      Matcher matcher = regex.matcher(line);
      if (matcher.find()) {
        return matcher.group().replace("Submitted application ", "");
      }
    }

    try {
      int exitCode = launch.waitFor();
      if (exitCode == 1) {
        throw new SparkYunException(errLog.toString());
      }
    } catch (InterruptedException e) {
      throw new SparkYunException(e.getMessage());
    }

    throw new SparkYunException("无法获取applicationId");
  }

  @Override
  public String getAppStatus(String appId) throws IOException {

    String getStatusCmdFormat = "yarn application -status %s";

    Process process = Runtime.getRuntime().exec(String.format(getStatusCmdFormat, appId));

    InputStream inputStream = process.getInputStream();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

    StringBuilder errLog = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      errLog.append(line).append("\n");

      String pattern = "Final-State : (\\w+)";
      Pattern regex = Pattern.compile(pattern);
      Matcher matcher = regex.matcher(line);
      if (matcher.find()) {
        return matcher.group(1);
      }
    }

    try {
      int exitCode = process.waitFor();
      if (exitCode == 1) {
        throw new SparkYunException(errLog.toString());
      }
    } catch (InterruptedException e) {
      throw new SparkYunException(e.getMessage());
    }

    throw new SparkYunException("获取状态异常");
  }

  @Override
  public String getAppLog(String appId) throws IOException {

    String getLogCmdFormat = "yarn logs -applicationId %s";
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
      int exitCode = process.waitFor();
      if (exitCode == 1) {
        throw new SparkYunException(errLog.toString());
      } else {
        Pattern regex = Pattern.compile("LogType:stderr\\s*([\\s\\S]*?)\\s*End of LogType:stderr");
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
      throw new SparkYunException(e.getMessage());
    }
  }

  @Override
  public String getAppData(String appId) throws IOException {

    String getLogCmdFormat = "yarn logs -applicationId %s";

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
      int exitCode = process.waitFor();
      if (exitCode == 1) {
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
    } catch (InterruptedException e) {
      throw new SparkYunException(e.getMessage());
    }
  }

  @Override
  public void killApp(String appId) throws IOException {

    String killAppCmdFormat = "yarn application -kill %s";
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
      int exitCode = process.waitFor();
      if (exitCode == 1) {
        throw new SparkYunException(errLog.toString());
      }
    } catch (InterruptedException e) {
      throw new SparkYunException(e.getMessage());
    }
  }
}
