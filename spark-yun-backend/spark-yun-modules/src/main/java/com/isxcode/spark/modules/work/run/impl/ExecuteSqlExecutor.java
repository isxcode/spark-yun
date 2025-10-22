package com.isxcode.spark.modules.work.run.impl;

import com.isxcode.spark.api.datasource.dto.ConnectInfo;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.datasource.entity.DatasourceEntity;
import com.isxcode.spark.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.spark.modules.datasource.repository.DatasourceRepository;
import com.isxcode.spark.modules.datasource.source.DataSourceFactory;
import com.isxcode.spark.modules.datasource.source.Datasource;
import com.isxcode.spark.modules.secret.repository.SecretKeyRepository;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.service.WorkService;
import com.isxcode.spark.modules.work.sql.SqlCommentService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.work.sql.SqlValueService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import com.isxcode.spark.modules.work.repository.WorkRepository;
import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
import com.isxcode.spark.modules.work.repository.VipWorkVersionRepository;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import org.quartz.Scheduler;
import com.isxcode.spark.common.locker.Locker;


import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExecuteSqlExecutor extends WorkExecutor {

    private final DatasourceRepository datasourceRepository;

    private final SqlCommentService sqlCommentService;

    private final SqlValueService sqlValueService;

    private final SqlFunctionService sqlFunctionService;

    private final DataSourceFactory dataSourceFactory;

    private final DatasourceMapper datasourceMapper;

    public ExecuteSqlExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, DatasourceRepository datasourceRepository,
        SqlCommentService sqlCommentService, SqlValueService sqlValueService, SqlFunctionService sqlFunctionService,
        AlarmService alarmService, DataSourceFactory dataSourceFactory, DatasourceMapper datasourceMapper,
        SecretKeyRepository secretKeyRepository, WorkEventRepository workEventRepository, Scheduler scheduler,
        Locker locker, WorkRepository workRepository, WorkRunJobFactory workRunJobFactory,
        WorkConfigRepository workConfigRepository, VipWorkVersionRepository vipWorkVersionRepository,
        WorkService workService) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository,
            workService);
        this.datasourceRepository = datasourceRepository;
        this.sqlCommentService = sqlCommentService;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.dataSourceFactory = dataSourceFactory;
        this.datasourceMapper = datasourceMapper;
    }

    @Override
    public String getWorkType() {
        return WorkType.EXECUTE_JDBC_SQL;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance, WorkEventEntity workEvent)
        throws Exception {

        // 获取实例日志
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 打印首行日志
        if (workEvent.getEventProcess() == 0) {
            logBuilder.append(startLog("开始检测数据源"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检查数据源
        if (workEvent.getEventProcess() == 1) {

            // 检测数据源是否配置
            if (Strings.isEmpty(workRunContext.getDatasourceId())) {
                throw errorLogException("检测数据源失败: 未配置有效数据源");
            }

            // 检查数据源是否存在
            datasourceRepository.findById(workRunContext.getDatasourceId())
                .orElseThrow(() -> errorLogException("检测数据源失败: 数据源不存在"));

            // 保存事件
            logBuilder.append(endLog("数据源检测正常"));
            logBuilder.append(startLog("开始检测Sql脚本"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 解析SQL脚本
        if (workEvent.getEventProcess() == 2) {

            // 检查脚本是否为空
            if (Strings.isEmpty(workRunContext.getScript())) {
                throw errorLogException("检测脚本失败 : Sql内容为空不能执行");
            }

            // 去掉sql中的注释
            String sqlNoComment = sqlCommentService.removeSqlComment(workRunContext.getScript());

            // 解析上游参数
            String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);

            // 翻译sql中的系统变量
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

            // 翻译sql中的系统函数
            String script = sqlFunctionService.parseSqlFunction(parseValueSql);

            // 保存事件
            workRunContext.setScript(script);

            // 保存日志
            logBuilder.append(endLog("脚本检测正常"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 执行脚本
        if (workEvent.getEventProcess() == 3) {

            // 上下文获取参数
            String script = workRunContext.getScript();
            String datasourceId = workRunContext.getDatasourceId();

            // 获取数据源
            DatasourceEntity datasourceEntity = datasourceRepository.findById(datasourceId).get();
            ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
            Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
            connectInfo.setLoginTimeout(5);

            try (Connection connection = datasource.getConnection(connectInfo);
                Statement statement = connection.createStatement()) {

                statement.setQueryTimeout(1800);

                // 清除脚本中的脏数据
                List<String> sqls =
                    Arrays.stream(script.split(";")).filter(Strings::isNotBlank).collect(Collectors.toList());

                // 逐条执行sql
                for (String sql : sqls) {

                    // 记录开始执行时间
                    logBuilder.append(startLog("开始执行SQL"));
                    logBuilder.append("> ").append(sql).append(" \n");
                    workInstance = updateInstance(workInstance, logBuilder);

                    // 执行sql
                    statement.execute(sql);

                    // 记录结束执行时间
                    logBuilder.append(endLog("SQL执行成功"));
                    workInstance = updateInstance(workInstance, logBuilder);
                }

            } catch (WorkRunException | IsxAppException e) {
                throw errorLogException(log + "\n" + e.getMsg());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw errorLogException(log + "\n" + e.getMessage());
            }

            // 保存事件
            updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        return InstanceStatus.SUCCESS;
    }

    @Override
    protected void abort(WorkInstanceEntity workInstance) throws Exception {

        Thread thread = WORK_THREAD.get(workInstance.getId());
        if (thread != null) {
            thread.interrupt();
        }
    }
}
