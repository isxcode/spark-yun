package com.isxcode.star.yun.agent.service;

import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import com.isxcode.star.api.pojos.yun.agent.req.SparkSubmit;
import java.io.IOException;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

@Service
public class StandaloneAgentService implements AgentService {
  @Override
  public SparkLauncher genSparkLauncher(
      PluginReq pluginReq, SparkSubmit sparkSubmit, String agentHomePath) {
    return null;
  }

  @Override
  public String executeWork(SparkLauncher sparkLauncher) throws IOException {
    return null;
  }

  @Override
  public String getAppStatus(String appId) {

    String a = "spark-submit --status app-20230610112208-0007";
    return null;
  }

  @Override
  public String getAppLog(String appId) {

    return null;
  }

  @Override
  public String getAppData(String appId) {

    return null;
  }

  @Override
  public void killApp(String appId) throws IOException {}
}
