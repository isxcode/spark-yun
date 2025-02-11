package com.isxcode.star.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.dto.ConnectInfo;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.backend.api.base.exceptions.WorkRunException;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.alarm.service.AlarmService;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.datasource.source.DataSourceFactory;
import com.isxcode.star.modules.datasource.source.Datasource;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.work.run.WorkExecutor;
import com.isxcode.star.modules.work.run.WorkRunContext;
import com.isxcode.star.modules.work.sql.SqlCommentService;
import com.isxcode.star.modules.work.sql.SqlFunctionService;
import com.isxcode.star.modules.work.sql.SqlValueService;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.prql.prql4j.PrqlCompiler;
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

    public PrqlExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, DatasourceRepository datasourceRepository,
        SqlCommentService sqlCommentService, SqlValueService sqlValueService, SqlFunctionService sqlFunctionService,
        AlarmService alarmService, DataSourceFactory dataSourceFactory, DatasourceMapper datasourceMapper) {
        super(workInstanceRepository, workflowInstanceRepository, alarmService, sqlFunctionService);
        this.datasourceRepository = datasourceRepository;
        this.sqlCommentService = sqlCommentService;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.dataSourceFactory = dataSourceFactory;
        this.datasourceMapper = datasourceMapper;
    }

    @Override
    public String getWorkType() {
        return WorkType.PRQL;
    }

    @Override
    protected void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

        // 将线程存到Map
        WORK_THREAD.put(workInstance.getId(), Thread.currentThread());

        // 获取日志构造器
        StringBuilder logBuilder = workRunContext.getLogBuilder();

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
        DatasourceEntity datasourceEntity = datasourceEntityOptional.get();

        // 数据源检查通过
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成  \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 检查脚本是否为空
        if (Strings.isEmpty(workRunContext.getScript())) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Sql内容为空 \n");
        }

        // 脚本检查通过
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 开始执行sql
        ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
        Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
        try (Connection connection = datasource.getConnection(connectInfo);
            Statement statement = connection.createStatement()) {

            statement.setQueryTimeout(1800);

            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始解析prql \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 去掉sql中的注释
            String sqlNoComment = sqlCommentService.removeSqlComment(workRunContext.getScript());

            // 解析上游参数
            String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);

            // 翻译sql中的系统变量
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

            // 翻译sql中的系统函数
            String script = sqlFunctionService.parseSqlFunction(parseValueSql);

            // 打印日志
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("执行PRQL: \n").append(script)
                .append(" \n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 解析sql
            String sql;
            try {
                sql = PrqlCompiler.toSql(script.replace(";", ""),
                    translateDBType(datasourceEntityOptional.get().getDbType()), true, true);
            } catch (NoClassDefFoundError error) {
                throw new Exception(error.getMessage());
            }

            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO)
                .append(String.format("prql转化完成: \n%s\n", sql));
            workInstance = updateInstance(workInstance, logBuilder);

            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行SQL \n");
            workInstance = updateInstance(workInstance, logBuilder);
            statement.execute(sql);

            // 记录结束执行时间
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("SQL执行成功  \n");
            workInstance = updateInstance(workInstance, logBuilder);

            ResultSet resultSet = statement.getResultSet();

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
                    metaList.add(String.valueOf(resultSet.getObject(i)));
                }
                result.add(metaList);
            }

            // 讲data转为json存到实例中
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功  \n");
            workInstance.setResultData(JSON.toJSONString(result));
            updateInstance(workInstance, logBuilder);
        } catch (Exception e) {

            log.error(e.getMessage(), e);
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMessage() + "\n");
        }
    }

    @Override
    protected void abort(WorkInstanceEntity workInstance) {

        Thread thread = WORK_THREAD.get(workInstance.getId());
        thread.interrupt();
    }

    public static String translateDBType(String dbType) {

        switch (dbType) {
            case DatasourceType.MYSQL:
                return "mysql";
            case DatasourceType.CLICKHOUSE:
                return "clickhouse";
            case DatasourceType.POSTGRE_SQL:
                return "postgres";
            case DatasourceType.H2:
                return "h2";
            default:
                throw new IsxAppException("当前数据库类型不支持");
        }
    }
}
