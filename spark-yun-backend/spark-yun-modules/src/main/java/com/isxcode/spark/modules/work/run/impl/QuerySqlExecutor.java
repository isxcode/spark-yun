package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.datasource.constants.DatasourceConfig;
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
import com.isxcode.spark.modules.secret.repository.SecretKeyRepository;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.*;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.sql.SqlCommentService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.work.sql.SqlValueService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QuerySqlExecutor extends WorkExecutor {

    private final DatasourceRepository datasourceRepository;

    private final SqlCommentService sqlCommentService;

    private final SqlFunctionService sqlFunctionService;

    private final SqlValueService sqlValueService;

    private final DataSourceFactory dataSourceFactory;

    private final DatasourceMapper datasourceMapper;

    private final SecretKeyRepository secretKeyRepository;

    private final WorkEventRepository workEventRepository;

    public QuerySqlExecutor(WorkInstanceRepository workInstanceRepository,
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
        return WorkType.QUERY_JDBC_SQL;
    }

    public String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance, WorkEventEntity workEvent) {

        // 获取日志
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 校验环境、解析脚本并保存
        if (workEvent.getEventProcess() == 0) {
            // 检测数据源是否配置
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测运行环境 \n");
            if (Strings.isEmpty(workRunContext.getDatasourceId())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源  \n");
            }

            // 检查数据源是否存在
            Optional<DatasourceEntity> datasourceEntityOptional =
                datasourceRepository.findById(workRunContext.getDatasourceId());
            if (!datasourceEntityOptional.isPresent()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源  \n");
            }

            // 数据源检查通过
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成  \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 检查脚本是否为空
            if (Strings.isEmpty(workRunContext.getScript())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Sql内容为空 \n");
            }

            // 保存事件
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 解析SQL脚本
        if (workEvent.getEventProcess() == 1) {

            // 去掉sql中的注释
            String sqlNoComment = sqlCommentService.removeSqlComment(workRunContext.getScript());

            // 解析上游参数
            String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);

            // 翻译sql中的系统变量
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

            String script;

            // 翻译sql中的系统函数
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

        // 执行SQL语句
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
                Statement statement = connection.createStatement();) {

                statement.setQueryTimeout(1800);

                // 清除脚本中的脏数据
                List<String> sqls =
                    Arrays.stream(script.split(";")).filter(Strings::isNotBlank).collect(Collectors.toList());

                // 执行每条sql，除了最后一条
                for (int i = 0; i < sqls.size() - 1; i++) {

                    // 记录开始执行时间
                    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行SQL: \n")
                        .append(sqls.get(i)).append(" \n");
                    workInstance = updateInstance(workInstance, logBuilder);

                    // 执行sql
                    statement.execute(sqls.get(i));

                    // 记录结束执行时间
                    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("SQL执行成功  \n");
                    workInstance = updateInstance(workInstance, logBuilder);
                }

                // 执行查询sql，给lastSql添加查询条数限制
                String lastSql = sqls.get(sqls.size() - 1);

                // 特殊查询语句直接跳过
                if (!lastSql.toUpperCase().trim().startsWith("SHOW")
                    && !lastSql.toUpperCase().trim().startsWith("DESCRIBE")
                    && !lastSql.replace(" ", "").toLowerCase().startsWith("selectcount")) {

                    // 判断返回结果的条数，超过200条，则提出警告
                    String countSql = String.format("SELECT COUNT(*) FROM ( %s ) temp", lastSql);
                    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("执行条数检测SQL: \n")
                        .append(countSql).append(" \n");
                    workInstance = updateInstance(workInstance, logBuilder);
                    ResultSet countResultSet = statement.executeQuery(countSql);
                    while (countResultSet.next()) {
                        if (countResultSet.getInt(1) > DatasourceConfig.LIMIT_NUMBER) {
                            throw new WorkRunException(
                                LocalDateTime.now() + WorkLog.ERROR_INFO + "条数大于200条，请添加sql行数限制 \n");
                        }
                    }
                }

                // 执行最后一句查询语句
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("执行查询SQL: \n")
                    .append(lastSql).append(" \n");
                workInstance = updateInstance(workInstance, logBuilder);
                ResultSet resultSet = statement.executeQuery(lastSql);

                // 记录结束执行时间
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("查询SQL执行成功  \n");
                workInstance = updateInstance(workInstance, logBuilder);

                // 记录返回结果
                List<List<String>> result = new ArrayList<>();

                // 封装表头
                int columnCount = resultSet.getMetaData().getColumnCount();
                List<String> metaList = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    metaList.add(resultSet.getMetaData().getColumnName(i));
                }
                result.add(metaList);

                // 封装数据
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

                // 讲data转为json存到实例中
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功  \n");
                workInstance.setResultData(JSON.toJSONString(result));
                updateInstance(workInstance, logBuilder);
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
    protected void abort(WorkInstanceEntity workInstance) {

        Thread thread = WORK_THREAD.get(workInstance.getId());
        if (thread != null) {
            thread.interrupt();
        }
    }
}
