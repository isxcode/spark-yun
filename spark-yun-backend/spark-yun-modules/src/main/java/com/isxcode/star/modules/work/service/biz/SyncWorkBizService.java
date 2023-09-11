package com.isxcode.star.modules.work.service.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.isxcode.star.api.work.pojos.req.GetDataSourceColumnsReq;
import com.isxcode.star.api.work.pojos.req.GetDataSourceTablesReq;
import com.isxcode.star.api.work.pojos.req.GetSyncWorkConfigReq;
import com.isxcode.star.api.work.pojos.req.SaveSyncWorkConfigReq;
import com.isxcode.star.api.work.pojos.res.GetDataSourceColumnsRes;
import com.isxcode.star.api.work.pojos.res.GetDataSourceTablesRes;
import com.isxcode.star.api.work.pojos.res.GetSyncWorkConfigRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.connection.JDBCConnection;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.work.entity.SyncWorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.mapper.SyncWorkConfigMapper;
import com.isxcode.star.modules.work.repository.SyncWorkConfigRepository;
import com.isxcode.star.modules.work.repository.WorkRepository;
import com.isxcode.star.modules.work.service.SyncWorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SyncWorkBizService {

	private final WorkRepository workRepository;

	private final SyncWorkConfigRepository syncWorkConfigRepository;

  private final SyncWorkConfigMapper syncWorkConfigMapper;

  private final DatasourceRepository datasourceRepository;

  private final SyncWorkService syncWorkService;

  private final AesUtils aesUtils;

	public GetSyncWorkConfigRes getSyncWorkConfig(GetSyncWorkConfigReq getSyncWorkConfigReq) {

		Optional<SyncWorkConfigEntity> syncWorkConfigEntityOptional = Optional.ofNullable(syncWorkConfigRepository.findByWorkId(getSyncWorkConfigReq.getWorkId()));
		if (!syncWorkConfigEntityOptional.isPresent()) {
			throw new IsxAppException("作业异常，请联系开发者");
		}

    GetSyncWorkConfigRes getSyncWorkConfigRes = syncWorkConfigMapper
      .syncWorkConfigEntityToGetSyncWorkConfigRes(syncWorkConfigEntityOptional.get());
    getSyncWorkConfigRes.setColumMapping(JSON.parseObject(getSyncWorkConfigRes.getColumMapping().toString(),
      new TypeReference<HashMap<String, List<String>>>() {
      }));
    return getSyncWorkConfigRes;
	}

	public void saveSyncWorkConfig(SaveSyncWorkConfigReq saveSyncWorkConfigReq) {

		Optional<WorkEntity> workEntityOptional = workRepository.findById(saveSyncWorkConfigReq.getWorkId());
		if (!workEntityOptional.isPresent()) {
			throw new IsxAppException("作业不存在");
		}

    SyncWorkConfigEntity syncWorkConfigEntity = syncWorkConfigRepository.findByWorkId(workEntityOptional.get().getId());
    saveSyncWorkConfigReq.setColumMapping(JSON.toJSON(saveSyncWorkConfigReq.getColumMapping()));
    syncWorkConfigRepository.save(syncWorkConfigMapper.saveSyncWorkConfigReqAndSyncWorkConfigEntityToSyncWorkConfigEntity(saveSyncWorkConfigReq,syncWorkConfigEntity));
	}

  public GetDataSourceTablesRes getDataSourceTables(GetDataSourceTablesReq getDataSourceTablesReq) throws Exception {

    Optional<DatasourceEntity> datasourceEntityOptional = datasourceRepository.findById(getDataSourceTablesReq.getDataSourceId());
    if (!datasourceEntityOptional.isPresent()) {
      throw new IsxAppException("数据源异常，请联系开发者");
    }

    Connection connection = JDBCConnection.getConnection(datasourceEntityOptional.get().getJdbcUrl(),
      datasourceEntityOptional.get().getUsername(),
      aesUtils.decrypt(datasourceEntityOptional.get().getPasswd()),
      null, null);
    String catalog = getCatalogOrSchema(connection, true);
    String schema = getCatalogOrSchema(connection, false);

    List<String> tables = syncWorkService.tables(connection.getMetaData(), catalog, schema);
    List<String> views = syncWorkService.views(connection.getMetaData(), catalog, schema);
    tables.addAll(views);
    connection.close();
    return GetDataSourceTablesRes.builder().tables(tables).build();
  }

  public GetDataSourceColumnsRes getDataSourceColumns(GetDataSourceColumnsReq getDataSourceColumnsReq) throws Exception {

    Optional<DatasourceEntity> datasourceEntityOptional = datasourceRepository.findById(getDataSourceColumnsReq.getDataSourceId());
    if (!datasourceEntityOptional.isPresent()) {
      throw new IsxAppException("数据源异常，请联系开发者");
    }

    Connection connection = JDBCConnection.getConnection(datasourceEntityOptional.get().getJdbcUrl(),
      datasourceEntityOptional.get().getUsername(),
      aesUtils.decrypt(datasourceEntityOptional.get().getPasswd()),
      null, null);
    String dataBase = null;
    String tableName = getDataSourceColumnsReq.getTableName();
    if (tableName.contains(".")){
      dataBase = tableName.split("\\.")[0];
      tableName = tableName.split("\\.")[1];
    }

    String catalog = getCatalogOrSchema(connection, true);
    String schema = getCatalogOrSchema(connection, false);

    Map<String, String> transform = syncWorkService.transform(dataBase, catalog, schema);

    List<List<String>> columns = syncWorkService.columns(connection.getMetaData(), transform.get("catalog"), transform.get("schema"), tableName);

    connection.close();
    return GetDataSourceColumnsRes.builder().columns(columns).build();
  }

  private String getCatalogOrSchema(Connection connection, boolean isCatalog) {
    try {
      if (isCatalog) {
        return connection.getCatalog();
      } else {
        return connection.getSchema();
      }
    } catch (SQLException e) {
      return null;
    }
  }

}
