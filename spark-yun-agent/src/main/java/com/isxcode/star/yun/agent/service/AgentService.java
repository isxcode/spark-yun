package com.isxcode.star.yun.agent.service;

import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import com.isxcode.star.api.pojos.yun.agent.req.SparkSubmit;
import java.io.IOException;
import org.apache.spark.launcher.SparkLauncher;

public interface AgentService {

  String getMaster() throws IOException;

  /**
   * @param pluginReq 插件请求体
   * @param sparkSubmit spark作业提交配置
   */
  SparkLauncher genSparkLauncher(PluginReq pluginReq, SparkSubmit sparkSubmit, String agentHomePath)
      throws IOException;

  /** @param sparkLauncher spark启动器 */
  String executeWork(SparkLauncher sparkLauncher) throws IOException;

  String getAppStatus(String appId) throws IOException;

  String getAppLog(String appId) throws IOException;

  String getAppData(String appId) throws IOException;

  void killApp(String appId) throws IOException;
}
