package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.api.constants.ApiType;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.dto.ApiWorkConfig;
import com.isxcode.spark.api.work.dto.ApiWorkValueDto;
import com.isxcode.spark.common.utils.http.HttpUtils;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.repository.VipWorkVersionRepository;
import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
import com.isxcode.spark.modules.work.repository.WorkRepository;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.modules.work.service.WorkService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import org.quartz.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ApiExecutor extends WorkExecutor {

    public ApiExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, AlarmService alarmService,
        SqlFunctionService sqlFunctionService, WorkEventRepository workEventRepository, Scheduler scheduler,
        WorkRunJobFactory workRunJobFactory, VipWorkVersionRepository vipWorkVersionRepository,
        WorkConfigRepository workConfigRepository, WorkRepository workRepository, Locker locker,
        WorkService workService) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository,
            workService);
    }

    @Override
    public String getWorkType() {
        return WorkType.API;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
        WorkEventEntity workEvent) {

        // 获取实例日志
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 打印首行日志
        if (workEvent.getEventProcess() == 0) {
            logBuilder.append(startLog("开始检测作业配置"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检查API配置
        if (workEvent.getEventProcess() == 1) {

            // 检查配置是否为空
            ApiWorkConfig apiWorkConfig = workRunContext.getApiWorkConfig();
            if (apiWorkConfig == null) {
                throw errorLogException("检测作业失败 : 接口调用作业配置为空不能执行");
            }

            // 检测作业请求方式是否符合规范
            if (StringUtils.isBlank(apiWorkConfig.getRequestType())) {
                throw errorLogException("检测作业失败 : 接口调用作业请求方式为空不能执行");
            }

            // 检查请求方式
            if (!ApiType.GET.equals(apiWorkConfig.getRequestType())
                && !ApiType.POST.equals(apiWorkConfig.getRequestType())) {
                throw errorLogException("检测作业失败 : 接口调用作业请求方式仅支持POST/GET");
            }

            // 检测接口url是否为空
            if (StringUtils.isBlank(apiWorkConfig.getRequestUrl())) {
                throw errorLogException("检测作业失败 : 接口调用作业请求url为空不能执行");
            }

            // 保存事件
            workRunContext.setApiWorkConfig(apiWorkConfig);

            // 保存日志
            logBuilder.append(endLog("作业检测正常"));
            logBuilder.append(startLog("开始执行接口调用"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 开始执行作业
        if (workEvent.getEventProcess() == 2) {

            // 上下文获取参数
            ApiWorkConfig apiWorkConfig = workRunContext.getApiWorkConfig();

            Object response = null;
            try {

                // 转换一下结构
                Map<String, String> requestParam = new HashMap<>();
                Map<String, String> requestHeader = new HashMap<>();
                for (int i = 0; i < apiWorkConfig.getRequestParam().size(); i++) {
                    ApiWorkValueDto e = apiWorkConfig.getRequestParam().get(i);
                    if (!e.getLabel().isEmpty()) {
                        requestParam.put(e.getLabel(), parseJsonPath(e.getValue(), workInstance));
                    }
                }
                for (int i = 0; i < apiWorkConfig.getRequestHeader().size(); i++) {
                    ApiWorkValueDto e = apiWorkConfig.getRequestHeader().get(i);
                    if (!e.getLabel().isEmpty()) {
                        requestHeader.put(e.getLabel(), parseJsonPath(e.getValue(), workInstance));
                    }
                }
                if (ApiType.GET.equals(apiWorkConfig.getRequestType())) {
                    response =
                        HttpUtils.doGet(apiWorkConfig.getRequestUrl(), requestParam, requestHeader, Object.class);
                }
                if (ApiType.POST.equals(apiWorkConfig.getRequestType())) {
                    response = HttpUtils.doPost(apiWorkConfig.getRequestUrl(), requestHeader,
                        JSON.parseObject(parseJsonPath(apiWorkConfig.getRequestBody(), workInstance), Object.class));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                errorLog("作业执行异常 : " + e.getMessage().replace("<EOL>", "\n"));
            }

            // 保存结果
            workInstance.setResultData(String.valueOf(response));

            // 保存日志
            logBuilder.append(endLog("请求成功, 查看运行结果"));
            updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        return InstanceStatus.SUCCESS;
    }

    @Override
    protected void abort(WorkInstanceEntity workInstance) {

        Thread thread = WORK_THREAD.get(workInstance.getId());
        thread.interrupt();
    }
}
