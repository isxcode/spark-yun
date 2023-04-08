package com.isxcode.star.backend.module.datasource.service;

import static java.sql.DriverManager.getConnection;

import com.isxcode.star.api.constants.DatasourceType;
import com.isxcode.star.api.pojos.datasource.req.DasAddDatasourceReq;
import com.isxcode.star.api.pojos.datasource.req.DasQueryDatasourceReq;
import com.isxcode.star.api.pojos.datasource.req.DasTestConnectReq;
import com.isxcode.star.api.pojos.datasource.res.DasQueryDatasourceRes;
import com.isxcode.star.api.pojos.datasource.res.DasTestConnectRes;
import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import com.isxcode.star.backend.module.datasource.mapper.DatasourceMapper;
import com.isxcode.star.backend.module.datasource.repository.DatasourceRepository;
import com.isxcode.star.common.exception.SparkYunException;
import java.sql.Connection;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/** 数据源模块service. */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DatasourceBizService {

  private final DatasourceRepository datasourceRepository;

  private final DatasourceMapper datasourceMapper;

  /** 添加数据源. */
  public void addDatasource(DasAddDatasourceReq dasAddDatasourceReq) {

    DatasourceEntity datasource =
        datasourceMapper.dasAddDatasourceReqToDatasourceEntity(dasAddDatasourceReq);

    datasourceRepository.save(datasource);
  }

  /** 查询数据源. */
  public Page<DasQueryDatasourceRes> queryDatasource(DasQueryDatasourceReq dasQueryDatasourceReq) {

    Page<DatasourceEntity> datasourceEntityPage =
        datasourceRepository.findAll(
            PageRequest.of(dasQueryDatasourceReq.getPage(), dasQueryDatasourceReq.getPageSize()));

    return datasourceMapper.datasourceEntityListToQueryDatasourceResList(datasourceEntityPage);
  }

  /** 删除数据源. */
  public void delDatasource(String datasourceId) {

    datasourceRepository.deleteById(datasourceId);
  }

  /** 数据源连接测试. */
  public DasTestConnectRes testConnect(DasTestConnectReq dasTestConnectRes) {

    // 获取数据源
    Optional<DatasourceEntity> datasourceEntityOptional =
        datasourceRepository.findById(dasTestConnectRes.getDatasourceId());
    if (!datasourceEntityOptional.isPresent()) {
      throw new SparkYunException("数据源不存在");
    }
    DatasourceEntity datasource = datasourceEntityOptional.get();

    // 获取class
    switch (datasource.getDatasourceType()) {
      case DatasourceType.MYSQL:
        try {
          Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
          log.error(e.getMessage());
          throw new SparkYunException("找不到mysql驱动");
        }
        break;
      case DatasourceType.ORACLE:
        try {
          Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
          log.error(e.getMessage());
          throw new SparkYunException("找不到oracle驱动");
        }
        break;
      case DatasourceType.SQL_SERVER:
        try {
          Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
          log.error(e.getMessage());
          throw new SparkYunException("找不到sqlServer驱动");
        }
        break;
      default:
        throw new SparkYunException("数据源暂不支持");
    }

    try (Connection connection =
        getConnection(
            datasource.getJdbcUrl(), datasource.getUsername(), datasource.getPasswd()); ) {
      if (connection != null) {
        return new DasTestConnectRes(true, "连接成功");
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      return new DasTestConnectRes(false, e.getMessage());
    }

    return new DasTestConnectRes(false, "连接失败");
  }
}
