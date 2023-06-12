package com.isxcode.star.yun.agent.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import com.isxcode.star.api.pojos.yun.agent.req.SparkSubmit;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkLauncher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StandaloneAgentService implements AgentService {

  @Override
  public String getMaster() {

    String sparkHome = System.getenv("SPARK_HOME");
    String defaultSparkConfig = sparkHome + "/conf/spark-defaults.conf";

    try (BufferedReader reader = new BufferedReader(new FileReader(defaultSparkConfig))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.matches("^spark\\.master\\s+(.+)")) {
          return line.split("\\s+")[1];
        }
      }
      throw new SparkYunException("无法获取master url");
    } catch (IOException e) {
      throw new SparkYunException("无法获取master url");
    }
  }

  public String getMasterWebUrl() {

    String sparkHome = System.getenv("SPARK_HOME");
    String defaultSparkConfig = sparkHome + "/conf/spark-defaults.conf";

    try (BufferedReader reader = new BufferedReader(new FileReader(defaultSparkConfig))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.matches("^spark\\.master\\.web\\.url\\s+(.+)")) {
          return line.split("\\s+")[1];
        }
      }
      throw new SparkYunException("无法获取master url");
    } catch (IOException e) {
      throw new SparkYunException("无法获取master url");
    }
  }

  @Override
  public SparkLauncher genSparkLauncher(
    PluginReq pluginReq, SparkSubmit sparkSubmit, String agentHomePath) {

    SparkLauncher sparkLauncher =
      new SparkLauncher()
        .setVerbose(sparkSubmit.isVerbose())
        .setMainClass(sparkSubmit.getMainClass())
        .setDeployMode(sparkSubmit.getDeployMode())
        .setAppName(sparkSubmit.getAppName())
        .setMaster(getMaster())
        .setAppResource(
          agentHomePath
            + File.separator
            + "plugins"
            + File.separator
            + sparkSubmit.getAppResource())
        .setSparkHome(sparkSubmit.getSparkHome());

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

    if (sparkSubmit.getAppArgs().isEmpty()) {
      sparkLauncher.addAppArgs(
        Base64.getEncoder().encodeToString(JSON.toJSONString(pluginReq).getBytes()));
    } else {
      sparkLauncher.addAppArgs(String.valueOf(sparkSubmit.getAppArgs()));
    }

    sparkSubmit.getConf().forEach(sparkLauncher::setConf);

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

      String pattern = "driver-\\d+-\\d is RUNNING";
      Pattern regex = Pattern.compile(pattern);
      Matcher matcher = regex.matcher(line);
      if (matcher.find()) {
        return matcher.group().replace(" is RUNNING", "");
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

    throw new SparkYunException("无法获取submissionId");
  }

  @Override
  public String getAppStatus(String submissionId) throws IOException {

    Document doc = Jsoup.connect(getMasterWebUrl()).get();

    Element completedDriversTable = doc.selectFirst(".aggregated-completedDrivers table");
    Elements completedDriversRows = completedDriversTable.select("tbody tr");

    Element runningDriversTable = doc.selectFirst(".aggregated-activeDrivers table");
    Elements runningDriversRows = runningDriversTable.select("tbody tr");

    Map<String, String> apps = new HashMap<>();

    for (Element row : completedDriversRows) {
      apps.put(row.selectFirst("td:nth-child(1)").text(), row.selectFirst("td:nth-child(4)").text());
    }

    for (Element row : runningDriversRows) {
      apps.put(row.selectFirst("td:nth-child(1)").text(), row.select("td").get(7).text());
    }

    return apps.get(submissionId);
  }

  @Override
  public String getAppLog(String submissionId) throws IOException {

    Document doc = Jsoup.connect(getMasterWebUrl()).get();

    Element completedDriversTable = doc.selectFirst(".aggregated-completedDrivers table");
    Elements completedDriversRows = completedDriversTable.select("tbody tr");

    Map<String, String> apps = new HashMap<>();

    for (Element row : completedDriversRows) {
      apps.put(row.selectFirst("td:nth-child(1)").text(), row.select("td:nth-child(3) a").first().attr("href"));
    }

    String workUrl = apps.get(submissionId);

    doc = Jsoup.connect(workUrl).get();
    Elements rows = doc.select(".aggregated-finishedDrivers table tbody tr");
    Map<String, String> driversMap = new HashMap<>();
    for (Element row : rows) {
      String driverId = row.select("td:nth-child(1)").text();
      String stderrUrl = row.select("td:nth-child(7) a[href$=stderr]").attr("href");
      driversMap.put(driverId, stderrUrl);
    }

    // 从errlog连接中，爬取日志
    String errlogUrl = driversMap.get(submissionId);
    doc = Jsoup.connect(errlogUrl).get();
    Element preElement = doc.selectFirst("pre");

    return preElement.text();
  }

  @Override
  public String getAppData(String submissionId) {



    return null;
  }

  @Override
  public void killApp(String appId) throws IOException {

  }
}
