package com.isxcode.spark.modules.work.run.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.common.utils.path.PathUtils;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.*;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.service.WorkService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.quartz.Scheduler;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;

@Service
@Slf4j
public class CurlExecutor extends WorkExecutor {

    private final IsxAppProperties isxAppProperties;

    public CurlExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, SqlFunctionService sqlFunctionService,
        AlarmService alarmService, WorkEventRepository workEventRepository, Scheduler scheduler, Locker locker,
        WorkRepository workRepository, WorkRunJobFactory workRunJobFactory, WorkConfigRepository workConfigRepository,
        VipWorkVersionRepository vipWorkVersionRepository, IsxAppProperties isxAppProperties, WorkService workService) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository,
            workService);
        this.isxAppProperties = isxAppProperties;
    }

    @Override
    public String getWorkType() {
        return WorkType.CURL;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
        WorkEventEntity workEvent) {

        // 获取日志
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 打印首行日志，防止前端卡顿
        if (workEvent.getEventProcess() == 0) {
            logBuilder.append(startLog("检测Curl脚本开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检测Curl脚本
        if (workEvent.getEventProcess() == 1) {

            // 检测脚本是否为空
            String script = workRunContext.getScript();
            if (Strings.isEmpty(script)) {
                throw errorLogException("检测Curl脚本异常 : Curl脚本不能为空");
            }

            // 兼容老版写法
            if (!script.contains("curl -s ") && !script.contains("curl --silent ")) {
                script = script.replace("curl ", "curl -s ");
            }

            // 脚本需要返回网络状态
            script = script.replace("curl", "curl -w \"%{http_code}\" ");

            // 保存上下文
            workRunContext.setScript(script);

            // 保存日志
            logBuilder.append(script).append("\n");
            logBuilder.append(endLog("检测Curl脚本完成"));
            logBuilder.append(startLog("执行Curl脚本开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 执行Curl脚本
        if (workEvent.getEventProcess() == 2) {

            // 获取上下文参数
            String script = workRunContext.getScript();

            // 将脚本推送到本地
            String bashFile = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + "/work/"
                + workRunContext.getTenantId() + "/" + workInstance.getId() + ".sh";
            FileUtil.writeUtf8String(script + " \\\n && echo 'zhiqingyun_success'", bashFile);

            // 执行本地命令
            String executeBashWorkCommand = "bash " + PathUtils.parseProjectPath(isxAppProperties.getResourcesPath())
                + "/work/" + workRunContext.getTenantId() + "/" + workInstance.getId() + ".sh";
            String result = RuntimeUtil.execForStr(executeBashWorkCommand);
            String yarnLog = result.replace("&& echo 'zhiqingyun_success'", "").replace("zhiqingyun_success", "");

            // 保存日志和数据
            workInstance.setYarnLog(yarnLog);
            workInstance.setResultData(yarnLog.substring(0, yarnLog.length() - 4));

            // 如果日志不包含关键字则为异常
            if (!result.contains("200zhiqingyun_success")) {
                workRunContext.setPreStatus(InstanceStatus.FAIL);
            }

            // 保存日志
            logBuilder.append(endLog("执行Curl脚本完成"));
            logBuilder.append(startLog("清理缓存文件开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 清理缓存文件
        if (workEvent.getEventProcess() == 3) {
            try {
                String clearWorkRunFile =
                    "rm -f " + PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "work"
                        + File.separator + workRunContext.getTenantId() + File.separator + workInstance.getId() + ".sh";
                RuntimeUtil.execForStr(clearWorkRunFile);
            } catch (Exception e) {
                log.error(e.getMessage(), e);

                // 优化日志
                throw errorLogException("清理缓存文件异常 : " + e.getMessage());
            }

            // 保存日志
            logBuilder.append(endLog("清理缓存文件完成"));
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
