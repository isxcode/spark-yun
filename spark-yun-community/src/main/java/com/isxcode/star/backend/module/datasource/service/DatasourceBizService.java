package com.isxcode.star.backend.module.datasource.service;

import static java.sql.DriverManager.getConnection;

import com.isxcode.star.api.constants.datasource.DatasourceStatus;
import com.isxcode.star.api.constants.datasource.DatasourceDriver;
import com.isxcode.star.api.constants.datasource.DatasourceType;
import com.isxcode.star.api.pojos.datasource.req.DasAddDatasourceReq;
import com.isxcode.star.api.pojos.datasource.req.DasQueryDatasourceReq;
import com.isxcode.star.api.pojos.datasource.req.DasUpdateDatasourceReq;
import com.isxcode.star.api.pojos.datasource.res.DasGetConnectLogRes;
import com.isxcode.star.api.pojos.datasource.res.DasQueryDatasourceRes;
import com.isxcode.star.api.pojos.datasource.res.DasTestConnectRes;
import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import com.isxcode.star.backend.module.datasource.mapper.DatasourceMapper;
import com.isxcode.star.backend.module.datasource.repository.DatasourceRepository;
import com.isxcode.star.api.exceptions.SparkYunException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DatasourceBizService {

  private final DatasourceRepository datasourceRepository;

  private final DatasourceMapper datasourceMapper;

  private final SparkYunProperties sparkYunProperties;

  public void addDatasource(DasAddDatasourceReq dasAddDatasourceReq) {

    DatasourceEntity datasource = datasourceMapper.dasAddDatasourceReqToDatasourceEntity(dasAddDatasourceReq);

    // 密码对成加密
    datasource.setPasswd(AesUtils.encrypt(sparkYunProperties.getAesSlat(), datasource.getPasswd()));

    datasource.setCheckDateTime(LocalDateTime.now());
    datasource.setStatus(DatasourceStatus.UN_CHECK);
    datasourceRepository.save(datasource);
  }

  public void updateDatasource(DasUpdateDatasourceReq dasAddDatasourceReq) {

    Optional<DatasourceEntity> datasourceEntityOptional = datasourceRepository.findById(dasAddDatasourceReq.getId());
    if (!datasourceEntityOptional.isPresent()) {
      throw new SparkYunException("数据源不存在");
    }

    DatasourceEntity datasource = datasourceMapper.dasUpdateDatasourceReqToDatasourceEntity(dasAddDatasourceReq, datasourceEntityOptional.get());

    // 密码对成加密
    datasource.setPasswd(AesUtils.encrypt(sparkYunProperties.getAesSlat(), datasource.getPasswd()));

    datasource.setCheckDateTime(LocalDateTime.now());
    datasource.setStatus(DatasourceStatus.UN_CHECK);
    datasourceRepository.save(datasource);
  }

  public Page<DasQueryDatasourceRes> queryDatasource(DasQueryDatasourceReq dasQueryDatasourceReq) {

    Page<DatasourceEntity> datasourceEntityPage = datasourceRepository.searchAll(dasQueryDatasourceReq.getSearchKeyWord(), PageRequest.of(dasQueryDatasourceReq.getPage(), dasQueryDatasourceReq.getPageSize()));

    return datasourceMapper.datasourceEntityToQueryDatasourceResPage(datasourceEntityPage);
  }


  public Connection getDbConnection(DatasourceEntity datasource) throws SQLException {
    loadDriverClass(datasource.getDbType());

    DriverManager.setLoginTimeout(10);
    return DriverManager.getConnection(datasource.getJdbcUrl(), datasource.getUsername(), AesUtils.decrypt(sparkYunProperties.getAesSlat(), datasource.getPasswd()));
  }

  public void delDatasource(String datasourceId) {

    datasourceRepository.deleteById(datasourceId);
  }

  public void loadDriverClass(String datasourceType) {

    try {
      switch (datasourceType) {
        case DatasourceType.MYSQL:
          Class.forName(DatasourceDriver.MYSQL_DRIVER);
          break;
        case DatasourceType.ORACLE:
          Class.forName(DatasourceDriver.ORACLE_DRIVER);
          break;
        case DatasourceType.SQL_SERVER:
          Class.forName(DatasourceDriver.SQL_SERVER_DRIVER);
          break;
        case DatasourceType.DORIS:
          Class.forName(DatasourceDriver.DORIS_DRIVER);
          break;
        case DatasourceType.POSTGRE_SQL:
          Class.forName(DatasourceDriver.POSTGRE_SQL_DRIVER);
          break;
        case DatasourceType.CLICKHOUSE:
          Class.forName(DatasourceDriver.CLICKHOUSE_DRIVER);
          break;
        case DatasourceType.HANA_SAP:
          Class.forName(DatasourceDriver.HANA_SAP_DRIVER);
          break;
        case DatasourceType.HIVE:
          Class.forName(DatasourceDriver.HIVE_DRIVER);
          break;
        case DatasourceType.DM:
          Class.forName(DatasourceDriver.DM_DRIVER);
          break;
        case DatasourceType.OCEAN_BASE:
          Class.forName(DatasourceDriver.OCEAN_BASE_DRIVER);
          break;
        case DatasourceType.TIDB:
          Class.forName(DatasourceDriver.TIDB_DRIVER);
          break;
        case DatasourceType.DB2:
          Class.forName(DatasourceDriver.DB2_DRIVER);
          break;
        default:
          throw new SparkYunException("数据源暂不支持");
      }
    } catch (ClassNotFoundException e) {
      log.error(e.getMessage());
      throw new SparkYunException("找不到对应驱动");
    }

  }

  public DasTestConnectRes testConnect(String datasourceId) {

    // 获取数据源
    Optional<DatasourceEntity> datasourceEntityOptional = datasourceRepository.findById(datasourceId);
    if (!datasourceEntityOptional.isPresent()) {
      throw new SparkYunException("数据源不存在");
    }
    DatasourceEntity datasource = datasourceEntityOptional.get();

    // 加载驱动
    loadDriverClass(datasource.getDbType());

    // 测试连接
    datasource.setCheckDateTime(LocalDateTime.now());

    try (Connection connection = getDbConnection(datasource)) {
      if (connection != null) {
        datasource.setStatus(DatasourceStatus.ACTIVE);
        datasource.setConnectLog("测试连接成功！");
        datasourceRepository.save(datasource);
        return new DasTestConnectRes(true, "连接成功");
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      datasource.setStatus(DatasourceStatus.FAIL);
      datasource.setConnectLog("测试连接失败：" + e.getMessage());
      datasourceRepository.save(datasource);
      return new DasTestConnectRes(false, e.getMessage());
    }
    return new DasTestConnectRes(false, "连接失败");
  }

  public DasGetConnectLogRes getConnectLog(String datasourceId) {

    // 获取数据源
    Optional<DatasourceEntity> datasourceEntityOptional = datasourceRepository.findById(datasourceId);
    if (!datasourceEntityOptional.isPresent()) {
      throw new SparkYunException("数据源不存在");
    }
    DatasourceEntity datasource = datasourceEntityOptional.get();

    DasGetConnectLogRes dasGetConnectLogRes = new DasGetConnectLogRes();
    dasGetConnectLogRes.setConnectLog(datasource.getConnectLog());
    return dasGetConnectLogRes;
  }
}
