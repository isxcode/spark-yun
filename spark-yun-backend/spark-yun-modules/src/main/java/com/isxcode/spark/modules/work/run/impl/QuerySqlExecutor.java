package com.isxcode.spark.modules.work.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.datasource.constants.DatasourceConfig;
import com.isxcode.spark.api.datasource.dto.ConnectInfo;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.datasource.entity.DatasourceEntity;
import com.isxcode.spark.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.spark.modules.datasource.repository.DatasourceRepository;
import com.isxcode.spark.modules.datasource.service.DatasourceService;
import com.isxcode.spark.modules.datasource.source.DataSourceFactory;
import com.isxcode.spark.modules.datasource.source.Datasource;
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

import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.quartz.Scheduler;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class QuerySqlExecutor extends WorkExecutor {

    private final DatasourceRepository datasourceRepository;

    private final SqlCommentService sqlCommentService;

    private final SqlFunctionService sqlFunctionService;

    private final SqlValueService sqlValueService;

    private final DataSourceFactory dataSourceFactory;

    private final DatasourceMapper datasourceMapper;

    private final DatasourceService datasourceService;

    private final IsxAppProperties isxAppProperties;

    private final ServerProperties serverProperties;

    public QuerySqlExecutor(WorkInstanceRepository workInstanceRepository,
        WorkflowInstanceRepository workflowInstanceRepository, DatasourceRepository datasourceRepository,
        SqlCommentService sqlCommentService, SqlValueService sqlValueService, SqlFunctionService sqlFunctionService,
        AlarmService alarmService, DataSourceFactory dataSourceFactory, DatasourceMapper datasourceMapper,
        WorkEventRepository workEventRepository, Scheduler scheduler, Locker locker, WorkRepository workRepository,
        WorkRunJobFactory workRunJobFactory, WorkConfigRepository workConfigRepository,
        VipWorkVersionRepository vipWorkVersionRepository, WorkService workService, DatasourceService datasourceService,
        IsxAppProperties isxAppProperties, ServerProperties serverProperties) {

        super(alarmService, scheduler, locker, workRepository, workInstanceRepository, workflowInstanceRepository,
            workEventRepository, workRunJobFactory, sqlFunctionService, workConfigRepository, vipWorkVersionRepository,
            workService);
        this.datasourceRepository = datasourceRepository;
        this.sqlCommentService = sqlCommentService;
        this.sqlValueService = sqlValueService;
        this.sqlFunctionService = sqlFunctionService;
        this.dataSourceFactory = dataSourceFactory;
        this.datasourceMapper = datasourceMapper;
        this.datasourceService = datasourceService;
        this.isxAppProperties = isxAppProperties;
        this.serverProperties = serverProperties;
    }

    @Override
    public String getWorkType() {
        return WorkType.QUERY_JDBC_SQL;
    }

    @Override
    protected String execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance,
        WorkEventEntity workEvent) {

        // 获取日志
        StringBuilder logBuilder = new StringBuilder(workInstance.getSubmitLog());

        // 打印首行日志，防止前端卡顿
        if (workEvent.getEventProcess() == 0) {
            logBuilder.append(startLog("检测数据源开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检测数据源
        if (workEvent.getEventProcess() == 1) {

            // 检测数据源是否配置
            if (Strings.isEmpty(workRunContext.getDatasourceId())) {
                throw errorLogException("检测数据源异常: 未配置有效数据源");
            }

            // 检查数据源是否存在
            datasourceRepository.findById(workRunContext.getDatasourceId())
                .orElseThrow(() -> errorLogException("检测数据源异常: 数据源不存在"));

            // 保存日志
            logBuilder.append(endLog("检测数据源完成"));
            logBuilder.append(startLog("检测SQL脚本开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 检测SQL脚本
        if (workEvent.getEventProcess() == 2) {

            // 检查脚本是否为空
            if (Strings.isEmpty(workRunContext.getScript())) {
                throw errorLogException("检测SQL脚本异常 : SQL内容不能为空");
            }

            // 去掉sql中的注释
            String sqlNoComment = sqlCommentService.removeSqlComment(workRunContext.getScript());

            // 解析上游参数
            String jsonPathSql = parseJsonPath(sqlNoComment, workInstance);

            // 翻译sql中的系统变量
            String parseValueSql = sqlValueService.parseSqlValue(jsonPathSql);

            // 翻译sql中的系统函数
            String script = sqlFunctionService.parseSqlFunction(parseValueSql);

            // 保存上下文
            workRunContext.setScript(script);

            // 保存日志
            logBuilder.append(endLog("检测SQL脚本完成"));
            logBuilder.append(startLog("执行SQL脚本开始"));
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        // 执行SQL脚本
        if (workEvent.getEventProcess() == 3) {

            // 记录当前线程
            WORK_THREAD.put(workEvent.getId(), Thread.currentThread());
            workRunContext.setIsxAppName(isxAppProperties.getAppName());
            updateWorkEvent(workEvent, workRunContext);

            // 获取上下文参数
            String script = workRunContext.getScript();
            String datasourceId = workRunContext.getDatasourceId();

            // 获取数据源
            DatasourceEntity datasourceEntity = datasourceService.getDatasource(datasourceId);
            ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
            Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
            connectInfo.setLoginTimeout(5);

            try (Connection connection = datasource.getConnection(connectInfo);
                Statement statement = connection.createStatement()) {

                statement.setQueryTimeout(1800);

                // 清除脚本中的脏数据
                List<String> sqls =
                    Arrays.stream(script.split(";")).filter(Strings::isNotBlank).collect(Collectors.toList());

                // 执行每条sql，除了最后一条
                for (int i = 0; i < sqls.size() - 1; i++) {

                    // 记录开始执行时间
                    logBuilder.append(startLog("执行开始"));
                    logBuilder.append("> ").append(sqls.get(i)).append(" \n");
                    workInstance = updateInstance(workInstance, logBuilder);

                    // 执行sql
                    statement.execute(sqls.get(i));

                    // 记录结束执行时间
                    logBuilder.append(endLog("执行完成"));
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

                    logBuilder.append(startLog("检测SQL返回条数开始"));
                    logBuilder.append("> ").append(countSql).append(" \n");

                    workInstance = updateInstance(workInstance, logBuilder);
                    ResultSet countResultSet = statement.executeQuery(countSql);
                    while (countResultSet.next()) {
                        if (countResultSet.getInt(1) > DatasourceConfig.LIMIT_NUMBER) {
                            throw errorLogException(
                                "检测SQL返回条数异常 : 条数大于" + DatasourceConfig.LIMIT_NUMBER + "条，请添加条数查询限制");
                        }
                    }
                }

                // 执行最后一句查询语句
                logBuilder.append(endLog("检测SQL返回条数完成，总条数不超过" + DatasourceConfig.LIMIT_NUMBER + "条"));
                logBuilder.append(startLog("执行开始"));
                logBuilder.append("> ").append(lastSql).append(" \n");
                workInstance = updateInstance(workInstance, logBuilder);

                // 开始执行
                ResultSet resultSet = statement.executeQuery(lastSql);

                // 记录结束执行时间
                logBuilder.append(endLog("执行完成"));
                logBuilder.append(startLog("保存数据开始"));

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

                // 保存数据
                workInstance.setResultData(JSON.toJSONString(result));

                // 保存日志
                logBuilder.append(endLog("保存数据完成"));
                updateInstance(workInstance, logBuilder);
            } catch (WorkRunException | IsxAppException e) {
                throw errorLogException(log + "\n" + e.getMsg());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw errorLogException(log + "\n" + e.getMessage());
            }

            // 保存日志
            return updateWorkEventAndInstance(workInstance, logBuilder, workEvent, workRunContext);
        }

        return InstanceStatus.SUCCESS;
    }

    @Override
    protected boolean abort(WorkInstanceEntity workInstance, WorkEventEntity workEvent) {

        // 还未提交
        if (workEvent.getEventProcess() < 3) {
            return true;
        }

        // 运行完毕
        if (workEvent.getEventProcess() > 3) {
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
