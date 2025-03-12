package com.isxcode.star.agent.run;

import com.isxcode.star.api.agent.req.SubmitWorkReq;
import org.apache.spark.launcher.SparkLauncher;

public interface AgentService {

    /**
     * 获取当前代理的类型
     */
    String getAgentType();

    /**
     * 获取spark提交的master
     */
    String getMaster(String sparkHomePath) throws Exception;

    /**
     * 初始化spark提交的sparkLauncher
     */
    SparkLauncher getSparkLauncher(SubmitWorkReq submitWorkReq) throws Exception;

    /**
     * 执行spark作业
     */
    String submitWork(SparkLauncher sparkLauncher) throws Exception;

    /**
     * 获取spark作业的状态
     */
    String getWorkStatus(String appId, String sparkHomePath) throws Exception;

    /**
     * 获取spark作业的Stderr日志
     */
    String getStderrLog(String appId, String sparkHomePath) throws Exception;

    /**
     * 获取spark作业的Stdout日志
     */
    String getStdoutLog(String appId, String sparkHomePath) throws Exception;

    /**
     * 获取自定义spark作业的Stdout日志
     */
    String getCustomWorkStdoutLog(String appId, String sparkHomePath) throws Exception;

    /**
     * 获取spark作业返回的数据的字符串
     */
    String getWorkDataStr(String appId, String sparkHomePath) throws Exception;

    /**
     * 中止spark作业
     */
    void stopWork(String appId, String sparkHomePath, String agentHomePath) throws Exception;
}
