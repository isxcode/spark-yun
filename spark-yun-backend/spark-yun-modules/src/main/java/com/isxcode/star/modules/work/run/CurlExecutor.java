package com.isxcode.star.modules.work.run;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.exceptions.WorkRunException;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class CurlExecutor extends WorkExecutor {

	public CurlExecutor(WorkInstanceRepository workInstanceRepository,
			WorkflowInstanceRepository workflowInstanceRepository) {

		super(workInstanceRepository, workflowInstanceRepository);
	}

	public void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

		// 获取日志构造器
		StringBuilder logBuilder = workRunContext.getLogBuilder();

		// 判断执行脚本是否为空
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测脚本内容 \n");
		if (Strings.isEmpty(workRunContext.getScript())) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测脚本失败 : CURL内容为空不能执行  \n");
		}

		// 脚本检查通过
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
		workInstance = updateInstance(workInstance, logBuilder);

		// curl请求结果, 成功日志, 失败日志
		boolean result;
		List<String> resultList = new ArrayList<>();
		List<String> errorList = new ArrayList<>();
		try {

			ProcessBuilder processBuilder = new ProcessBuilder(generateCommandList(workRunContext.getScript()));
			Process process = processBuilder.start();

			BufferedReader resultReader = new BufferedReader(
					new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
			BufferedReader errorReader = new BufferedReader(
					new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

			String line;
			while ((line = resultReader.readLine()) != null) {
				resultList.add(line);
			}
			while ((line = errorReader.readLine()) != null) {
				errorList.add(line);
			}
			process.waitFor();

			// 判断请求状态, 保存运行结果
			if (process.exitValue() == 0 || (process.exitValue() == 3 && CollectionUtil.isNotEmpty(resultList)
					&& errorList.contains("curl: (3) URL rejected: Malformed input to a URL function"))) {
				result = true;
				workInstance.setResultData(JSON.toJSONString(resultList));
			} else {
				result = false;
				workInstance.setResultData(JSON.toJSONString(errorList));
			}
			logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("保存日志成功 \n");

			resultReader.close();
			process.destroy();
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业执行异常 : " + e.getMessage() + "\n");
		}

		if (!result) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "执行失败 \n");
		}

		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("执行成功 \n");
		updateInstance(workInstance, logBuilder);
	}

	private List<String> generateCommandList(final String command) {

		int index = 0;
		boolean isApos = false;
		boolean isQuote = false;

		List<String> commands = new ArrayList<>();
		StringBuilder buffer = new StringBuilder(command.length());

		while (index < command.length()) {
			final char c = command.charAt(index);

			switch (c) {
				case ' ' :
					if (!isQuote && !isApos) {
						String arg = buffer.toString();
						buffer = new StringBuilder(command.length() - index);
						if (!arg.isEmpty()) {
							commands.add(arg);
						}
					} else {
						buffer.append(c);
					}
					break;
				case '\'' :
					if (!isQuote) {
						isApos = !isApos;
					} else {
						buffer.append(c);
					}
					break;
				case '"' :
					if (!isApos) {
						isQuote = !isQuote;
					} else {
						buffer.append(c);
					}
					break;
				default :
					buffer.append(c);
			}
			index++;
		}

		if (buffer.length() > 0) {
			String arg = buffer.toString();
			commands.add(arg);
		}
		return commands;
	}

	@Override
	protected void abort(WorkInstanceEntity workInstance) {

		Thread thread = WORK_THREAD.get(workInstance.getId());
		thread.interrupt();
	}
}
