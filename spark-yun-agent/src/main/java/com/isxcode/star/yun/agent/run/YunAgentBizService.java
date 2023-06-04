package com.isxcode.star.yun.agent.run;

import static com.isxcode.star.yarn.utils.YarnUtils.formatApplicationId;
import static com.isxcode.star.yarn.utils.YarnUtils.initYarnClient;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.base.SparkConstants;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.pojos.yun.agent.req.YagExecuteWorkReq;
import com.isxcode.star.api.pojos.yun.agent.res.YagExecuteWorkRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetDataRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetLogRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetStatusRes;
import com.isxcode.star.yarn.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

/**
 * 代理服务层.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YunAgentBizService {

  public void executeWork() {

    // 压缩插件请求对西那个
    String appArgs = Base64.getEncoder().encodeToString(JSON.toJSONString("").getBytes());

    // 封装spark-launcher
    SparkLauncher sparkLauncher =
      new SparkLauncher()
        .setVerbose(true)
        .setMainClass("org.apache.spark.examples.SparkPi")
        .setMaster("k8s://172.16.215.101:6443")
        .setDeployMode("cluster")
        .setAppName("spark-yun-api")
        .setSparkHome("/home/ispong/spark-3.4.0-bin-hadoop3")
        .setAppResource("local:///opt/spark/examples/jars/spark-examples_2.12-3.4.0.jar")
        .setConf("spark.executor.memory", "1G")
        .setConf("spark.executor.cores", "1")
        .setConf("spark.executor.instances", "1")
        .setConf("spark.kubernetes.container.image", "apache/spark:3.4.0")
        .setConf("spark.kubernetes.driver.volumes.hostPath.local-path.mount.path", "/opt/spark/examples/jars")
        .setConf("spark.kubernetes.driver.volumes.hostPath.local-path.mount.readOnly", "false")
        .setConf("spark.kubernetes.driver.volumes.hostPath.local-path.options.path", "/opt/spark/examples/jars")
        .setConf("spark.kubernetes.authenticate.driver.serviceAccountName", "spark")
        .setConf("spark.kubernetes.namespace", "spark-yun")
        .addAppArgs("1000");

    // 添加依赖包
//    File[] jars = new File(yagExecuteWorkReq.getAgentLibPath()).listFiles();
//    if (jars != null) {
//      for (File jar : jars) {
//        try {
//          sparkLauncher.addJar(jar.toURI().toURL().toString());
//        } catch (MalformedURLException e) {
//          log.error(e.getMessage());
//          throw new SparkYunException("50010", "添加lib中文件异常", e.getMessage());
//        }
//      }
//    }

    // 提交作业到yarn
    SparkAppHandle sparkAppHandle;
    try {
      sparkAppHandle = sparkLauncher.startApplication(
        new SparkAppHandle.Listener() {

          @Override
          public void stateChanged(SparkAppHandle sparkAppHandle) {
            log.info("stateChanged:{}", sparkAppHandle.getState());
          }

          @Override
          public void infoChanged(SparkAppHandle sparkAppHandle) {
            log.info("infoChanged:{}", sparkAppHandle.getState());
          }
        });
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new SparkYunException("50010", "提交作业异常", e.getMessage());
    }

    // 等待作业响应
    long timeoutExpiredMs = System.currentTimeMillis() + SparkConstants.SPARK_SUBMIT_TIMEOUT;
    String applicationId;
    while (!SparkAppHandle.State.RUNNING.equals(sparkAppHandle.getState())) {

      log.info("sparkAppHandle:{},{}", sparkAppHandle.getAppId(), sparkAppHandle.getState());

      long waitMillis = timeoutExpiredMs - System.currentTimeMillis();
      if (waitMillis <= 0) {
        throw new SparkYunException("50010", "提交超时");
      }

      if (SparkAppHandle.State.FAILED.equals(sparkAppHandle.getState())) {
        Optional<Throwable> error = sparkAppHandle.getError();
        throw new SparkYunException("50010", "提交运行失败", error.toString());
      }

      applicationId = sparkAppHandle.getAppId();
      if (applicationId != null) {
        return;
      }
    }

//    throw new SparkYunException("50010", "出现非法异常，联系开发者");
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
}
