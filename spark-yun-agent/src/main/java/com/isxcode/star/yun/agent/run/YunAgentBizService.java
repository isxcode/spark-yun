package com.isxcode.star.yun.agent.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.exceptions.SparkYunException;
//import com.isxcode.star.api.pojos.yun.agent.req.SparkLauncher;
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
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import static com.isxcode.star.yarn.utils.YarnUtils.formatApplicationId;
import static com.isxcode.star.yarn.utils.YarnUtils.initYarnClient;

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

    sparkLauncher.launch("123");

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


}
