package com.isxcode.star.agent.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.pojos.agent.req.ExecuteReq;
import com.isxcode.star.api.pojos.agent.res.ExecuteRes;
import com.isxcode.star.api.pojos.agent.res.GetDataRes;
import com.isxcode.star.api.pojos.agent.res.GetLogRes;
import com.isxcode.star.api.pojos.agent.res.GetStatusRes;
import com.isxcode.star.yarn.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentBizService {

  public ExecuteRes execute(ExecuteReq executeReq) {

    // 获取hadoop环境变量
    String hadoopConfDir = System.getenv("HADOOP_HOME");
    Configuration hadoopConf = new Configuration(false);
    Path path = Paths.get(hadoopConfDir + File.separator + "etc" + File.separator + "hadoop" + File.separator + "yarn-site.xml");
    try {
      hadoopConf.addResource(Files.newInputStream(path));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    YarnConfiguration yarnConf = new YarnConfiguration(hadoopConf);

    // 获取star目录位置
    String starHomePath = "~/spark-yun-agent";

    // 封装launcher
    SparkLauncher sparkLauncher = new SparkLauncher()
      .setSparkHome("")
      .setMaster("yarn")
      .setDeployMode("cluster")
      .setAppName(executeReq.getAppName())
      .setVerbose(true)
      .setMainClass(executeReq.getMainClass())
      .setAppResource(starHomePath + File.separator + "plugins" + File.separator + executeReq.getAppResourceName() + ".jar")
      .addAppArgs(Base64.getEncoder().encodeToString(JSON.toJSONString(executeReq.getPluginReq()).getBytes()));

    // 添加依赖包
    File[] jars = new File(starHomePath + File.separator + "lib" + File.separator).listFiles();
    if (jars != null) {
      for (File jar : jars) {
        try {
          sparkLauncher.addJar(jar.toURI().toURL().toString());
        } catch (MalformedURLException e) {
          throw new RuntimeException(e);
        }
      }
    }

    // 提交作业到yarn
    SparkAppHandle sparkAppHandle;
    try {
      sparkAppHandle = sparkLauncher.startApplication(new SparkAppHandle.Listener() {

        @Override
        public void stateChanged(SparkAppHandle sparkAppHandle) {
          // do nothing
        }

        @Override
        public void infoChanged(SparkAppHandle sparkAppHandle) {
          // do nothing
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // 默认10秒超时
    long timeoutExpiredMs = System.currentTimeMillis() + 10000;
    String applicationId;
    while (!SparkAppHandle.State.RUNNING.equals(sparkAppHandle.getState())) {

      long waitMillis = timeoutExpiredMs - System.currentTimeMillis();
      if (waitMillis <= 0) {
        return new ExecuteRes("提交失败", "提交超时", null);
      }

      if (SparkAppHandle.State.FAILED.equals(sparkAppHandle.getState())) {
                Optional<Throwable> error = sparkAppHandle.getError();
        return new ExecuteRes("提交失败", error.get().getMessage(), null);
      }

      applicationId = sparkAppHandle.getAppId();
      if (applicationId != null) {
        return new ExecuteRes("提交成功", "提交日志成功", applicationId);
      }
    }

    return new ExecuteRes("提交异常", "未知异常", null);
  }

  public GetStatusRes getStatus(String applicationId) {

    YarnClient yarnClient = initYarnClient();

    ApplicationReport applicationReport;
    try {
      applicationReport = yarnClient.getApplicationReport(formatApplicationId(applicationId));
    } catch (YarnException | IOException e) {
      throw new RuntimeException(e);
    }

    return new GetStatusRes(String.valueOf(applicationReport.getYarnApplicationState()), String.valueOf(applicationReport.getFinalApplicationStatus()));
  }

  public GetLogRes getLog(String applicationId) {

    Map<String, String> map = LogUtils.parseYarnLog(applicationId);
    String stdErrLog = map.get("stderr");

    if (Strings.isEmpty(stdErrLog)) {
      return new GetLogRes("查询失败", "作业日志暂未生成");
    }

    return new GetLogRes("查询成功", stdErrLog);
  }

  public GetDataRes getData(String applicationId) {

    Map<String, String> map = LogUtils.parseYarnLog(applicationId);
    String stdoutLog = map.get("stdout");

    if (Strings.isEmpty(stdoutLog)) {
      return new GetDataRes(null, "查询异常", "日志为空");
    }

    List<List> lists = JSON.parseArray(stdoutLog, List.class);

    return new GetDataRes(lists, "查询成功", "查询日志成功");
  }

  public void stopJob(String applicationId) {

    YarnClient yarnClient = initYarnClient();
    try {
      yarnClient.killApplication(formatApplicationId(applicationId));
    } catch (YarnException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public YarnClient initYarnClient() {

    // 获取hadoop的配置文件目录
    String hadoopConfDir = System.getenv("HADOOP_HOME");

    // 读取配置yarn-site.yml文件
    Configuration yarnConf = new Configuration(false);
    Path path = Paths.get(hadoopConfDir + File.separator + "etc" + File.separator + "hadoop" + File.separator + "yarn-site.xml");
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
