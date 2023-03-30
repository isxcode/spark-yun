package com.isxcode.star.backend.module.datasource.service;

import com.isxcode.star.api.pojos.datasource.req.AddDatasourceReq;
import com.isxcode.star.api.pojos.datasource.req.TestConnectReq;
import com.isxcode.star.api.pojos.datasource.res.QueryDatasourceRes;
import com.isxcode.star.api.pojos.datasource.res.TestConnectRes;
import com.isxcode.star.api.pojos.node.req.AddNodeReq;
import com.isxcode.star.api.pojos.node.res.QueryNodeRes;
import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import com.isxcode.star.backend.module.datasource.mapper.DatasourceMapper;
import com.isxcode.star.backend.module.datasource.repository.DatasourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.sql.DriverManager.getConnection;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
public class DatasourceBizService {

  private final DatasourceRepository datasourceRepository;

  private final DatasourceMapper datasourceMapper;

  public void addDatasource(AddDatasourceReq addDatasourceReq) {

    // req 转 entity
    DatasourceEntity datasource = datasourceMapper.addDatasourceReqToDatasourceEntity(addDatasourceReq);
    datasource.setCheckDate(LocalDateTime.now());
    datasource.setStatus("未检测");

    // 数据持久化
    datasourceRepository.save(datasource);
  }

  public List<QueryDatasourceRes> queryDatasource() {

    List<DatasourceEntity> datasourceEntities = datasourceRepository.findAll();

    return datasourceMapper.datasourceEntityListToQueryDatasourceResList(datasourceEntities);
  }

  public void delDatasource(String datasourceId) {

    datasourceRepository.deleteById(datasourceId);
  }

  public TestConnectRes testConnect(TestConnectReq testConnectReq) throws ClassNotFoundException {

    DatasourceEntity datasource = datasourceRepository.findById(testConnectReq.getDatasourceId()).get();

    switch (datasource.getType()) {
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
        throw new RuntimeException("暂不支持数据源");
    }

    try (Connection connection = getConnection(datasource.getJdbcUrl(), datasource.getUsername(), datasource.getPasswd());) {
      if (connection != null) {
        datasource.setStatus("连接成功");
        datasource.setCheckDate(LocalDateTime.now());
        return new TestConnectRes(true, "连接成功");
      } else {
        datasource.setStatus("连接失败");
        datasource.setCheckDate(LocalDateTime.now());
        return new TestConnectRes(false, "连接失败");
      }
    } catch (Exception e) {
      e.printStackTrace();
      datasource.setStatus("连接失败");
      datasource.setCheckDate(LocalDateTime.now());
      return new TestConnectRes(false, e.getMessage());
    }
  }
}
