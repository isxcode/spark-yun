package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.api.constants.ApiType;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.dto.ApiWorkConfig;
import com.isxcode.spark.api.work.dto.ApiWorkValueDto;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
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
import org.apache.logging.log4j.util.Strings;
import org.quartz.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ApiExecutor extends WorkExecutor {

    private final IsxAppProperties isxAppProperties;

    private final Scheduler scheduler;

    private final WorkService workService;

    private final ServerProperties serverProperties;

    public ApiExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, AlarmService alarmService,
        SqlFunctionService sqlFunctionService, WorkEventRepository workEventRepository, Scheduler scheduler,
        WorkRunJobFactory workRunJobFactory, VipWorkVersionRepository vipWorkVersionRepository,
        WorkConfigRepository workConfigRepository, WorkRepository workRepository, Locker locker,
        WorkService workService, IsxAppProperties isxAppProperties, ServerProperties serverProperties) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository,
            workService);
        this.isxAppProperties = isxAppProperties;
        this.scheduler = scheduler;
        this.workService = workService;
        this.serverProperties = serverProperties;
    }

    @Override
    public String getWorkType() {
        return WorkType.API;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
        WorkEventEntity workEvent) {

        // 获取日志
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 打印首行日志，防止前端卡顿
        if (workEvent.getEventProcess() == 0) {
            logBuilder.append(startLog("检测API配置开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检测API配置
        if (workEvent.getEventProcess() == 1) {

            // 检查配置是否为空
            ApiWorkConfig apiWorkConfig = workRunContext.getApiWorkConfig();
            if (apiWorkConfig == null) {
                throw errorLogException("检测API配置异常 : 作业配置不能为空");
            }

            // 检测作业请求方式是否为空
            if (StringUtils.isBlank(apiWorkConfig.getRequestType())) {
                throw errorLogException("检测API配置异常 : 请求方式不能为空");
            }

            // 检查请求方式是否符合规范
            if (!ApiType.GET.equals(apiWorkConfig.getRequestType())
                && !ApiType.POST.equals(apiWorkConfig.getRequestType())) {
                throw errorLogException("检测API配置异常 : 请求方式仅支持POST/GET");
            }

            // 检测请求接口是否为空
            if (StringUtils.isBlank(apiWorkConfig.getRequestUrl())) {
                throw errorLogException("检测API配置异常 : 请求URL不能为空");
            }

            // 保存上下文
            workRunContext.setApiWorkConfig(apiWorkConfig);

            // 保存日志
            logBuilder.append(endLog("检测API配置完成"));
            logBuilder.append(startLog("执行调用开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 执行调用
        if (workEvent.getEventProcess() == 2) {

            // 记录当前线程
            WORK_THREAD.put(workEvent.getId(), Thread.currentThread());
            workRunContext.setIsxAppName(isxAppProperties.getAppName());
            updateWorkEvent(workEvent, workRunContext);

            // 获取上下文参数
            ApiWorkConfig apiWorkConfig = workRunContext.getApiWorkConfig();

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
                    Object response =
                        HttpUtils.doGet(apiWorkConfig.getRequestUrl(), requestParam, requestHeader, Object.class);

                    // 保存结果
                    workInstance.setResultData(JSON.toJSONString(response));
                }
                if (ApiType.POST.equals(apiWorkConfig.getRequestType())) {
                    Object response = HttpUtils.doPost(apiWorkConfig.getRequestUrl(), requestHeader,
                        JSON.parseObject(parseJsonPath(apiWorkConfig.getRequestBody(), workInstance), Object.class));

                    // 保存结果
                    workInstance.setResultData(JSON.toJSONString(response));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);

                // 优化日志
                throw errorLogException("执行调用异常 : " + e.getMessage().replace("<EOL>", "\n"));
            } finally {

                // 运行完删除多余线程池
                WORK_THREAD.remove(workEvent.getId());
            }

            // 保存日志
            logBuilder.append(endLog("执行调用完成"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 判断状态
        if (InstanceStatus.FAIL.equals(workRunContext.getPreStatus())) {
            throw errorLogException("最终状态为失败");
        }
        return InstanceStatus.SUCCESS;
    }

    @Override
    protected boolean abort(WorkInstanceEntity workInstance, WorkEventEntity workEvent) {

        // 还未提交
        if (workEvent.getEventProcess() < 2) {
            return true;
        }

        // 运行完毕
        if (workEvent.getEventProcess() > 2) {
            return false;
        }

        // 运行中，中止作业
        WorkRunContext workRunContext = JSON.parseObject(workEvent.getEventContext(), WorkRunContext.class);
        if (!Strings.isEmpty(workRunContext.getIsxAppName())) {

            // 杀死程序
            String killUrl = "http://" + isxAppProperties.getNodes().get(isxAppProperties.getAppName()) + ":"
                + serverProperties.getPort() + "/ha/open/kill";
            URI uri =
                UriComponentsBuilder.fromHttpUrl(killUrl).queryParam("workEventId", workEvent.getId()).build().toUri();
            new RestTemplate().exchange(uri, HttpMethod.GET, null, String.class);
        }

        return true;
    }
}
