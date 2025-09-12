package com.isxcode.spark.modules.work.run.impl;

import com.isxcode.spark.api.datasource.dto.ConnectInfo;
import com.isxcode.spark.api.work.constants.WorkLog;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExecuteSqlExecutor extends WorkExecutor {

    private final DatasourceRepository datasourceRepository;

    private final SqlCommentService sqlCommentService;

    private final WorkEventRepository workEventRepository;

    private final SqlValueService sqlValueService;

    private final SqlFunctionService sqlFunctionService;

    private final DataSourceFactory dataSourceFactory;

    private final DatasourceMapper datasourceMapper;

    private final SecretKeyRepository secretKeyRepository;

    public ExecuteSqlExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, DatasourceRepository datasourceRepository,
        SqlCommentService sqlCommentService, SqlValueService sqlValueService, SqlFunctionService sqlFunctionService,
        AlarmService alarmService, DataSourceFactory dataSourceFactory, DatasourceMapper datasourceMapper,
        SecretKeyRepository secretKeyRepository, WorkEventRepository workEventRepository, Scheduler scheduler,
        Locker locker, WorkRepository workRepository, WorkRunJobFactory workRunJobFactory,
        WorkConfigRepository workConfigRepository, VipWorkVersionRepository vipWorkVersionRepository) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository);
        this.datasourceRepository = datasourceRepository;
        this.sqlCommentService = sqlCommentService;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.dataSourceFactory = dataSourceFactory;
        this.datasourceMapper = datasourceMapper;
        this.secretKeyRepository = secretKeyRepository;
        this.workEventRepository = workEventRepository;
    }

    @Override
    public String getWorkType() {
        return WorkType.EXECUTE_JDBC_SQL;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance, WorkEventEntity workEvent)
        throws Exception {

        // 获取日志
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 步骤1：校验环境、解析脚本并保存
        if (workEvent.getEventProcess() == 0) {

            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测运行环境 \n");
            if (Strings.isEmpty(workRunContext.getDatasourceId())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源 \n");
            }
            Optional<DatasourceEntity> datasourceEntityOptional =
                datasourceRepository.findById(workRunContext.getDatasourceId());
            if (!datasourceEntityOptional.isPresent()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源 \n");
            }
            if (Strings.isEmpty(workRunContext.getScript())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Sql内容为空 \n");
            }
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成 \n");

            // 保存事件
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 步骤2：解析SQL脚本
        if (workEvent.getEventProcess() == 1) {

            // 去掉sql中的注释 -> 解析上游参数 -> 系统变量 -> 系统函数
            String sqlNoComment = sqlCommentService.removeSqlComment(workRunContext.getScript());
            String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);
            String script;
            try {
                script = sqlFunctionService.parseSqlFunction(parseValueSql);
            } catch (Exception e) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "系统函数异常\n" + e.getMessage() + "\n");
            }

            // 保存脚本到事件上下文
            workRunContext.setScript(script);
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");

            // 保存事件
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 步骤3：执行SQL语句
        if (workEvent.getEventProcess() == 2) {

            // 读取脚本
            String script = workRunContext.getScript();

            Optional<DatasourceEntity> datasourceEntityOptional =
                datasourceRepository.findById(workRunContext.getDatasourceId());
            DatasourceEntity datasourceEntity = datasourceEntityOptional.get();
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
                    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行SQL:\n").append(sql)
                        .append(" \n");

                    // 记录开始执行时间
                    workInstance = updateInstance(workInstance, logBuilder);

                    // 执行sql
                    statement.execute(sql);

                    // 记录结束执行时间
                    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("SQL执行成功 \n");
                    workInstance = updateInstance(workInstance, logBuilder);
                }

            } catch (WorkRunException | IsxAppException e) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMsg() + "\n");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMessage() + "\n");
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
