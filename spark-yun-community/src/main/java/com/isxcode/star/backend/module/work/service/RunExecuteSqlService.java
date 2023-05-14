package com.isxcode.star.backend.module.work.service;

import com.isxcode.star.api.constants.work.WorkLog;
import com.isxcode.star.api.constants.work.instance.InstanceStatus;
import com.isxcode.star.api.exception.WorkRunException;
import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import com.isxcode.star.backend.module.datasource.repository.DatasourceRepository;
import com.isxcode.star.backend.module.datasource.service.DatasourceBizService;
import com.isxcode.star.backend.module.work.instance.entity.WorkInstanceEntity;
import com.isxcode.star.backend.module.work.instance.repository.WorkInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.Optional;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RunExecuteSqlService {

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
    logBuilder.append(WorkLog.SUCCESS_INFO + "提交作业完成 \n");

    instance.setStatus(InstanceStatus.RUNNING);
    instance.setSubmitLog(logBuilder.toString());
    instance.setExecStartDateTime(new Date());
    workInstanceRepository.saveAndFlush(instance);

    try {
      executeSql(datasourceId, sqlScript, instance, logBuilder);
      instance.setStatus(InstanceStatus.SUCCESS);
      instance.setSubmitLog(logBuilder.toString());
      instance.setExecEndDateTime(new Date());
      workInstanceRepository.saveAndFlush(instance);
    } catch (WorkRunException e) {
      log.error(e.getMessage());
      logBuilder.append(WorkLog.ERROR_INFO + e.getMessage() + "\n");
      instance.setStatus(InstanceStatus.FAIL);
      instance.setSubmitLog(logBuilder.toString());
      workInstanceRepository.saveAndFlush(instance);
    }
  }

  public void executeSql(String datasourceId, String sqlScript, WorkInstanceEntity instance, StringBuilder logBuilder) {

    // 检测数据源是否存在
    logBuilder.append(WorkLog.SUCCESS_INFO + "开始检测运行环境 \n");
    Optional<DatasourceEntity> datasourceEntityOptional = datasourceRepository.findById(datasourceId);
    if(!datasourceEntityOptional.isPresent()){
      throw new WorkRunException(WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源  \n");
    }
    logBuilder.append(WorkLog.SUCCESS_INFO + "检测运行环境完成  \n");
    instance.setSubmitLog(logBuilder.toString());
    workInstanceRepository.saveAndFlush(instance);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    // 开始执行作业
    logBuilder.append(WorkLog.SUCCESS_INFO + "开始执行作业 \n");
    try (Connection connection = datasourceBizService.getDbConnection(datasourceEntityOptional.get());
         Statement statement = connection.createStatement();) {

      String regex = "/\\*(?:.|[\\n\\r])*?\\*/|--.*";
      String noCommentSql = sqlScript.replaceAll(regex, "");
      String realSql = noCommentSql.replace("\n", " ");
      for (String sql : realSql.split(";")) {
        logBuilder.append(WorkLog.SUCCESS_INFO + "开始执行SQL: " + sql + " \n");
        if (!Strings.isEmpty(sql)) {
          statement.execute(sql);
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        logBuilder.append(WorkLog.SUCCESS_INFO + "SQL执行成功  \n");
        instance.setSubmitLog(logBuilder.toString());
        workInstanceRepository.saveAndFlush(instance);
      }
      logBuilder.append(WorkLog.SUCCESS_INFO + "[SUCCESS] \n");
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new WorkRunException(WorkLog.ERROR_INFO + e.getMessage() + "\n");
    }
  }
}
