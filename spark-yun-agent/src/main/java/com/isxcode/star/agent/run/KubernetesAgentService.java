package com.isxcode.star.agent.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.agent.pojos.req.YagExecuteWorkReq;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class KubernetesAgentService implements AgentService {

	@Override
	public String getMaster(String sparkHomePath) throws IOException {

		String getMasterCmd = "kubectl cluster-info";

		Process process = Runtime.getRuntime().exec(getMasterCmd);
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
				int startIndex = errLog.indexOf("https://") + "https://".length();
				int endIndex = errLog.indexOf("\n", startIndex);
				// k8s命令会返回特殊字符
				return ("k8s://" + errLog.substring(startIndex, endIndex)).replaceAll("0m", "").replaceAll("\u001B", "")
						.replaceAll("\\[", "");
			}
		} catch (InterruptedException e) {
			throw new IsxAppException(e.getMessage());
		}
	}

	@Override
	public SparkLauncher genSparkLauncher(YagExecuteWorkReq yagExecuteWorkReq) throws IOException {

		SparkLauncher sparkLauncher = new SparkLauncher().setVerbose(false)
				.setMainClass(yagExecuteWorkReq.getSparkSubmit().getMainClass()).setDeployMode("cluster")
				.setAppName("zhiqingyun-job").setMaster(getMaster(yagExecuteWorkReq.getSparkHomePath()))
				.setAppResource(
						"local:///opt/spark/examples/jars/" + yagExecuteWorkReq.getSparkSubmit().getAppResource())
				.setSparkHome(yagExecuteWorkReq.getAgentHomePath() + File.separator + "spark-min");

		if (!Strings.isEmpty(yagExecuteWorkReq.getAgentHomePath())) {
			File[] jarFiles = new File(yagExecuteWorkReq.getAgentHomePath() + File.separator + "lib").listFiles();
			if (jarFiles != null) {
				for (int i = 0; i < jarFiles.length; i++) {
					if (!jarFiles[i].getName().contains("hive")) {
						sparkLauncher.addJar("local:///opt/spark/examples/jars/lib/" + jarFiles[i].getName());
						sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath." + i + ".mount.path",
								"/opt/spark/examples/jars/lib/" + jarFiles[i].getName());
						sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath." + i + ".mount.readOnly",
								"false");
						sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath." + i + ".options.path",
								jarFiles[i].getPath());
						sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath." + i + ".mount.path",
								"/opt/spark/examples/jars/lib/" + jarFiles[i].getName());
						sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath." + i + ".mount.readOnly",
								"false");
						sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath." + i + ".options.path",
								jarFiles[i].getPath());
					}
				}
			}
		}

		sparkLauncher.addAppArgs(Base64.getEncoder()
				.encodeToString(yagExecuteWorkReq.getPluginReq() == null
						? yagExecuteWorkReq.getArgsStr().getBytes()
						: JSON.toJSONString(yagExecuteWorkReq.getPluginReq()).getBytes()));

		yagExecuteWorkReq.getSparkSubmit().getConf().forEach(sparkLauncher::setConf);

		sparkLauncher.setConf("spark.kubernetes.container.image", "apache/spark:v3.1.3");
		sparkLauncher.setConf("spark.kubernetes.authenticate.driver.serviceAccountName", "zhiqingyun");
		sparkLauncher.setConf("spark.kubernetes.namespace", "zhiqingyun-space");
		sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.jar.mount.path",
				"/opt/spark/examples/jars/" + yagExecuteWorkReq.getSparkSubmit().getAppResource());
		sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.jar.mount.readOnly", "false");
		sparkLauncher.setConf("spark.kubernetes.driver.volumes.hostPath.jar.options.path",
				yagExecuteWorkReq.getAgentHomePath() + File.separator + "plugins" + File.separator
						+ yagExecuteWorkReq.getSparkSubmit().getAppResource());
		sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.jar.mount.path",
				"/opt/spark/examples/jars/" + yagExecuteWorkReq.getSparkSubmit().getAppResource());
		sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.jar.mount.readOnly", "false");
		sparkLauncher.setConf("spark.kubernetes.executor.volumes.hostPath.jar.options.path",
				yagExecuteWorkReq.getAgentHomePath() + File.separator + "plugins" + File.separator
						+ yagExecuteWorkReq.getSparkSubmit().getAppResource());

		return sparkLauncher;
	}

	@Override
	public String executeWork(SparkLauncher sparkLauncher) throws IOException {

		Process launch = sparkLauncher.launch();
		InputStream inputStream = launch.getErrorStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

		StringBuilder errLog = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			errLog.append(line).append("\n");

			String pattern = "pod name: (\\S+)";
			Pattern regex = Pattern.compile(pattern);
			Matcher matcher = regex.matcher(line);
			if (matcher.find()) {
				return matcher.group().replace("pod name: ", "");
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

		throw new IsxAppException("无法获取podName");
	}

	@Override
	public String getAppStatus(String podName, String sparkHomePath) throws IOException {

		String getStatusCmdFormat = "kubectl get pod %s -n zhiqingyun-space";

		Process process = Runtime.getRuntime().exec(String.format(getStatusCmdFormat, podName));
		InputStream inputStream = process.getInputStream();
		InputStream errStream = process.getErrorStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		BufferedReader errReader = new BufferedReader(new InputStreamReader(errStream, StandardCharsets.UTF_8));
		StringBuilder errLog = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			errLog.append(line).append("\n");
			String pattern = "\\s+\\d/\\d\\s+(\\w+)";
			Pattern regex = Pattern.compile(pattern);
			Matcher matcher = regex.matcher(line);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}

		if (errLog.toString().isEmpty()) {
			while ((line = errReader.readLine()) != null) {
				errLog.append(line).append("\n");
				if (errLog.toString().contains("not found")) {
					return "KILLED";
				}
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
	public String getAppLog(String appId, String sparkHomePath) throws IOException {

		String getLogCmdFormat = "kubectl logs -f %s -n zhiqingyun-space";

		Process process = Runtime.getRuntime().exec(String.format(getLogCmdFormat, appId));
		InputStream inputStream = process.getInputStream();
		InputStream errStream = process.getErrorStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		BufferedReader errReader = new BufferedReader(new InputStreamReader(errStream, StandardCharsets.UTF_8));

		StringBuilder errLog = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			errLog.append(line).append("\n");
		}

		if (Strings.isEmpty(errLog)) {
			while ((line = errReader.readLine()) != null) {
				errLog.append(line).append("\n");
			}
		}

		try {
			int exitCode = process.waitFor();
			if (exitCode == 1) {
				throw new IsxAppException(errLog.toString());
			} else {
				if (errLog.toString().contains("Error")) {
					return errLog.toString();
				}
				Pattern regex = Pattern.compile("LogType:spark-yun\\s*([\\s\\S]*?)\\s*End of LogType:spark-yun");
				Matcher matcher = regex.matcher(errLog);
				String log = errLog.toString();
				if (matcher.find()) {
					log = log.replace(matcher.group(), "");
				}
				return log;
			}
		} catch (InterruptedException e) {
			throw new IsxAppException(e.getMessage());
		}
	}

	@Override
	public String getStdoutLog(String appId, String sparkHomePath) throws IOException {

		String getLogCmdFormat = "kubectl logs -f %s -n zhiqingyun-space";

		Process process = Runtime.getRuntime().exec(String.format(getLogCmdFormat, appId));
		InputStream inputStream = process.getInputStream();
		InputStream errStream = process.getErrorStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		BufferedReader errReader = new BufferedReader(new InputStreamReader(errStream, StandardCharsets.UTF_8));

		StringBuilder errLog = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			errLog.append(line).append("\n");
		}

		if (Strings.isEmpty(errLog)) {
			while ((line = errReader.readLine()) != null) {
				errLog.append(line).append("\n");
			}
		}

		try {
			int exitCode = process.waitFor();
			if (exitCode == 1) {
				throw new IsxAppException(errLog.toString());
			} else {
				if (errLog.toString().contains("Error")) {
					return errLog.toString();
				}
				Pattern regex = Pattern.compile("LogType:spark-yun\\s*([\\s\\S]*?)\\s*End of LogType:spark-yun");
				Matcher matcher = regex.matcher(errLog);
				String log = errLog.toString();
				if (matcher.find()) {
					log = log.replace(matcher.group(), "");
				}
				return log;
			}
		} catch (InterruptedException e) {
			throw new IsxAppException(e.getMessage());
		}
	}

	@Override
	public String getAppData(String appId, String sparkHomePath) throws IOException {

		String getLogCmdFormat = "kubectl logs -f %s -n zhiqingyun-space";

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

		String killAppCmdFormat = "kubectl delete pod %s -n zhiqingyun-space";
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
