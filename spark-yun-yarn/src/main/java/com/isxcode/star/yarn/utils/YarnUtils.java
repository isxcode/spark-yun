package com.isxcode.star.yarn.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class YarnUtils {

  public static YarnClient initYarnClient() {

    // 获取hadoop的配置文件目录
    String hadoopConfDir = System.getenv("HADOOP_HOME");

    // 读取配置yarn-site.yml文件
    Configuration yarnConf = new Configuration(false);
    Path path =
      Paths.get(
        hadoopConfDir
          + File.separator
          + "etc"
          + File.separator
          + "hadoop"
          + File.separator
          + "yarn-site.xml");
    try {
      yarnConf.addResource(Files.newInputStream(path));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    YarnConfiguration yarnConfig = new YarnConfiguration(yarnConf);

    // 获取yarn客户端
    YarnClient yarnClient = YarnClient.createYarnClient();
    yarnClient.init(yarnConfig);
    yarnClient.start();

    return yarnClient;
  }


  public static ApplicationId formatApplicationId(String appIdStr) {

    String applicationPrefix = "application_";
    try {
      int pos1 = applicationPrefix.length() - 1;
      int pos2 = appIdStr.indexOf('_', pos1 + 1);
      long rmId = Long.parseLong(appIdStr.substring(pos1 + 1, pos2));
      int appId = Integer.parseInt(appIdStr.substring(pos2 + 1));
      return ApplicationId.newInstance(rmId, appId);
    } catch (NumberFormatException n) {
      throw new RuntimeException("ApplicationId异常");
    }
  }
}
