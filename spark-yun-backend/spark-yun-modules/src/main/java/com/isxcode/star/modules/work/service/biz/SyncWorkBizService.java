package com.isxcode.star.modules.work.service.biz;

import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.dto.ColumnMetaDto;
import com.isxcode.star.api.datasource.dto.ConnectInfo;
import com.isxcode.star.api.work.req.GetCreateTableSqlReq;
import com.isxcode.star.api.work.req.GetDataSourceColumnsReq;
import com.isxcode.star.api.work.req.GetDataSourceDataReq;
import com.isxcode.star.api.work.req.GetDataSourceTablesReq;
import com.isxcode.star.api.work.res.GetCreateTableSqlRes;
import com.isxcode.star.api.work.res.GetDataSourceColumnsRes;
import com.isxcode.star.api.work.res.GetDataSourceDataRes;
import com.isxcode.star.api.work.res.GetDataSourceTablesRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.star.modules.datasource.service.DatasourceService;
import com.isxcode.star.modules.datasource.source.DataSourceFactory;
import com.isxcode.star.modules.datasource.source.Datasource;
import com.isxcode.star.modules.work.service.SyncWorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SyncWorkBizService {

    private final DatasourceService datasourceService;

    private final SyncWorkService syncWorkService;

    private final DataSourceFactory dataSourceFactory;

    private final DatasourceMapper datasourceMapper;

    public GetDataSourceTablesRes getDataSourceTables(GetDataSourceTablesReq getDataSourceTablesReq) throws Exception {

        DatasourceEntity datasourceEntity = datasourceService.getDatasource(getDataSourceTablesReq.getDataSourceId());
        ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
        Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
        Connection connection;
        try {
            connection = datasource.getConnection(connectInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("【" + datasourceEntity.getName() + "】连接异常，请检查数据源");
        }
        Map<String, String> transform = getTransform(connection, getDataSourceTablesReq.getTablePattern());
        if (DatasourceType.ORACLE.equals(datasourceEntity.getDbType())) {
            transform.put("schema", connectInfo.getUsername().toUpperCase());
        }
        List<String> tables = syncWorkService.tables(connection.getMetaData(), transform.get("catalog"),
            transform.get("schema"), transform.get("tableName"));
        List<String> views = syncWorkService.views(connection.getMetaData(), transform.get("catalog"),
            transform.get("schema"), transform.get("tableName"));
        tables.addAll(views);
        connection.close();
        return GetDataSourceTablesRes.builder().tables(tables).build();
    }

    public GetDataSourceColumnsRes getDataSourceColumns(GetDataSourceColumnsReq getDataSourceColumnsReq)
        throws Exception {

        DatasourceEntity datasourceEntity = datasourceService.getDatasource(getDataSourceColumnsReq.getDataSourceId());
        ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
        Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
        Connection connection;
        try {
            connection = datasource.getConnection(connectInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("【" + datasourceEntity.getName() + "】连接异常，请检查数据源");
        }
        Map<String, String> transform = getTransform(connection, getDataSourceColumnsReq.getTableName());
        List<ColumnMetaDto> columns = syncWorkService.columns(connection.getMetaData(), transform.get("catalog"),
            transform.get("schema"), transform.get("tableName"));
        connection.close();
        return GetDataSourceColumnsRes.builder().columns(columns).build();
    }

    public GetDataSourceDataRes getDataSourceData(GetDataSourceDataReq getDataSourceDataReq) throws Exception {

        DatasourceEntity datasourceEntity = datasourceService.getDatasource(getDataSourceDataReq.getDataSourceId());
        ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
        Datasource datasource = dataSourceFactory.getDatasource(datasourceEntity.getDbType());
        connectInfo.setTableName(getDataSourceDataReq.getTableName());
        connectInfo.setRowNumber("200");
        return datasource.getTableData(connectInfo);
    }

    public GetCreateTableSqlRes getCreateTableSql(GetCreateTableSqlReq getCreateTableSqlReq) throws Exception {

        DatasourceEntity datasourceEntity = datasourceService.getDatasource(getCreateTableSqlReq.getDataSourceId());
        ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
        Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
        Connection connection = datasource.getConnection(connectInfo);
        Map<String, String> transform = getTransform(connection, getCreateTableSqlReq.getTableName());
        ResultSet columns = connection.getMetaData().getColumns(transform.get("catalog"), transform.get("schema"),
            transform.get("tableName"), null);
        String sql = String.join(" ", "CREATE TABLE", transform.get("tableName"), "(");
        while (columns.next()) {
            sql = String.join(" ", sql, "\n", columns.getString("COLUMN_NAME"), "String,");
        }
        connection.close();
        return GetCreateTableSqlRes.builder().sql(sql.substring(0, sql.length() - 1) + "\n)").build();
    }

    private Map<String, String> getTransform(Connection connection, String tableName) {
        String dataBase = null;
        if (tableName.contains(".")) {
            dataBase = tableName.split("\\.")[0];
            tableName = tableName.split("\\.")[1];
        }

        String catalog = getCatalogOrSchema(connection, true);
        String schema = getCatalogOrSchema(connection, false);

        Map<String, String> transform = syncWorkService.transform(dataBase, catalog, schema);
        transform.put("tableName", tableName);
        return transform;
    }

    private String getCatalogOrSchema(Connection connection, boolean isCatalog) {
        try {
            if (isCatalog) {
                return connection.getCatalog();
            } else {
                return connection.getSchema();
            }
        } catch (SQLException | AbstractMethodError e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }
}
