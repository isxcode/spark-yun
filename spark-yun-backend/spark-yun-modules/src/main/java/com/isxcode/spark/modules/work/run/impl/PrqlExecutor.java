package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.datasource.constants.DatasourceType;
import com.isxcode.spark.api.datasource.dto.ConnectInfo;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.WorkLog;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.datasource.entity.DatasourceEntity;
import com.isxcode.spark.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.spark.modules.datasource.repository.DatasourceRepository;
import com.isxcode.spark.modules.datasource.source.DataSourceFactory;
import com.isxcode.spark.modules.datasource.source.Datasource;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
import com.isxcode.spark.modules.work.repository.WorkRepository;
import com.isxcode.spark.modules.work.repository.VipWorkVersionRepository;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.sql.SqlCommentService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.work.sql.SqlValueService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.prql.prql4j.PrqlCompiler;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PrqlExecutor extends WorkExecutor {

    private final DatasourceRepository datasourceRepository;

    private final SqlCommentService sqlCommentService;

    private final SqlValueService sqlValueService;

    private final SqlFunctionService sqlFunctionService;

    private final DataSourceFactory dataSourceFactory;

    private final DatasourceMapper datasourceMapper;


    private final WorkEventRepository workEventRepository;

    public PrqlExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, DatasourceRepository datasourceRepository,
        SqlCommentService sqlCommentService, SqlValueService sqlValueService, SqlFunctionService sqlFunctionService,
        AlarmService alarmService, WorkEventRepository workEventRepository, Scheduler scheduler, Locker locker,
        WorkRepository workRepository, WorkRunJobFactory workRunJobFactory, WorkConfigRepository workConfigRepository,
        VipWorkVersionRepository vipWorkVersionRepository, DataSourceFactory dataSourceFactory,
        DatasourceMapper datasourceMapper) {
        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository);
        this.datasourceRepository = datasourceRepository;
        this.sqlCommentService = sqlCommentService;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.dataSourceFactory = dataSourceFactory;
        this.datasourceMapper = datasourceMapper;
        this.workEventRepository = workEventRepository;
    }

    @Override
    public String getWorkType() {
        return WorkType.PRQL;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance, WorkEventEntity workEvent)
        throws Exception {

        // 线程登记
        WORK_THREAD.put(workInstance.getId(), Thread.currentThread());

        // 获取事件上下文与日志
        WorkRunContext workEventBody = JSON.parseObject(workEvent.getEventContext(), WorkRunContext.class);
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 步骤1：校验数据源与脚本，解析PRQL并保存
        if (processNeverRun(workEvent, 1)) {
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
            workInstance = updateInstance(workInstance, logBuilder);

            // 去掉sql中的注释 -> 解析上游参数 -> 翻译系统变量/函数
            String sqlNoComment = sqlCommentService.removeSqlComment(workRunContext.getScript());
            String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);
            String prqlScript = sqlFunctionService.parseSqlFunction(parseValueSql);

            // 打印PRQL脚本
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("执行PRQL:\n").append(prqlScript)
                .append(" \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 保存解析后的PRQL脚本
            workEventBody.setScript(prqlScript);
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }

        // 步骤2：编译PRQL为SQL并保存
        if (processNeverRun(workEvent, 2)) {
            DatasourceEntity datasourceEntity = datasourceRepository.findById(workRunContext.getDatasourceId()).get();
            String sql;
            try {
                sql = PrqlCompiler.toSql(workEventBody.getScript().replace(";", ""),
                    translateDBType(datasourceEntity.getDbType()), true, true);
            } catch (NoClassDefFoundError error) {
                throw new Exception(error.getMessage());
            }
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO)
                .append(String.format("prql转化完成: \n%s\n", sql));
            workInstance = updateInstance(workInstance, logBuilder);

            // 保存SQL到事件上下文（复用log字段）
            workEventBody.setLog(sql);
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }

        // 步骤3：执行SQL并保存结果
        if (processNeverRun(workEvent, 3)) {
            DatasourceEntity datasourceEntity = datasourceRepository.findById(workRunContext.getDatasourceId()).get();
            ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
            Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
            connectInfo.setLoginTimeout(5);
            try (Connection connection = datasource.getConnection(connectInfo);
                Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(1800);
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行SQL \n");
                workInstance = updateInstance(workInstance, logBuilder);
                statement.execute(workEventBody.getLog());

                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("SQL执行成功 \n");
                workInstance = updateInstance(workInstance, logBuilder);

                ResultSet resultSet = statement.getResultSet();
                List<List<String>> result = new ArrayList<>();
                int columnCount = resultSet.getMetaData().getColumnCount();
                List<String> metaList = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    metaList.add(resultSet.getMetaData().getColumnName(i));
                }
                result.add(metaList);
                while (resultSet.next()) {
                    metaList = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        try {
                            metaList.add(resultSet.getString(i));
                        } catch (Exception e) {
                            metaList.add(String.valueOf(resultSet.getObject(i)));
                        }
                    }
                    result.add(metaList);
                }
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功 \n");
                workInstance.setResultData(JSON.toJSONString(result));
                updateInstance(workInstance, logBuilder);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMessage() + "\n");
            }
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

    public static String translateDBType(String dbType) {

        switch (dbType) {
            case DatasourceType.MYSQL:
                return "mysql";
            case DatasourceType.CLICKHOUSE:
                return "clickhouse";
            case DatasourceType.POSTGRE_SQL:
            case DatasourceType.OPEN_GAUSS:
            case DatasourceType.GAUSS:
                return "postgres";
            case DatasourceType.H2:
                return "h2";
            default:
                throw new IsxAppException("当前数据库类型不支持");
        }
    }
}
