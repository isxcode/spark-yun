package com.isxcode.star.modules.work.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.backend.api.base.exceptions.WorkRunException;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.datasource.service.DatasourceBizService;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
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

  private final DatasourceBizService datasourceBizService;

  private final DatasourceRepository datasourceRepository;

  private final WorkInstanceRepository workInstanceRepository;

  public QuerySqlExecutor(
      DatasourceBizService datasourceBizService,
      DatasourceRepository datasourceRepository,
      WorkInstanceRepository workInstanceRepository,
      WorkflowInstanceRepository workflowInstanceRepository) {

    super(workInstanceRepository, workflowInstanceRepository);
    this.datasourceBizService = datasourceBizService;
    this.datasourceRepository = datasourceRepository;
    this.workInstanceRepository = workInstanceRepository;
  }

  public void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

    // 获取日志构造器
    StringBuilder logBuilder = workRunContext.getLogBuilder();

    // 检测数据源是否配置
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测运行环境 \n");
    if (Strings.isEmpty(workRunContext.getDatasourceId())) {
      throw new WorkRunException(
          LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源  \n");
    }

    // 检查数据源是否存在
    Optional<DatasourceEntity> datasourceEntityOptional =
        datasourceRepository.findById(workRunContext.getDatasourceId());
    if (!datasourceEntityOptional.isPresent()) {
      throw new WorkRunException(
          LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源  \n");
    }

    // 数据源检查通过
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成  \n");
    workInstance = updateInstance(workInstance, logBuilder);

    // 检查脚本是否为空
    if (Strings.isEmpty(workRunContext.getSqlScript())) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Sql内容为空 \n");
    }

    // 脚本检查通过
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
    workInstance = updateInstance(workInstance, logBuilder);

    // 开始执行sql
    try (Connection connection =
            datasourceBizService.getDbConnection(datasourceEntityOptional.get());
        Statement statement = connection.createStatement()) {

      // 清除注释
      String noCommentSql =
          workRunContext.getSqlScript().replaceAll("/\\*(?:.|[\\n\\r])*?\\*/|--.*", "");

      // 清除脚本中的脏数据
      List<String> sqls =
          Arrays.stream(noCommentSql.split(";"))
              .filter(e -> !Strings.isEmpty(e))
              .collect(Collectors.toList());

      // 执行每条sql，除了最后一条
      for (int i = 0; i < sqls.size() - 1; i++) {

        // 记录开始执行时间
        logBuilder
            .append(LocalDateTime.now())
            .append(WorkLog.SUCCESS_INFO)
            .append("开始执行SQL: ")
            .append(sqls.get(i))
            .append(" \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 执行sql
        statement.execute(sqls.get(i));

        // 记录结束执行时间
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("SQL执行成功  \n");
        workInstance = updateInstance(workInstance, logBuilder);
      }

      // 执行最后一句查询语句
      logBuilder
          .append(LocalDateTime.now())
          .append(WorkLog.SUCCESS_INFO)
          .append("执行查询SQL: ")
          .append(sqls.get(sqls.size() - 1))
          .append(" \n");
      workInstance = updateInstance(workInstance, logBuilder);

      // 执行查询sql
      ResultSet resultSet = statement.executeQuery(sqls.get(sqls.size() - 1));

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
          metaList.add(String.valueOf(resultSet.getObject(i)));
        }
        result.add(metaList);
      }

      // 讲data转为json存到实例中
      logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功  \n");
      workInstance.setSubmitLog(logBuilder.toString());
      workInstance.setResultData(JSON.toJSONString(result));
      workInstanceRepository.saveAndFlush(workInstance);
    } catch (Exception e) {

      log.error(e.getMessage());
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMessage() + "\n");
    }
  }

  @Override
  protected void abort(WorkInstanceEntity workInstance) {

    Thread thread = WORK_THREAD.get(workInstance.getId());
    thread.interrupt();
  }
}
