package com.isxcode.star.modules.work.run.impl;

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
import org.springframework.stereotype.Service;

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

    private final SqlValueService sqlValueService;

    private final SqlFunctionService sqlFunctionService;

    private final DataSourceFactory dataSourceFactory;

    private final DatasourceMapper datasourceMapper;

    public ExecuteSqlExecutor(WorkInstanceRepository workInstanceRepository, DatasourceRepository datasourceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, SqlCommentService sqlCommentService,
        SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService alarmService,
        DataSourceFactory dataSourceFactory, DatasourceMapper datasourceMapper) {

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
        return WorkType.EXECUTE_JDBC_SQL;
    }

    @Override
    public void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

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

        // 开始执行作业
        ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
        Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
        connectInfo.setLoginTimeout(5);
        try (Connection connection = datasource.getConnection(connectInfo);
            Statement statement = connection.createStatement()) {

            statement.setQueryTimeout(1800);

            // 去掉sql中的注释
            String sqlNoComment = sqlCommentService.removeSqlComment(workRunContext.getScript());

            // 解析上游参数
            String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);

            // 翻译sql中的系统变量
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

            // 翻译sql中的系统函数
            String script = sqlFunctionService.parseSqlFunction(parseValueSql);

            // 清除脚本中的脏数据
            List<String> sqls =
                Arrays.stream(script.split(";")).filter(Strings::isNotBlank).collect(Collectors.toList());

            // 逐条执行sql
            for (String sql : sqls) {

                // 记录开始执行时间
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行SQL: \n").append(sql)
                    .append(" \n");
                workInstance = updateInstance(workInstance, logBuilder);

                // 执行sql
                statement.execute(sql);

                // 记录结束执行时间
                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("SQL执行成功  \n");
                workInstance = updateInstance(workInstance, logBuilder);
            }
        } catch (WorkRunException | IsxAppException e) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMsg() + "\n");
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
}
