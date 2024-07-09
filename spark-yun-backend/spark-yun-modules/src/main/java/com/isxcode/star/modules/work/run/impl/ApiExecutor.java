package com.isxcode.star.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.api.constants.ApiType;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.api.work.exceptions.WorkRunException;
import com.isxcode.star.api.work.pojos.dto.ApiWorkConfig;
import com.isxcode.star.common.utils.http.HttpUtils;
import com.isxcode.star.modules.alarm.service.AlarmService;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.work.run.WorkExecutor;
import com.isxcode.star.modules.work.run.WorkRunContext;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ApiExecutor extends WorkExecutor {

	public ApiExecutor(WorkInstanceRepository workInstanceRepository,
			WorkflowInstanceRepository workflowInstanceRepository, AlarmService alarmService) {

		super(workInstanceRepository, workflowInstanceRepository, alarmService);
	}

	@Override
	public String getWorkType() {
		return WorkType.API;
	}

	@Override
	protected void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

		// 将线程存到Map
		WORK_THREAD.put(workInstance.getId(), Thread.currentThread());

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

			// 转换一下结构
			Map<String, String> requestParam = new HashMap<>();
			Map<String, String> requestHeader = new HashMap<>();

			apiWorkConfig.getRequestParam().forEach(e -> requestParam.put(e.getLabel(), e.getValue()));
			apiWorkConfig.getRequestHeader().forEach(e -> requestHeader.put(e.getLabel(), e.getValue()));

			if (ApiType.GET.equals(apiWorkConfig.getRequestType())) {
				response = HttpUtils.doGet(apiWorkConfig.getRequestUrl(), requestParam, requestHeader, Object.class);
			}
			if (ApiType.POST.equals(apiWorkConfig.getRequestType())) {
				response = HttpUtils.doPost(apiWorkConfig.getRequestUrl(), requestHeader,
						JSON.parseObject(apiWorkConfig.getRequestBody()), Object.class);
			}

			log.debug("获取远程返回数据:{}", response);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业执行异常 : " + e.getMessage() + "\n");
		}

		// 保存运行日志
		workInstance.setResultData(JSON.toJSONString(response));
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("请求成功, 返回结果: \n")
				.append(JSON.toJSONString(response, true)).append(" \n");
		updateInstance(workInstance, logBuilder);
	}

	@Override
	protected void abort(WorkInstanceEntity workInstance) {

		Thread thread = WORK_THREAD.get(workInstance.getId());
		thread.interrupt();
	}
}
