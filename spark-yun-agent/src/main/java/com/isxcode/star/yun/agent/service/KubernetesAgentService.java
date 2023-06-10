package com.isxcode.star.yun.agent.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import com.isxcode.star.api.pojos.yun.agent.req.SparkSubmit;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Base64;

@Slf4j
@Service
public class KubernetesAgentService implements AgentService{
  @Override
  public SparkLauncher genSparkLauncher(PluginReq pluginReq, SparkSubmit sparkSubmit, String agentHomePath) {

    SparkLauncher sparkLauncher = new SparkLauncher()
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
      sparkLauncher.addAppArgs(Base64.getEncoder().encodeToString(JSON.toJSONString(pluginReq).getBytes()));
    } else {
      sparkLauncher.addAppArgs(String.valueOf(sparkSubmit.getAppArgs()));
    }

    sparkSubmit.getConf().forEach(sparkLauncher::setConf);

    sparkLauncher.setConf("spark.kubernetes.container.image", "zhiqingyun/spark:latest");
    sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.local-path.mount.path", "/opt/spark/examples/jars/" + sparkSubmit.getAppResource());
    sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.local-path.mount.readOnly", "false");
    sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.local-path.options.path", agentHomePath + File.separator + "plugins" + File.separator + sparkSubmit.getAppResource());
    sparkLauncher.setConf("spark.kubernetes.authenticate.driver.serviceAccountName", "zhiqingyun");
    sparkLauncher.setConf("spark.kubernetes.namespace", "spark-yun");

    return sparkLauncher;
  }

  @Override
  public String executeWork(SparkLauncher sparkLauncher) throws IOException {
    return null;
  }

  @Override
  public String getAppStatus(String appId)  {
    return null;
  }

  @Override
  public String getAppLog(String appId)  {
    return null;
  }

  @Override
  public String getAppData(String appId) {
    return null;
  }

  @Override
  public void killApp(String appId) throws IOException {

  }
}
