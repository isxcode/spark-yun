package com.isxcode.star.modules.datasource.service;

import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.api.datasource.constants.DatasourceStatus;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.pojos.req.*;
import com.isxcode.star.api.datasource.pojos.res.GetConnectLogRes;
import com.isxcode.star.api.datasource.pojos.res.PageDatasourceRes;
import com.isxcode.star.api.datasource.pojos.res.TestConnectRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
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

  private final AesUtils aesUtils;

  public void addDatasource(AddDatasourceReq dasAddDatasourceReq) {

    DatasourceEntity datasource =
        datasourceMapper.dasAddDatasourceReqToDatasourceEntity(dasAddDatasourceReq);

    // 密码对成加密
    datasource.setPasswd(aesUtils.encrypt(datasource.getPasswd()));

    datasource.setCheckDateTime(LocalDateTime.now());
    datasource.setStatus(DatasourceStatus.UN_CHECK);
    datasourceRepository.save(datasource);
  }

  public void updateDatasource(UpdateDatasourceReq dasAddDatasourceReq) {

    Optional<DatasourceEntity> datasourceEntityOptional =
        datasourceRepository.findById(dasAddDatasourceReq.getId());
    if (!datasourceEntityOptional.isPresent()) {
      throw new IsxAppException("数据源不存在");
    }

    DatasourceEntity datasource =
        datasourceMapper.dasUpdateDatasourceReqToDatasourceEntity(
            dasAddDatasourceReq, datasourceEntityOptional.get());

    // 密码对成加密
    datasource.setPasswd(aesUtils.encrypt(datasource.getPasswd()));

    datasource.setCheckDateTime(LocalDateTime.now());
    datasource.setStatus(DatasourceStatus.UN_CHECK);
    datasourceRepository.save(datasource);
  }

  public Page<PageDatasourceRes> pageDatasource(PageDatasourceReq dasQueryDatasourceReq) {

    Page<DatasourceEntity> datasourceEntityPage =
        datasourceRepository.searchAll(
            dasQueryDatasourceReq.getSearchKeyWord(),
            PageRequest.of(dasQueryDatasourceReq.getPage(), dasQueryDatasourceReq.getPageSize()));

    return datasourceMapper.datasourceEntityToQueryDatasourceResPage(datasourceEntityPage);
  }

  public Connection getDbConnection(DatasourceEntity datasource) throws SQLException {
    loadDriverClass(datasource.getDbType());

    DriverManager.setLoginTimeout(10);
    return DriverManager.getConnection(
        datasource.getJdbcUrl(),
        datasource.getUsername(),
        aesUtils.decrypt(datasource.getPasswd()));
  }

  public void deleteDatasource(DeleteDatasourceReq deleteDatasourceReq) {

    datasourceRepository.deleteById(deleteDatasourceReq.getDatasourceId());
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
        case DatasourceType.OCEANBASE:
          Class.forName(DatasourceDriver.OCEAN_BASE_DRIVER);
          break;
        case DatasourceType.TIDB:
          Class.forName(DatasourceDriver.TIDB_DRIVER);
          break;
        case DatasourceType.DB2:
          Class.forName(DatasourceDriver.DB2_DRIVER);
          break;
        case DatasourceType.STAR_ROCKS:
          Class.forName(DatasourceDriver.STAR_ROCKS_DRIVER);
          break;
        default:
          throw new IsxAppException("数据源暂不支持");
      }
    } catch (ClassNotFoundException e) {
      log.error(e.getMessage());
      throw new IsxAppException("找不到对应驱动");
    }
  }

  public TestConnectRes testConnect(GetConnectLogReq testConnectReq) {

    // 获取数据源
    Optional<DatasourceEntity> datasourceEntityOptional =
        datasourceRepository.findById(testConnectReq.getDatasourceId());
    if (!datasourceEntityOptional.isPresent()) {
      throw new IsxAppException("数据源不存在");
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
        return new TestConnectRes(true, "连接成功");
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      datasource.setStatus(DatasourceStatus.FAIL);
      datasource.setConnectLog("测试连接失败：" + e.getMessage());
      datasourceRepository.save(datasource);
      return new TestConnectRes(false, e.getMessage());
    }
    return new TestConnectRes(false, "连接失败");
  }

  public GetConnectLogRes getConnectLog(GetConnectLogReq getConnectLogReq) {

    // 获取数据源
    Optional<DatasourceEntity> datasourceEntityOptional =
        datasourceRepository.findById(getConnectLogReq.getDatasourceId());
    if (!datasourceEntityOptional.isPresent()) {
      throw new IsxAppException("数据源不存在");
    }
    DatasourceEntity datasource = datasourceEntityOptional.get();

    GetConnectLogRes dasGetConnectLogRes = new GetConnectLogRes();
    dasGetConnectLogRes.setConnectLog(datasource.getConnectLog());
    return dasGetConnectLogRes;
  }
}
