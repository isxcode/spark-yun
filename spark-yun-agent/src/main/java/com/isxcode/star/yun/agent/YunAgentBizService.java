package com.isxcode.star.yun.agent;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.agent.AgentType;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.pojos.yun.agent.req.YagExecuteWorkReq;
import com.isxcode.star.api.pojos.yun.agent.res.YagExecuteWorkRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetDataRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetLogRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetStatusRes;
import com.isxcode.star.yun.agent.service.KubernetesAgentService;
import com.isxcode.star.yun.agent.service.StandaloneAgentService;
import com.isxcode.star.yun.agent.service.YarnAgentService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.spark.launcher.SparkLauncher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

/** 代理服务层. */
@Slf4j
@Service
@RequiredArgsConstructor
public class YunAgentBizService {

  private final KubernetesAgentService kubernetesAgentService;

  private final YarnAgentService yarnAgentService;

  private final StandaloneAgentService standaloneAgentService;

  public YagExecuteWorkRes executeWork(YagExecuteWorkReq yagExecuteWorkReq) throws IOException {

    SparkLauncher sparkLauncher;
    String appId;
    switch (yagExecuteWorkReq.getAgentType()) {
      case AgentType.YARN:
        sparkLauncher =
            yarnAgentService.genSparkLauncher(
                yagExecuteWorkReq.getPluginReq(),
                yagExecuteWorkReq.getSparkSubmit(),
                yagExecuteWorkReq.getAgentHomePath());
        appId = yarnAgentService.executeWork(sparkLauncher);
        break;
      case AgentType.K8S:
        sparkLauncher =
            kubernetesAgentService.genSparkLauncher(
                yagExecuteWorkReq.getPluginReq(),
                yagExecuteWorkReq.getSparkSubmit(),
                yagExecuteWorkReq.getAgentHomePath());
        appId = kubernetesAgentService.executeWork(sparkLauncher);
        break;
      case AgentType.StandAlone:
        sparkLauncher =
            standaloneAgentService.genSparkLauncher(
                yagExecuteWorkReq.getPluginReq(),
                yagExecuteWorkReq.getSparkSubmit(),
                yagExecuteWorkReq.getAgentHomePath());
        appId = standaloneAgentService.executeWork(sparkLauncher);
        break;
      default:
        throw new SparkYunException("agent类型不支持");
    }

    return YagExecuteWorkRes.builder().appId(appId).build();
  }

  public YagGetStatusRes getStatus(String appId, String agentType) throws IOException {

    String appStatus;
    switch (agentType) {
      case AgentType.YARN:
        appStatus = yarnAgentService.getAppStatus(appId);
        break;
      case AgentType.K8S:
        appStatus = kubernetesAgentService.getAppStatus(appId);
        break;
      case AgentType.StandAlone:
        appStatus = standaloneAgentService.getAppStatus(appId);
        break;
      default:
        throw new SparkYunException("agent类型不支持");
    }

    return YagGetStatusRes.builder().appStatus(appStatus).build();
  }

  public YagGetLogRes getLog(String appId, String agentType) throws IOException {

    String appLog;
    switch (agentType) {
      case AgentType.YARN:
        appLog = yarnAgentService.getAppLog(appId);
        break;
      case AgentType.K8S:
        appLog = kubernetesAgentService.getAppLog(appId);
        break;
      case AgentType.StandAlone:
        appLog = standaloneAgentService.getAppLog(appId);
        break;
      default:
        throw new SparkYunException("agent类型不支持");
    }

    return YagGetLogRes.builder().log(appLog).build();
  }

  public YagGetDataRes getData(String appId, String agentType) throws IOException {

    String stdoutLog;
    switch (agentType) {
      case AgentType.YARN:
        stdoutLog = yarnAgentService.getAppData(appId);
        break;
      case AgentType.K8S:
        stdoutLog = kubernetesAgentService.getAppData(appId);
        break;
      case AgentType.StandAlone:
        stdoutLog = standaloneAgentService.getAppData(appId);
        break;
      default:
        throw new SparkYunException("agent类型不支持");
    }

    return YagGetDataRes.builder().data(JSON.parseArray(stdoutLog, List.class)).build();
  }

  public void stopJob(String appId, String agentType) throws IOException {

    switch (agentType) {
      case AgentType.YARN:
        yarnAgentService.killApp(appId);
        break;
      case AgentType.K8S:
        kubernetesAgentService.killApp(appId);
        break;
      case AgentType.StandAlone:
        standaloneAgentService.killApp(appId);
        break;
      default:
        throw new SparkYunException("agent类型不支持");
    }
  }
}
