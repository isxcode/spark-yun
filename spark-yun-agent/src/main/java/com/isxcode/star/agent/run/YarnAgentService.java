package com.isxcode.star.agent.run;

import com.alibaba.fastjson2.JSON;
import com.isxcode.star.agent.properties.SparkYunAgentProperties;
import com.isxcode.star.api.agent.pojos.req.DeployContainerReq;
import com.isxcode.star.api.agent.pojos.req.YagExecuteWorkReq;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class YarnAgentService implements AgentService {

	private final SparkYunAgentProperties sparkYunAgentProperties;

	@Override
	public String getMaster(String sparkHomePath) {
		return "yarn";
	}

	@Override
	public SparkLauncher genSparkLauncher(YagExecuteWorkReq yagExecuteWorkReq) {

		SparkLauncher sparkLauncher;
		if (WorkType.SPARK_JAR.equals(yagExecuteWorkReq.getWorkType())) {
			// 如果是自定义作业
			sparkLauncher = new SparkLauncher().setVerbose(false)
					.setMainClass(yagExecuteWorkReq.getSparkSubmit().getMainClass()).setDeployMode("cluster")
					.setAppName(yagExecuteWorkReq.getSparkSubmit().getAppName())
					.setMaster(getMaster(yagExecuteWorkReq.getSparkHomePath()))
					.setAppResource(yagExecuteWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
							+ yagExecuteWorkReq.getSparkSubmit().getAppResource())
					.setSparkHome(yagExecuteWorkReq.getAgentHomePath() + File.separator + "spark-min");
		} else {
			sparkLauncher = new SparkLauncher().setVerbose(false)
					.setMainClass(yagExecuteWorkReq.getSparkSubmit().getMainClass()).setDeployMode("cluster")
					.setAppName("zhiqingyun-job").setMaster(getMaster(yagExecuteWorkReq.getSparkHomePath()))
					.setAppResource(yagExecuteWorkReq.getAgentHomePath() + File.separator + "plugins" + File.separator
							+ yagExecuteWorkReq.getSparkSubmit().getAppResource())
					.setSparkHome(yagExecuteWorkReq.getAgentHomePath() + File.separator + "spark-min");
		}

		if (!Strings.isEmpty(yagExecuteWorkReq.getAgentHomePath())) {
			File[] jarFiles = new File(yagExecuteWorkReq.getAgentHomePath() + File.separator + "lib").listFiles();
			if (jarFiles != null) {
				for (File jar : jarFiles) {
					try {
						// 使用本地hive驱动
						if (!jar.getName().contains("hive")) {
							sparkLauncher.addJar(jar.toURI().toURL().toString());
						}
					} catch (MalformedURLException e) {
						log.error(e.getMessage());
						throw new IsxAppException("50010", "添加lib中文件异常", e.getMessage());
					}
				}
			}
		}

		// 添加额外依赖
		if (yagExecuteWorkReq.getLibConfig() != null) {
			yagExecuteWorkReq.getLibConfig().forEach(e -> {
				String libPath = yagExecuteWorkReq.getAgentHomePath() + File.separator + "file" + File.separator + e
						+ ".jar";
				sparkLauncher.addJar(libPath);
			});
		}

		// 添加自定义函数
		if (yagExecuteWorkReq.getFuncConfig() != null) {
			yagExecuteWorkReq.getFuncConfig().forEach(e -> {
				String libPath = yagExecuteWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
						+ e.getFileId() + ".jar";
				sparkLauncher.addJar(libPath);
			});
		}

		if (WorkType.SPARK_JAR.equals(yagExecuteWorkReq.getWorkType())) {
			sparkLauncher.addAppArgs(yagExecuteWorkReq.getArgs());
		} else {
			sparkLauncher.addAppArgs(Base64.getEncoder()
					.encodeToString(yagExecuteWorkReq.getPluginReq() == null
							? yagExecuteWorkReq.getArgsStr().getBytes()
							: JSON.toJSONString(yagExecuteWorkReq.getPluginReq()).getBytes()));
		}

		// 调整spark.yarn.submit.waitAppCompletion，减少资源消耗
		sparkLauncher.setConf("spark.yarn.submit.waitAppCompletion", "false");
		yagExecuteWorkReq.getSparkSubmit().getConf().forEach(sparkLauncher::setConf);

		return sparkLauncher;
	}

	public SparkLauncher genSparkLauncher(DeployContainerReq deployContainerReq) {

		SparkLauncher sparkLauncher = new SparkLauncher().setVerbose(false)
				.setMainClass(deployContainerReq.getSparkSubmit().getMainClass()).setDeployMode("cluster")
				.setAppName("zhiqingyun-job").setMaster(getMaster(deployContainerReq.getSparkHomePath()))
				.setAppResource(deployContainerReq.getAgentHomePath() + File.separator + "plugins" + File.separator
						+ deployContainerReq.getSparkSubmit().getAppResource())
				.setSparkHome(deployContainerReq.getAgentHomePath() + File.separator + "spark-min");

		if (!Strings.isEmpty(deployContainerReq.getAgentHomePath())) {
			File[] jarFiles = new File(deployContainerReq.getAgentHomePath() + File.separator + "lib").listFiles();
			if (jarFiles != null) {
				for (File jar : jarFiles) {
					try {
						// 使用本地hive驱动
						if (!jar.getName().contains("hive")) {
							sparkLauncher.addJar(jar.toURI().toURL().toString());
						}
					} catch (MalformedURLException e) {
						log.error(e.getMessage());
						throw new IsxAppException("50010", "添加lib中文件异常", e.getMessage());
					}
				}
			}
		}

		sparkLauncher.addAppArgs(Base64.getEncoder()
				.encodeToString(deployContainerReq.getPluginReq() == null
						? deployContainerReq.getArgs().getBytes()
						: JSON.toJSONString(deployContainerReq.getPluginReq()).getBytes()));

		deployContainerReq.getSparkSubmit().getConf().forEach(sparkLauncher::setConf);

		return sparkLauncher;
	}

	@Override
	public String executeWork(SparkLauncher sparkLauncher) throws IOException {

		Process launch = sparkLauncher.launch();
		InputStream inputStream = launch.getErrorStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

		long timeoutExpiredMs = System.currentTimeMillis() + sparkYunAgentProperties.getSubmitTimeout() * 1000;

		StringBuilder errLog = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			errLog.append(line).append("\n");

			long waitMillis = timeoutExpiredMs - System.currentTimeMillis();
			if (waitMillis <= 0) {
				launch.destroy();
				throw new IsxAppException(errLog.toString());
			}

			String pattern = "Submitted application application_\\d+_\\d+";
			Pattern regex = Pattern.compile(pattern);
			Matcher matcher = regex.matcher(line);
			if (matcher.find()) {
				return matcher.group().replace("Submitted application ", "");
			}
		}

		try {
			int exitCode = launch.waitFor();
			if (exitCode == 1) {
				throw new IsxAppException(errLog.toString());
			}
		} catch (InterruptedException e) {
			throw new IsxAppException(e.getMessage());
		} finally {
			launch.destroy();
		}

		throw new IsxAppException("无法获取applicationId");
	}

	@Override
	public String getAppStatus(String appId, String sparkHomePath) throws IOException {

		String getStatusCmdFormat = "yarn application -status %s";

		Process process = Runtime.getRuntime().exec(String.format(getStatusCmdFormat, appId));

		InputStream inputStream = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

		StringBuilder errLog = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			errLog.append(line).append("\n");

			String pattern = "Final-State : (\\w+)";
			Pattern regex = Pattern.compile(pattern);
			Matcher matcher = regex.matcher(line);
			if (matcher.find()) {
				String status = matcher.group(1);
				if ("UNDEFINED".equals(status)) {
					status = "RUNNING";
				}
				return status;
			}
		}

		try {
			int exitCode = process.waitFor();
			if (exitCode == 1) {
				throw new IsxAppException(errLog.toString());
			}
		} catch (InterruptedException e) {
			throw new IsxAppException(e.getMessage());
		}

		throw new IsxAppException("获取状态异常");
	}

	@Override
	public String getStdoutLog(String appId, String sparkHomePath) throws IOException {

		String getLogCmdFormat = "yarn logs -applicationId %s";
		Process process = Runtime.getRuntime().exec(String.format(getLogCmdFormat, appId));

		InputStream inputStream = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

		StringBuilder errLog = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			errLog.append(line).append("\n");
		}

		try {
			int exitCode = process.waitFor();
			if (exitCode == 1) {
				throw new IsxAppException(errLog.toString());
			} else {
				Pattern regex = Pattern.compile("LogType:stdout-start\\s*([\\s\\S]*?)\\s*End of LogType:stdout");
				Matcher matcher = regex.matcher(errLog);
				String log = "";
				while (matcher.find()) {
					String tmpLog = matcher.group();
					if (tmpLog.contains("ERROR")) {
						log = tmpLog;
						break;
					}
					if (tmpLog.length() > log.length()) {
						log = tmpLog;
					}
				}
				return log;
			}
		} catch (InterruptedException e) {
			throw new IsxAppException(e.getMessage());
		}
	}

	@Override
	public String getAppLog(String appId, String sparkHomePath) throws IOException {

		String getLogCmdFormat = "yarn logs -applicationId %s";
		Process process = Runtime.getRuntime().exec(String.format(getLogCmdFormat, appId));

		InputStream inputStream = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

		StringBuilder errLog = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			errLog.append(line).append("\n");
		}

		try {
			int exitCode = process.waitFor();
			if (exitCode == 1) {
				throw new IsxAppException(errLog.toString());
			} else {
				Pattern regex = Pattern.compile("LogType:stderr\\s*([\\s\\S]*?)\\s*End of LogType:stderr");
				Matcher matcher = regex.matcher(errLog);
				String log = "";
				while (matcher.find()) {
					String tmpLog = matcher.group();
					if (tmpLog.contains("ERROR")) {
						log = tmpLog;
						break;
					}
					if (tmpLog.length() > log.length()) {
						log = tmpLog;
					}
				}
				return log;
			}
		} catch (InterruptedException e) {
			throw new IsxAppException(e.getMessage());
		}
	}

	@Override
	public String getAppData(String appId, String sparkHomePath) throws IOException {

		String getLogCmdFormat = "yarn logs -applicationId %s";

		Process process = Runtime.getRuntime().exec(String.format(getLogCmdFormat, appId));

		InputStream inputStream = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

		StringBuilder errLog = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			errLog.append(line).append("\n");
		}

		try {
			int exitCode = process.waitFor();
			if (exitCode == 1) {
				throw new IsxAppException(errLog.toString());
			} else {
				Pattern regex = Pattern.compile("LogType:spark-yun\\s*([\\s\\S]*?)\\s*End of LogType:spark-yun");
				Matcher matcher = regex.matcher(errLog);
				String log = "";
				while (matcher.find() && Strings.isEmpty(log)) {
					log = matcher.group().replace("LogType:spark-yun\n", "").replace("\nEnd of LogType:spark-yun", "");
				}
				return log;
			}
		} catch (InterruptedException e) {
			throw new IsxAppException(e.getMessage());
		}
	}

	@Override
	public void killApp(String appId, String sparkHomePath) throws IOException {

		String killAppCmdFormat = "yarn application -kill %s";
		Process process = Runtime.getRuntime().exec(String.format(killAppCmdFormat, appId));

		InputStream inputStream = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

		StringBuilder errLog = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			errLog.append(line).append("\n");
		}

		try {
			int exitCode = process.waitFor();
			if (exitCode == 1) {
				throw new IsxAppException(errLog.toString());
			}
		} catch (InterruptedException e) {
			throw new IsxAppException(e.getMessage());
		}
	}
}
