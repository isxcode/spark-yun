package com.isxcode.star.modules.work.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.api.constants.ApiType;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.exceptions.WorkRunException;
import com.isxcode.star.api.work.pojos.dto.ApiWorkConfig;
import com.isxcode.star.common.utils.http.HttpUtils;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ApiExecutor extends WorkExecutor {

	public ApiExecutor(WorkInstanceRepository workInstanceRepository,
			WorkflowInstanceRepository workflowInstanceRepository) {

		super(workInstanceRepository, workflowInstanceRepository);
	}

	@Override
	protected void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

		// 获取日志构造器
		StringBuilder logBuilder = workRunContext.getLogBuilder();

		// 判断作业配置是否为空
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测作业配置 \n");
		ApiWorkConfig apiWorkConfig = workRunContext.getApiWorkConfig();
		if (apiWorkConfig == null) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测作业失败 : 接口调用作业配置为空不能执行  \n");
		}

		// 检测作业请求方式是否符合规范
		if (StringUtils.isBlank(apiWorkConfig.getRequestType())) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测作业失败 : 接口调用作业请求方式为空不能执行  \n");
		}

		if (!ApiType.GET.equals(apiWorkConfig.getRequestType())
				&& !ApiType.POST.equals(apiWorkConfig.getRequestType())) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测作业失败 : 接口调用作业请求方式仅支持POST/GET  \n");
		}

		// 检测接口url是否为空
		if (StringUtils.isBlank(apiWorkConfig.getRequestUrl())) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测作业失败 : 接口调用作业请求url为空不能执行  \n");
		}

		// 检查通过
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
		workInstance = updateInstance(workInstance, logBuilder);

		Object response = null;
		try {
			if (ApiType.GET.equals(apiWorkConfig.getRequestType())) {
				response = HttpUtils.doGet(apiWorkConfig.getRequestUrl(), apiWorkConfig.getRequestParam(),
						apiWorkConfig.getRequestHeader(), Object.class);
			}
			if (ApiType.POST.equals(apiWorkConfig.getRequestType())) {
				response = HttpUtils.doPost(apiWorkConfig.getRequestUrl(), apiWorkConfig.getRequestHeader(),
						apiWorkConfig.getRequestBody(), Object.class);
			}
		} catch (Exception e) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "请求执行异常 : " + e.getMessage() + "\n");
		}
		// 保存运行日志
		workInstance.setResultData(JSON.toJSONString(response));
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("返回结果： \n").append(response)
				.append(" \n");
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("保存日志成功 \n");
		updateInstance(workInstance, logBuilder);
	}

	@Override
	protected void abort(WorkInstanceEntity workInstance) {

		Thread thread = WORK_THREAD.get(workInstance.getId());
		thread.interrupt();
	}
}
