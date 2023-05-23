package com.isxcode.star.backend.module.work.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.work.WorkLog;
import com.isxcode.star.api.constants.work.instance.InstanceStatus;
import com.isxcode.star.api.exceptions.WorkRunException;
import com.isxcode.star.backend.module.datasource.DatasourceEntity;
import com.isxcode.star.backend.module.datasource.DatasourceRepository;
import com.isxcode.star.backend.module.datasource.DatasourceBizService;
import com.isxcode.star.backend.module.work.instance.entity.WorkInstanceEntity;
import com.isxcode.star.backend.module.work.instance.repository.WorkInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RunQuerySqlService {
  private final DatasourceBizService datasourceBizService;

  private final DatasourceRepository datasourceRepository;

  private final WorkInstanceRepository workInstanceRepository;

  @Async("sparkYunWorkThreadPool")
  public void run(String datasourceId, String sqlScript, String instanceId, String tenantId, String userId) {

    USER_ID.set(userId);
    TENANT_ID.set(tenantId);

    Optional<WorkInstanceEntity> instanceEntityOptional = workInstanceRepository.findById(instanceId);
    if (!instanceEntityOptional.isPresent()) {
      return;
    }
    WorkInstanceEntity instance = instanceEntityOptional.get();

    StringBuilder logBuilder = new StringBuilder();
    logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "提交作业完成 \n");

    instance.setStatus(InstanceStatus.RUNNING);
    instance.setSubmitLog(logBuilder.toString());
    instance.setExecStartDateTime(new Date());
    workInstanceRepository.saveAndFlush(instance);

    try {
      executeQuerySql(datasourceId, sqlScript, instance, logBuilder);
      instance.setStatus(InstanceStatus.SUCCESS);
      instance.setSubmitLog(logBuilder.toString());
      instance.setExecEndDateTime(new Date());
      workInstanceRepository.saveAndFlush(instance);
    } catch (WorkRunException e) {
      log.error(e.getMsg());
      logBuilder.append(e.getMsg());
      logBuilder.append(LocalDateTime.now() + WorkLog.ERROR_INFO + "提交失败 \n");
      instance.setStatus(InstanceStatus.FAIL);
      instance.setExecEndDateTime(new Date());
      instance.setSubmitLog(logBuilder.toString());
      workInstanceRepository.saveAndFlush(instance);
    }
  }

  public void executeQuerySql(String datasourceId, String sqlScript, WorkInstanceEntity instance, StringBuilder logBuilder) {

    // 检测数据源是否存在
    logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始检测运行环境 \n");
    if (Strings.isEmpty(datasourceId)) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源  \n");
    }
    Optional<DatasourceEntity> datasourceEntityOptional = datasourceRepository.findById(datasourceId);
    if (!datasourceEntityOptional.isPresent()) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源  \n");
    }

    // 检测完成同步日志
    logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "检测运行环境完成  \n");
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);

    // 开始执行作业
    logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始执行作业 \n");

    if (Strings.isEmpty(sqlScript)) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Sql内容为空 \n");
    }

    List<List<String>> result = new ArrayList<>();
    try (Connection connection = datasourceBizService.getDbConnection(datasourceEntityOptional.get());
         Statement statement = connection.createStatement()) {

      String regex = "/\\*(?:.|[\\n\\r])*?\\*/|--.*";
      String noCommentSql = sqlScript.replaceAll(regex, "");
      String realSql = noCommentSql.replace("\n", " ");
      String[] sqls = realSql.split(";");

      // 执行每条sql
      for (int i = 0; i < sqls.length - 1; i++) {
        logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始执行SQL: " + sqls[i] + " \n");
        if (!Strings.isEmpty(sqls[i])) {
          statement.execute(sqls[i]);
        }
        logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "SQL执行成功  \n");
        instance.setSubmitLog(logBuilder.toString());
        workInstanceRepository.saveAndFlush(instance);
      }

      // 执行最后一句查询语句
      logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始查询SQL: " + sqls[sqls.length - 1] + " \n");
      ResultSet resultSet = statement.executeQuery(sqls[sqls.length - 1]);
      logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "查询SQL执行成功  \n");
      instance.setSubmitLog(logBuilder.toString());
      workInstanceRepository.saveAndFlush(instance);

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
      instance.setResultData(JSON.toJSONString(result));
      workInstanceRepository.saveAndFlush(instance);
      logBuilder.append(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "[SUCCESS] \n");
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMessage() + "\n");
    }
  }
}
