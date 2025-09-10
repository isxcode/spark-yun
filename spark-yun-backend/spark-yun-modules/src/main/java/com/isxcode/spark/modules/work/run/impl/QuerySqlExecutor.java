package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.datasource.constants.DatasourceConfig;
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
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
import com.isxcode.spark.modules.work.run.WorkExecutor;
import com.isxcode.spark.modules.work.run.WorkInfo;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.sql.SqlCommentService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.work.sql.SqlValueService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.entity.VipWorkVersionEntity;
import com.isxcode.spark.modules.work.repository.VipWorkVersionRepository;
import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
import com.isxcode.spark.modules.work.repository.WorkRepository;
import com.isxcode.spark.common.locker.Locker;
import org.quartz.Scheduler;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    private final WorkEventRepository workEventRepository;

    private final Scheduler scheduler;

    private final WorkRunJobFactory workRunJobFactory;

    private final VipWorkVersionRepository vipWorkVersionRepository;

    private final WorkConfigRepository workConfigRepository;

    private final WorkRepository workRepository;

    private final Locker locker;

    public QuerySqlExecutor(DatasourceRepository datasourceRepository, WorkInstanceRepository
                                workInstanceRepository,
                            WorkflowInstanceRepository workflowInstanceRepository, SqlCommentService sqlCommentService,
                            SqlFunctionService sqlFunctionService, SqlValueService sqlValueService, AlarmService
                                alarmService,
                            DataSourceFactory dataSourceFactory, DatasourceMapper datasourceMapper,
                            WorkEventRepository workEventRepository, Scheduler scheduler, WorkRunJobFactory workRunJobFactory,
                            VipWorkVersionRepository vipWorkVersionRepository, WorkConfigRepository workConfigRepository,
                            WorkRepository workRepository, Locker locker) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository, workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository);
        this.datasourceRepository = datasourceRepository;
        this.sqlCommentService = sqlCommentService;
        this.sqlFunctionService = sqlFunctionService;
        this.sqlValueService = sqlValueService;
        this.dataSourceFactory = dataSourceFactory;
        this.datasourceMapper = datasourceMapper;
        this.workEventRepository = workEventRepository;
        this.scheduler = scheduler;
        this.workRunJobFactory = workRunJobFactory;
        this.vipWorkVersionRepository = vipWorkVersionRepository;
        this.workConfigRepository = workConfigRepository;
        this.workRepository = workRepository;
        this.locker = locker;
    }

    @Override
    public String getWorkType() {
        return WorkType.QUERY_JDBC_SQL;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance, WorkEventEntity workEvent) {

        // 获取作业运行上下文
        QuerySqlExecutorContext workEventBody = JSON.parseObject(workEvent.getEventContext(), QuerySqlExecutorContext.class);
        if (workEventBody == null) {
            workEventBody = new QuerySqlExecutorContext();
            workEventBody.setWorkRunContext(workRunContext);
        }
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 检查数据源配置是否合法，并保存数据源信息
        if (processNeverRun(workEvent, 1)) {

            // 检测数据源是否配置
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测运行环境 \n");
            if (Strings.isEmpty(workRunContext.getDatasourceId())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源 \n");
            }

            // 检查数据源是否存在
            Optional<DatasourceEntity> datasourceEntityOptional =
                datasourceRepository.findById(workRunContext.getDatasourceId());
            if (!datasourceEntityOptional.isPresent()) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源 \n");
            }
            DatasourceEntity datasourceEntity = datasourceEntityOptional.get();

            // 数据源检查通过
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成 \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 保存数据源信息
            workEventBody.setDatasourceEntity(datasourceEntity);
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }

        // 检查脚本和SQL处理，保存处理后的脚本
        if (processNeverRun(workEvent, 2)) {

            // 检查脚本是否为空
            if (Strings.isEmpty(workRunContext.getScript())) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Sql内容为空 \n");
            }

            // 脚本检查通过
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 判断实例中是否有脚本
            String script;
            if (workInstance.getWorkInfo() == null) {
                // 去掉sql中的注释
                String sqlNoComment = sqlCommentService.removeSqlComment(workRunContext.getScript());

                // 解析上游参数
                String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);

                // 翻译sql中的系统变量
                String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

                // 翻译sql中的系统函数
                try {
                    script = sqlFunctionService.parseSqlFunction(parseValueSql);
                } catch (Exception e) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "系统函数异常\n" + e.getMessage() + "\n");
                }

                workInstance.setWorkInfo(JSON.toJSONString(WorkInfo.builder().script(script).build()));
            } else {
                WorkInfo workInfo = JSON.parseObject(workInstance.getWorkInfo(), WorkInfo.class);
                script = workInfo.getScript();
            }

            // 清除脚本中的脏数据
            List<String> sqls =
                Arrays.stream(script.split(";")).filter(Strings::isNotBlank).collect(Collectors.toList());

            // 保存处理后的脚本信息
            workEventBody.setScript(script);
            workEventBody.setSqls(sqls);
            workEvent.setEventContext(JSON.toJSONString(workEventBody));
            workEventRepository.saveAndFlush(workEvent);
        }

        // 执行SQL语句，保存执行结果
        if (processNeverRun(workEvent, 3)) {

            // 获取数据源和脚本信息
            DatasourceEntity datasourceEntity = workEventBody.getDatasourceEntity();
            List<String> sqls = workEventBody.getSqls();

            // 开始执行sql
            ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
            Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
            connectInfo.setLoginTimeout(5);
            try (Connection connection = datasource.getConnection(connectInfo);
                 Statement statement = connection.createStatement();) {

                statement.setQueryTimeout(1800);

                // 执行每条sql，除了最后一条
                for (int i = 0; i < sqls.size() - 1; i++) {

                    // 记录开始执行时间
                    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行SQL: \n")
                        .append(sqls.get(i)).append(" \n");
                    workInstance = updateInstance(workInstance, logBuilder);

                    // 执行sql
                    statement.execute(sqls.get(i));

                    // 记录结束执行时间
                    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("SQL执行成功 \n");
                    workInstance = updateInstance(workInstance, logBuilder);
                }

                // 执行查询sql，给lastSql添加查询条数限制
                String lastSql = sqls.get(sqls.size() - 1);

                // 特殊查询语句直接跳过
                if (!lastSql.toUpperCase().trim().startsWith("SHOW") &&
                    !lastSql.toUpperCase().trim().startsWith("DESCRIBE")
                    && !lastSql.replace(" ", "").toLowerCase().startsWith("selectcount")) {

                    // 判断返回结果的条数，超过200条，则提出警告
                    String countSql = String.format("SELECT COUNT(*) FROM ( %s ) temp", lastSql);
                    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("执行条数检测SQL: \n")
                        .append(countSql).append(" \n");
                    workInstance = updateInstance(workInstance, logBuilder);
                    ResultSet countResultSet = statement.executeQuery(countSql);
                    while (countResultSet.next()) {
                        if (countResultSet.getInt(1) > DatasourceConfig.LIMIT_NUMBER) {
                            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "条数大于200条，请添加sql行数限制 \n");
                        }
                    }
                }

                // 执行最后一句查询语句
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("执行查询SQL: \n").append(lastSql).append(" \n");
                workInstance = updateInstance(workInstance, logBuilder);
                ResultSet resultSet = statement.executeQuery(lastSql);

                // 记录结束执行时间
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("查询SQL执行成功 \n");
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

                // 将data转为json存到实例中
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功 \n");
                workInstance.setResultData(JSON.toJSONString(result));
                updateInstance(workInstance, logBuilder);

                // 保存执行结果
                workEventBody.setResult(result);
                workEvent.setEventContext(JSON.toJSONString(workEventBody));
                workEventRepository.saveAndFlush(workEvent);

            } catch (WorkRunException | IsxAppException e) {
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMsg() + "\n");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMessage() + "\n");
            }
        }

        return InstanceStatus.SUCCESS;
    }

    @Override
    protected void abort(WorkInstanceEntity workInstance) {

        Thread thread = WORK_THREAD.get(workInstance.getId());
        thread.interrupt();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuerySqlExecutorContext {

        private WorkRunContext workRunContext;

        private DatasourceEntity datasourceEntity;

        private String script;

        private List<String> sqls;

        private List<List<String>> result;
    }
}
