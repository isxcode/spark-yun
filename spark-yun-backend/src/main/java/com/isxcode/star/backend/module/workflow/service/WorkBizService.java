package com.isxcode.star.backend.module.workflow.service;

import static java.sql.DriverManager.getConnection;

import com.isxcode.star.api.pojos.work.req.AddWorkReq;
import com.isxcode.star.api.pojos.work.req.ConfigWorkReq;
import com.isxcode.star.api.pojos.work.res.GetWorkRes;
import com.isxcode.star.api.pojos.work.res.QueryWorkRes;
import com.isxcode.star.api.pojos.work.res.RunWorkRes;
import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import com.isxcode.star.backend.module.datasource.repository.DatasourceRepository;
import com.isxcode.star.backend.module.workflow.entity.WorkConfigEntity;
import com.isxcode.star.backend.module.workflow.entity.WorkEntity;
import com.isxcode.star.backend.module.workflow.mapper.WorkMapper;
import com.isxcode.star.backend.module.workflow.repository.WorkConfigRepository;
import com.isxcode.star.backend.module.workflow.repository.WorkRepository;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkBizService {

  private final WorkRepository workRepository;

  private final WorkConfigRepository workConfigRepository;

  private final DatasourceRepository datasourceRepository;

  private final WorkMapper workMapper;

  public void addWork(AddWorkReq addWorkReq) {

    // req 转 entity
    WorkEntity work = workMapper.addWorkReqToWorkEntity(addWorkReq);

    // 添加默认配置
    WorkConfigEntity workConfigEntity = new WorkConfigEntity();
    WorkConfigEntity save = workConfigRepository.save(workConfigEntity);
    work.setWorkConfigId(save.getId());

    work.setStatus("新建");
    work.setCreateDateTime(LocalDateTime.now());

    // 数据持久化
    workRepository.save(work);
  }

  public List<QueryWorkRes> queryWork(String workflowId) {

    List<WorkEntity> workEntities = workRepository.findAllByWorkflowId(workflowId);

    return workMapper.workEntityListToQueryWorkResList(workEntities);
  }

  public void delWork(String workId) {

    workRepository.deleteById(workId);
  }

  public RunWorkRes runWork(String workId) throws ClassNotFoundException {

    // 查询作业
    WorkEntity workEntity = workRepository.findById(workId).get();

    // 判断类型
    switch (workEntity.getType()) {
      case "executeSql":
        return executeSql(workEntity.getWorkConfigId());
      case "querySql":
        return querySql(workEntity.getWorkConfigId());
      default:
        return new RunWorkRes("执行失败", "作业类型暂不支持", null);
    }
  }

  public RunWorkRes executeSql(String workConfigId) throws ClassNotFoundException {

    // 查询配置
    WorkConfigEntity workConfigEntity = workConfigRepository.findById(workConfigId).get();

    // 获取数据源信息
    DatasourceEntity datasourceEntity =
        datasourceRepository.findById(workConfigEntity.getDatasourceId()).get();

    switch (datasourceEntity.getType()) {
      case "mysql":
        Class.forName("com.mysql.cj.jdbc.Driver");
        break;
      case "oracle":
        Class.forName("oracle.jdbc.driver.OracleDriver");
        break;
      case "sqlserver":
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        break;
      default:
        return new RunWorkRes("暂不支持数据源", "暂不支持数据源", null);
    }

    try (Connection connection =
            getConnection(
                datasourceEntity.getJdbcUrl(),
                datasourceEntity.getUsername(),
                datasourceEntity.getPasswd());
        Statement statement = connection.createStatement(); ) {
      statement.execute(workConfigEntity.getScript());
      return new RunWorkRes("提交成功", "执行成功", null);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new RunWorkRes("执行失败", e.getMessage(), null);
    }
  }

  public RunWorkRes querySql(String workConfigId) throws ClassNotFoundException {

    // 查询配置
    WorkConfigEntity workConfigEntity = workConfigRepository.findById(workConfigId).get();

    if (Strings.isEmpty(workConfigEntity.getDatasourceId())) {
      return new RunWorkRes("执行失败", "请先在配置中选择数据源", null);
    }

    // 获取数据源信息
    DatasourceEntity datasourceEntity =
        datasourceRepository.findById(workConfigEntity.getDatasourceId()).get();

    switch (datasourceEntity.getType()) {
      case "mysql":
        Class.forName("com.mysql.cj.jdbc.Driver");
        break;
      case "oracle":
        Class.forName("oracle.jdbc.driver.OracleDriver");
        break;
      case "sqlserver":
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        break;
      default:
        return new RunWorkRes("暂不支持数据源", "暂不支持数据源", null);
    }

    List<List<String>> result = new ArrayList<>();

    try (Connection connection =
            getConnection(
                datasourceEntity.getJdbcUrl(),
                datasourceEntity.getUsername(),
                datasourceEntity.getPasswd());
        Statement statement = connection.createStatement(); ) {
      ResultSet resultSet = statement.executeQuery(workConfigEntity.getScript());
      int columnCount = resultSet.getMetaData().getColumnCount();

      // 表头
      List<String> metaList = new ArrayList<>();
      for (int i = 1; i <= columnCount; i++) {
        metaList.add(resultSet.getMetaData().getColumnName(i));
      }
      result.add(metaList);

      // 数据
      while (resultSet.next()) {
        metaList = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
          metaList.add(String.valueOf(resultSet.getObject(i)));
        }
        result.add(metaList);
      }
      return new RunWorkRes("提交成功", "查询成功", result);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new RunWorkRes("查询失败", e.getMessage(), null);
    }
  }

  public void configWork(ConfigWorkReq configWorkReq) {

    WorkEntity workEntity = workRepository.findById(configWorkReq.getWorkId()).get();

    WorkConfigEntity workConfigEntity =
        workConfigRepository.findById(workEntity.getWorkConfigId()).get();

    if (!Strings.isEmpty(configWorkReq.getDatasourceId())) {
      workConfigEntity.setDatasourceId(configWorkReq.getDatasourceId());
    }

    if (!Strings.isEmpty(configWorkReq.getScript())) {
      workConfigEntity.setScript(configWorkReq.getScript());
    }

    workConfigRepository.save(workConfigEntity);
  }

  public GetWorkRes getWork(String workId) {

    WorkEntity workEntity = workRepository.findById(workId).get();

    WorkConfigEntity workConfigEntity =
        workConfigRepository.findById(workEntity.getWorkConfigId()).get();

    return workMapper.workEntityAndWorkConfigEntityToGetWorkRes(workEntity, workConfigEntity);
  }
}
