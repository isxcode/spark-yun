// package com.isxcode.spark.modules.work.run.impl;
//
// import com.alibaba.fastjson.JSON;
// import com.isxcode.spark.api.api.constants.ApiType;
// import com.isxcode.spark.api.work.constants.WorkLog;
// import com.isxcode.spark.api.work.constants.WorkType;
// import com.isxcode.spark.api.instance.constants.InstanceStatus;
// import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
// import com.isxcode.spark.api.work.dto.ApiWorkConfig;
// import com.isxcode.spark.api.work.dto.ApiWorkValueDto;
// import com.isxcode.spark.common.utils.http.HttpUtils;
// import com.isxcode.spark.modules.alarm.service.AlarmService;
// import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
// import com.isxcode.spark.modules.work.entity.WorkEventEntity;
// import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
// import com.isxcode.spark.modules.work.repository.WorkEventRepository;
// import com.isxcode.spark.modules.work.run.WorkExecutor;
// import com.isxcode.spark.modules.work.run.WorkRunContext;
// import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
// import com.isxcode.spark.modules.work.repository.VipWorkVersionRepository;
// import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
// import com.isxcode.spark.modules.work.repository.WorkRepository;
// import com.isxcode.spark.common.locker.Locker;
// import com.isxcode.spark.modules.work.sql.SqlFunctionService;
// import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
// import org.quartz.Scheduler;
// import lombok.Data;
// import lombok.Builder;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang3.StringUtils;
// import org.springframework.stereotype.Service;
//
// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.Map;
//
// @Service
// @Slf4j
// public class ApiExecutor extends WorkExecutor {
//
// private final WorkEventRepository workEventRepository;
//
// private final Scheduler scheduler;
//
// private final WorkRunJobFactory workRunJobFactory;
//
// private final VipWorkVersionRepository vipWorkVersionRepository;
//
// private final WorkConfigRepository workConfigRepository;
//
// private final WorkRepository workRepository;
//
// private final Locker locker;
//
// public ApiExecutor(WorkInstanceRepository workInstanceRepository,
// WorkflowInstanceRepository workflowInstanceRepository, AlarmService alarmService,
// SqlFunctionService sqlFunctionService, WorkEventRepository workEventRepository, Scheduler
// scheduler,
// WorkRunJobFactory workRunJobFactory, VipWorkVersionRepository vipWorkVersionRepository,
// WorkConfigRepository workConfigRepository, WorkRepository workRepository, Locker locker) {
//
// super(alarmService, scheduler, locker, workRepository, workInstanceRepository,
// workflowInstanceRepository,
// workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository,
// vipWorkVersionRepository);
// this.workEventRepository = workEventRepository;
// this.scheduler = scheduler;
// this.workRunJobFactory = workRunJobFactory;
// this.vipWorkVersionRepository = vipWorkVersionRepository;
// this.workConfigRepository = workConfigRepository;
// this.workRepository = workRepository;
// this.locker = locker;
// }
//
// @Override
// public String getWorkType() {
// return WorkType.API;
// }
//
// @Override
// protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
// WorkEventEntity workEvent) {
//
// // 获取作业运行上下文
// ApiExecutorContext workEventBody = JSON.parseObject(workEvent.getEventContext(),
// ApiExecutorContext.class);
// if (workEventBody == null) {
// workEventBody = new ApiExecutorContext();
// workEventBody.setWorkRunContext(workRunContext);
// }
// StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());
// // 检查API配置，保存配置信息
// if (processNeverRun(workEvent, 1)) {
//
// // 判断作业配置是否为空
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测作业配置 \n");
// ApiWorkConfig apiWorkConfig = workRunContext.getApiWorkConfig();
// if (apiWorkConfig == null) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测作业失败 : 接口调用作业配置为空不能执行
// \n");
// }
//
// // 检测作业请求方式是否符合规范
// if (StringUtils.isBlank(apiWorkConfig.getRequestType())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测作业失败 : 接口调用作业请求方式为空不能执行
// \n");
// }
//
// if (!ApiType.GET.equals(apiWorkConfig.getRequestType())
// && !ApiType.POST.equals(apiWorkConfig.getRequestType())) {
// throw new WorkRunException(
// LocalDateTime.now() + WorkLog.ERROR_INFO + "检测作业失败 : 接口调用作业请求方式仅支持POST/GET \n");
// }
//
// // 检测接口url是否为空
// if (StringUtils.isBlank(apiWorkConfig.getRequestUrl())) {
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测作业失败 : 接口调用作业请求url为空不能执行
// \n");
// }
//
// // 检查通过
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行接口调用 \n");
// workInstance = updateInstance(workInstance, logBuilder);
//
// // 保存API配置信息
// workEventBody.setApiWorkConfig(apiWorkConfig);
// workEvent.setEventContext(JSON.toJSONString(workEventBody));
// workEventRepository.saveAndFlush(workEvent);
// }
// // 处理请求参数和执行API调用，保存执行结果
// if (processNeverRun(workEvent, 2)) {
//
// // 获取API配置
// ApiWorkConfig apiWorkConfig = workEventBody.getApiWorkConfig();
//
// Object response = null;
// try {
//
// // 转换一下结构
// Map<String, String> requestParam = new HashMap<>();
// Map<String, String> requestHeader = new HashMap<>();
// for (int i = 0; i < apiWorkConfig.getRequestParam().size(); i++) {
// ApiWorkValueDto e = apiWorkConfig.getRequestParam().get(i);
// if (!e.getLabel().isEmpty()) {
// requestParam.put(e.getLabel(), parseJsonPath(e.getValue(), workInstance));
// }
// }
// for (int i = 0; i < apiWorkConfig.getRequestHeader().size(); i++) {
// ApiWorkValueDto e = apiWorkConfig.getRequestHeader().get(i);
// if (!e.getLabel().isEmpty()) {
// requestHeader.put(e.getLabel(), parseJsonPath(e.getValue(), workInstance));
// }
// }
//
// if (ApiType.GET.equals(apiWorkConfig.getRequestType())) {
// response =
// HttpUtils.doGet(apiWorkConfig.getRequestUrl(), requestParam, requestHeader, Object.class);
// }
// if (ApiType.POST.equals(apiWorkConfig.getRequestType())) {
// response = HttpUtils.doPost(apiWorkConfig.getRequestUrl(), requestHeader,
// JSON.parseObject(parseJsonPath(apiWorkConfig.getRequestBody(), workInstance), Object.class));
// }
//
// log.debug("获取远程返回数据:{}", response);
// } catch (Exception e) {
// log.debug(e.getMessage(), e);
// throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业执行异常 : "
// + e.getMessage().replace("<EOL>", "\n") + "\n");
// }
//
// // 保存运行日志
// workInstance.setResultData(String.valueOf(response));
// logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("请求成功, 查看运行结果 \n");
// updateInstance(workInstance, logBuilder);
//
// // 保存执行结果
// workEventBody.setResponse(response);
// workEvent.setEventContext(JSON.toJSONString(workEventBody));
// workEventRepository.saveAndFlush(workEvent);
// }
//
// return InstanceStatus.SUCCESS;
// }
//
// @Override
// protected void abort(WorkInstanceEntity workInstance) {
//
// Thread thread = WORK_THREAD.get(workInstance.getId());
// thread.interrupt();
// }
//
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public static class ApiExecutorContext {
//
// private WorkRunContext workRunContext;
//
// private ApiWorkConfig apiWorkConfig;
//
// private Object response;
// }
// }
