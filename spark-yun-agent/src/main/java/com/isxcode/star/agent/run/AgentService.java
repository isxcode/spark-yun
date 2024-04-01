package com.isxcode.star.agent.run;

import com.isxcode.star.api.agent.pojos.req.YagExecuteWorkReq;
import org.apache.spark.launcher.SparkLauncher;

import java.io.IOException;

public interface AgentService {

	String getMaster(String sparkHomePath) throws IOException;

	/**
	 * @param yagExecuteWorkReq
	 *            请求体
	 */
	SparkLauncher genSparkLauncher(YagExecuteWorkReq yagExecuteWorkReq) throws IOException;

	String executeWork(SparkLauncher sparkLauncher) throws IOException;

	String getAppStatus(String appId, String sparkHomePath) throws IOException;

	String getAppLog(String appId, String sparkHomePath) throws IOException;

	String getStdoutLog(String appId, String sparkHomePath) throws IOException;

	String getAppData(String appId, String sparkHomePath) throws IOException;

	void killApp(String appId, String sparkHomePath) throws IOException;
}
