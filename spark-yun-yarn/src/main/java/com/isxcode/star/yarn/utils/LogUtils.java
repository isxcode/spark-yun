package com.isxcode.star.yarn.utils;

import com.isxcode.star.api.exception.SparkYunException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class LogUtils {

  public static Map<String, String> parseYarnLog(String applicationId) {

    String hadoopHomePath = System.getenv("HADOOP_HOME");
    String yarnSiteXml =
        hadoopHomePath
            + File.separator
            + "etc"
            + File.separator
            + "hadoop"
            + File.separator
            + "yarn-site.xml";
    String mapredSiteXml =
        hadoopHomePath
            + File.separator
            + "etc"
            + File.separator
            + "hadoop"
            + File.separator
            + "mapred-site.xml";

    // 读取yarn的配置文件
    Configuration yarnConf = new Configuration(false);
    try {
      yarnConf.addResource(Files.newInputStream(Paths.get(yarnSiteXml)));
      yarnConf.addResource(Files.newInputStream(Paths.get(mapredSiteXml)));
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new SparkYunException("50010", "未找到yarn配置文件", e.getMessage());
    }

    // 启动yarn客户端
    YarnClient yarnClient = YarnClient.createYarnClient();
    YarnConfiguration yarnConfig = new YarnConfiguration(yarnConf);
    yarnClient.init(yarnConfig);
    yarnClient.start();

    // 在配置文件中找到yarn.resourcemanager.webapp.address
    if (Strings.isEmpty(yarnConfig.get("yarn.resourcemanager.webapp.address"))) {
      throw new SparkYunException(
          "50010",
          "yarn配置异常",
          "请在yarn-site.xml中配置yarn.resourcemanager.webapp.address属性:${yarn.resourcemanager.hostname}:8088");
    }

    // 访问yarn作业日志页面
    String getAppInfoUrl =
        "http://"
            + yarnConfig.get("yarn.resourcemanager.webapp.address")
            + "/ws/v1/cluster/apps/"
            + applicationId;
    Map appInfoMap = new RestTemplate().getForObject(getAppInfoUrl, Map.class);
    Map<String, Map<String, Object>> appMap =
        (Map<String, Map<String, Object>>) appInfoMap.get("app");

    // 获取日志url
    String amContainerLogsUrl = String.valueOf(appMap.get("amContainerLogs"));

    // 使用jsoup解析日志网页
    Document doc;
    try {
      log.info("amContainerLogsUrl:{}", amContainerLogsUrl);
      doc = Jsoup.connect(amContainerLogsUrl).get();
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new SparkYunException("50010", "日志解析异常", e.getMessage());
    }

    // 获取content的元素
    Elements contentEls = doc.getElementsByClass("content");
    if (contentEls.isEmpty()) {
      throw new SparkYunException("50010", "日志解析异常");
    }

    // 开始解析
    Map<String, String> resultLog = new HashMap<>();
    Elements preElements = contentEls.get(0).getElementsByTag("pre");

    // 获取jobHistoryAddress前缀
    String jobHistoryAddress = yarnConfig.get("mapreduce.jobhistory.webapp.address");
    if (Strings.isEmpty(jobHistoryAddress)) {
      throw new SparkYunException(
          "50010",
          "yarn配置异常",
          "请在mapred-site.xml中配置mapreduce.jobhistory.webapp.address属性:0.0.0.0:19888");
    }

    // 遍历
    for (Element element : preElements) {
      String elementText = element.text();
      if (elementText.isEmpty()) {
        continue;
      }
      Element thirdElement = element.previousElementSibling();
      String logUrl = thirdElement.select("a[href]").attr("href");

      String logStr;
      if (!logUrl.isEmpty()) {
        try {
          logStr =
              Jsoup.connect("http://" + jobHistoryAddress + logUrl)
                  .get()
                  .body()
                  .getElementsByTag("pre")
                  .text();
        } catch (IOException e) {
          log.error(e.getMessage());
          throw new SparkYunException("50010", "获取日志异常", e.getMessage());
        }
        thirdElement = thirdElement.previousElementSibling();
      } else {
        logStr = elementText;
      }
      Element firstElement = thirdElement.previousElementSibling().previousElementSibling();

      resultLog.put(firstElement.text().replace("Log Type:", "").trim(), logStr);
    }

    return resultLog;
  }
}
