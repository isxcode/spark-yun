package com.isxcode.spark.agent.run.spark;

import com.isxcode.spark.api.agent.req.spark.SubmitWorkReq;
import com.isxcode.spark.api.agent.res.spark.GetWorkInfoRes;
import org.apache.spark.launcher.SparkLauncher;

public interface SparkAgentService {

    String getAgentType();

    String getMaster(String sparkHomePath) throws Exception;

    SparkLauncher getSparkLauncher(SubmitWorkReq submitWorkReq) throws Exception;

    String submitWork(SparkLauncher sparkLauncher) throws Exception;

    GetWorkInfoRes getWorkInfo(String appId, String sparkHomePath) throws Exception;

    String getStderrLog(String appId, String sparkHomePath) throws Exception;

    String getStdoutLog(String appId, String sparkHomePath) throws Exception;

    String getCustomJarStdoutLog(String appId, String sparkHomePath) throws Exception;

    String getWorkDataStr(String appId, String sparkHomePath) throws Exception;

    void stopWork(String appId, String sparkHomePath, String agentHomePath) throws Exception;
}
