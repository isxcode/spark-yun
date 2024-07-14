package com.isxcode.star.modules.work.run;

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

		List<String> resultList = new ArrayList<>();
		try {

			ProcessBuilder hiveProcessBuilder = new ProcessBuilder(generateCommandList(workRunContext.getScript()));
			Process hiveProcess = hiveProcessBuilder.start();

			BufferedReader result = new BufferedReader(
					new InputStreamReader(hiveProcess.getInputStream(), StandardCharsets.UTF_8));
			BufferedReader error = new BufferedReader(
					new InputStreamReader(hiveProcess.getErrorStream(), StandardCharsets.UTF_8));

			String line;
			while ((line = result.readLine()) != null) {
				resultList.add(line);
			}
			while ((line = error.readLine()) != null) {

			}
			hiveProcess.waitFor();
			if (hiveProcess.exitValue() != 0) {

			}
			result.close();
			hiveProcess.destroy();
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业执行异常 : " + e.getMessage() + "\n");
		}

		// 保存运行日志
		workInstance.setResultData(JSON.toJSONString(resultList));
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("请求成功, 返回结果: \n")
				.append(JSON.toJSONString(resultList, true)).append(" \n");
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
