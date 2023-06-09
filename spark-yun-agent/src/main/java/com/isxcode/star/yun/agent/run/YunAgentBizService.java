package com.isxcode.star.yun.agent.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.pojos.yun.agent.req.YagExecuteWorkReq;
import com.isxcode.star.api.pojos.yun.agent.res.YagExecuteWorkRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetDataRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetLogRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetStatusRes;
import com.isxcode.star.yarn.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkLauncher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.isxcode.star.yarn.utils.YarnUtils.formatApplicationId;
import static com.isxcode.star.yarn.utils.YarnUtils.initYarnClient;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

/**
 * 代理服务层.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YunAgentBizService {

  /**
   * 执行作业.
   */
  public YagExecuteWorkRes executeWork(YagExecuteWorkReq yagExecuteWorkReq) throws IOException {

    SparkLauncher sparkLauncher = new SparkLauncher()
      .setVerbose(yagExecuteWorkReq.getSparkSubmit().isVerbose())
      .setMainClass(yagExecuteWorkReq.getSparkSubmit().getMainClass())
      .setDeployMode(yagExecuteWorkReq.getSparkSubmit().getDeployMode())
      .setMaster(yagExecuteWorkReq.getSparkSubmit().getMaster())
      .setAppName(yagExecuteWorkReq.getSparkSubmit().getAppName())
      .setAppResource(yagExecuteWorkReq.getSparkSubmit().getAppResource())
      .setSparkHome(yagExecuteWorkReq.getSparkSubmit().getSparkHome());

    if (!Strings.isEmpty(yagExecuteWorkReq.getAgentHomePath())) {
      File[] jarFiles = new File(yagExecuteWorkReq.getAgentHomePath() + File.separator + "lib").listFiles();
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

    if (yagExecuteWorkReq.getSparkSubmit().getAppArgs().isEmpty()) {
      sparkLauncher.addAppArgs(Base64.getEncoder().encodeToString(JSON.toJSONString(yagExecuteWorkReq.getPluginReq()).getBytes()));
    }else{
      sparkLauncher.addAppArgs(String.valueOf(yagExecuteWorkReq.getSparkSubmit().getAppArgs()));
    }

    yagExecuteWorkReq.getSparkSubmit().getConf().forEach(sparkLauncher::setConf);

    Process launch = sparkLauncher.launch();
    InputStream inputStream = launch.getErrorStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

    // 等待作业响应
    String applicationId;

    StringBuilder errLog = new StringBuilder();
    int errIndex = 0;
    boolean isError = false;

    // 如果发现错误日志，返回100行日志
    String line;
    while ((line = reader.readLine()) != null) {

      if (errIndex > 100) {
        throw new SparkYunException(errLog.toString());
      }

      if (!isError) {
        if ((containsIgnoreCase(line, "Error") || containsIgnoreCase(line, "Exception")) && !line.contains("at ")) {
          isError = true;
        }
      }

      if (isError) {
        errLog.append(line).append("\n");
        errIndex++;
      }

      System.out.println(line);
    }

    if (launch.exitValue() == 1) {
      throw new SparkYunException(errLog.toString());
    }

    return null;
  }

  public YagGetStatusRes getStatus(String applicationId) {

    // 初始化本地yarn客户端
    YarnClient yarnClient = initYarnClient();

    // 获取状态
    ApplicationReport applicationReport;
    try {
      applicationReport = yarnClient.getApplicationReport(formatApplicationId(applicationId));
    } catch (YarnException | IOException e) {
      log.error(e.getMessage());
      throw new SparkYunException("50010", "获取状态异常", e.getMessage());
    }

    return new YagGetStatusRes(
      String.valueOf(applicationReport.getYarnApplicationState()),
      String.valueOf(applicationReport.getFinalApplicationStatus()),
      String.valueOf(applicationReport.getTrackingUrl()),
      applicationId);
  }

  public YagGetLogRes getLog(String applicationId) {

    // 使用yarn工具获取日志
    Map<String, String> map = LogUtils.parseYarnLog(applicationId);

    // 获取日志
    String stdErrLog = map.get("stderr");

    if (Strings.isEmpty(stdErrLog)) {
      throw new SparkYunException("50010", "查询失败，日志暂未生成");
    }

    return new YagGetLogRes(stdErrLog, applicationId);
  }

  public YagGetDataRes getData(String applicationId) {

    // 使用yarn工具获取日志
    Map<String, String> map = LogUtils.parseYarnLog(applicationId);
    String stdoutLog = map.get("stdout");

    if (Strings.isEmpty(stdoutLog)) {
      throw new SparkYunException("50010", "查询失败，数据暂未生成");
    }

    return new YagGetDataRes(JSON.parseArray(stdoutLog, List.class), applicationId);
  }

  public void stopJob(String applicationId) {

    YarnClient yarnClient = initYarnClient();

    try {
      yarnClient.killApplication(formatApplicationId(applicationId));
    } catch (YarnException | IOException e) {
      log.error(e.getMessage());
      throw new SparkYunException("50010", "中止作业异常", e.getMessage());
    }
  }

  public String getStatus1(String jobName) throws IOException {

    // 爬虫获取状态
    Document doc = Jsoup.connect("http://39.100.67.15:8081/").get();

    Element completedAppsTable = doc.selectFirst("div.aggregated-completedApps table");
    Elements completedRows = completedAppsTable.select("tbody tr");

    Element activeAppsTable = doc.selectFirst("div.aggregated-activeApps table");
    Elements activeRows = activeAppsTable.select("tbody tr");

    Map<String, String> apps = new HashMap<>();

    for (Element row : completedRows) {
      apps.put(row.select("td").get(1).text(), row.select("td").get(7).text());
    }

    for (Element row : activeRows) {
      apps.put(row.select("td").get(1).text(), row.select("td").get(7).text());
    }

    return apps.get(jobName);
  }

  public String getAppId(String jobName) throws IOException {

    // 爬虫获取状态
    Document doc = Jsoup.connect("http://39.100.67.15:8081/").get();

    Element completedAppsTable = doc.selectFirst("div.aggregated-completedApps table");
    Elements completedRows = completedAppsTable.select("tbody tr");

    Element activeAppsTable = doc.selectFirst("div.aggregated-activeApps table");
    Elements activeRows = activeAppsTable.select("tbody tr");

    Map<String, String> apps = new HashMap<>();

    for (Element row : completedRows) {
      apps.put(row.select("td").get(1).text(), row.select("td").get(0).text());
    }

    for (Element row : activeRows) {
      apps.put(row.select("td").get(1).text(), row.select("td").get(0).text().replace("(kill)", ""));
    }

    return apps.get(jobName);
  }

  public String getOutUrl() throws IOException {

    // 爬虫获取状态
    Document doc = Jsoup.connect("http://39.100.67.15:8081/app/?appId=app-20230608171856-0001").get();

    // 提取stdout链接
    Elements stdoutLinks = doc.select("div.aggregated-removedExecutors tbody tr td:nth-child(8) a[href]:eq(0)");
    for (Element link : stdoutLinks) {
     return  link.attr("href");
    }
    return null;
  }

  public String getErrUrl() throws IOException {

    // 爬虫获取状态
    Document doc = Jsoup.connect("http://39.100.67.15:8081/app/?appId=app-20230608171856-0001").get();

    // 提取stderr链接
    Elements stderrLinks = doc.select("div.aggregated-removedExecutors tbody tr td:nth-child(8) a[href]:eq(1)");
    for (Element link : stderrLinks) {
      return link.attr("href");
    }
    return null;
  }

  public String getErrLog() throws IOException {

    Document doc = Jsoup.connect("http://39.100.67.15:8082/logPage/?appId=app-20230608171856-0001&executorId=0&logType=stderr").get();

    Element preElement = doc.selectFirst("pre");

    return preElement.text();
  }

  public String getOutLog() throws IOException {

    Document doc = Jsoup.connect("http://39.100.67.15:8082/logPage/?appId=app-20230608171856-0001&executorId=0&logType=stdout").get();

    Element preElement = doc.selectFirst("pre");

    return preElement.text();
  }

  public void killWork() throws IOException, ParseException {

    String url = "http://39.100.67.15:8081/app/kill/";

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(url);

    String payload = "id=app-20230608190523-0016&terminate=true";
    StringEntity stringEntity = new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);

    httpPost.setEntity(stringEntity);

    try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
      // 获取响应实体
      HttpEntity responseEntity = response.getEntity();
      if (responseEntity != null) {
        String responseBody = EntityUtils.toString(responseEntity);
        System.out.println(responseBody);
      }
    }
  }

  public static void main(String[] args) throws IOException, ParseException {


  }

}
