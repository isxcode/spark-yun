package com.isxcode.spark.modules.work.run.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.common.utils.path.PathUtils;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.spark.modules.datasource.source.DataSourceFactory;
import com.isxcode.spark.modules.secret.repository.SecretKeyRepository;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.*;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.service.WorkService;
import com.isxcode.spark.modules.work.sql.SqlCommentService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.work.sql.SqlValueService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class CurlExecutor extends WorkExecutor {

    private final IsxAppProperties isxAppProperties;

    public CurlExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, SqlCommentService sqlCommentService,
        SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService alarmService,
        DataSourceFactory dataSourceFactory, DatasourceMapper datasourceMapper, SecretKeyRepository secretKeyRepository,
        WorkEventRepository workEventRepository, Scheduler scheduler, Locker locker, WorkRepository workRepository,
        WorkRunJobFactory workRunJobFactory, WorkConfigRepository workConfigRepository,
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

        // 获取实例日志
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 打印首行日志
        if (workEvent.getEventProcess() == 0) {
            logBuilder.append(startLog("开始检测curl脚本"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检查脚本
        if (workEvent.getEventProcess() == 1) {

            String script = workRunContext.getScript();

            // 检查脚本是否为空
            if (Strings.isEmpty(script)) {
                throw errorLogException("检测脚本失败 : Curl内容为空不能执行");
            }

            // 脚本检查通过
            if (!script.contains("curl -s ") && !script.contains("curl --silent ")) {
                script = script.replace("curl ", "curl -s ");
            }
            script = script.replace("curl", "curl -w \"%{http_code}\" ");

            // 保存事件
            workRunContext.setScript(script);

            // 保存日志
            logBuilder.append(endLog("脚本检测正常"));
            logBuilder.append(script).append("\n");
            logBuilder.append(startLog("开始执行Curl"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 执行Curl脚本
        if (workEvent.getEventProcess() == 2) {

            // 上下文获取参数
            String script = workRunContext.getScript();

            // 将脚本推送到本地
            String bashFile = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + "/work/"
                + workRunContext.getTenantId() + "/" + workInstance.getId() + ".sh";
            FileUtil.writeUtf8String(script + " \\\n && echo 'zhiqingyun_success'", bashFile);

            // 执行命令
            String executeBashWorkCommand = "bash " + PathUtils.parseProjectPath(isxAppProperties.getResourcesPath())
                + "/work/" + workRunContext.getTenantId() + "/" + workInstance.getId() + ".sh";
            String result = RuntimeUtil.execForStr(executeBashWorkCommand);
            String yarnLog = result.replace("&& echo 'zhiqingyun_success'", "").replace("zhiqingyun_success", "");

            // 保存运行日志
            workInstance.setYarnLog(yarnLog);
            workInstance.setResultData(yarnLog.substring(0, yarnLog.length() - 4));
            logBuilder.append(endLog("调用成功"));

            // 如果日志不包含关键字则为失败
            if (!result.contains("200zhiqingyun_success")) {
                workRunContext.setPreStatus(InstanceStatus.FAIL);
            }

            // 保存日志
            logBuilder.append(startLog("开始清理执行文件"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 删除脚本
        if (workEvent.getEventProcess() == 3) {
            try {
                String clearWorkRunFile =
                    "rm -f " + PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "work"
                        + File.separator + workRunContext.getTenantId() + File.separator + workInstance.getId() + ".sh";
                RuntimeUtil.execForStr(clearWorkRunFile);
            } catch (Exception e) {
                errorLog("删除运行脚本失败 : " + e.getMessage());
            }

            // 保存日志
            logBuilder.append(endLog("清理执行脚本完成"));
            updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 如果最终状态为失败，抛出空异常
        if (InstanceStatus.FAIL.equals(workRunContext.getPreStatus())) {
            errorLog("作业最终状态为失败");
        }

        // 最终执行成功
        return InstanceStatus.SUCCESS;
    }

    @Override
    protected void abort(WorkInstanceEntity workInstance) {

        Thread thread = WORK_THREAD.get(workInstance.getId());
        thread.interrupt();
    }
}
