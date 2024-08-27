package com.isxcode.star.agent.run;

import com.isxcode.star.api.agent.pojos.req.DeployContainerReq;
import com.isxcode.star.api.agent.pojos.req.YagExecuteWorkReq;
import org.apache.spark.launcher.SparkLauncher;

import java.io.IOException;

public interface AgentService {

    /**
     * 获取spark提交的master信息.
     */
    String getMaster(String sparkHomePath) throws IOException;

    /**
     * 初始化spark提交的SparkLauncher.
     */
    SparkLauncher genSparkLauncher(YagExecuteWorkReq yagExecuteWorkReq) throws IOException;

    /**
     * 执行spark作业.
     */
    String executeWork(SparkLauncher sparkLauncher) throws IOException;

    /**
     * 获取spark作业的状态.
     */
    String getAppStatus(String appId, String sparkHomePath) throws IOException;

    /**
     * 获取spark作业的运行日志.
     */
    String getAppLog(String appId, String sparkHomePath) throws IOException;

    /**
     * 获取spark作业的输出日志.
     */
    String getStdoutLog(String appId, String sparkHomePath) throws IOException;

    /**
     * 获取spark作业返回的数据.
     */
    String getAppData(String appId, String sparkHomePath) throws IOException;

    /**
     * 中止spark作业.
     */
    void killApp(String appId, String sparkHomePath, String agentHomePath) throws IOException;

    /**
     * 获取当前代理的类型.
     */
    String getAgentName();

    /**
     * 获取容器话部署的SparkLauncher.
     */
    SparkLauncher genSparkLauncher(DeployContainerReq deployContainerReq) throws IOException;
}
