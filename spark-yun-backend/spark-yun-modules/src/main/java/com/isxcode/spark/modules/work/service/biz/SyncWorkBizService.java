package com.isxcode.spark.modules.work.service.biz;

import com.isxcode.spark.api.datasource.dto.ColumnMetaDto;
import com.isxcode.spark.api.datasource.dto.ConnectInfo;
import com.isxcode.spark.api.datasource.dto.TableMetaInfo;
import com.isxcode.spark.api.work.req.GetCreateTableSqlReq;
import com.isxcode.spark.api.work.req.GetDataSourceColumnsReq;
import com.isxcode.spark.api.work.req.GetDataSourceDataReq;
import com.isxcode.spark.api.work.req.GetDataSourceTablesReq;
import com.isxcode.spark.api.work.res.GetCreateTableSqlRes;
import com.isxcode.spark.api.work.res.GetDataSourceColumnsRes;
import com.isxcode.spark.api.work.res.GetDataSourceDataRes;
import com.isxcode.spark.api.work.res.GetDataSourceTablesRes;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.modules.datasource.entity.DatasourceEntity;
import com.isxcode.spark.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.spark.modules.datasource.service.DatasourceService;
import com.isxcode.spark.modules.datasource.source.DataSourceFactory;
import com.isxcode.spark.modules.datasource.source.Datasource;
import com.isxcode.spark.modules.meta.entity.MetaColumnEntity;
import com.isxcode.spark.modules.meta.repository.MetaColumnRepository;
import com.isxcode.spark.modules.work.service.SyncWorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SyncWorkBizService {

    private final DatasourceService datasourceService;

    private final SyncWorkService syncWorkService;

    private final DataSourceFactory dataSourceFactory;

    private final DatasourceMapper datasourceMapper;

    private final MetaColumnRepository metaColumnRepository;

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
        TableMetaInfo tableMetaInfo = getTableMetaInfo(connection, getDataSourceTablesReq.getTablePattern());
        List<String> tables = syncWorkService.tables(connection.getMetaData(), tableMetaInfo.getCatalog(),
            tableMetaInfo.getSchema(), tableMetaInfo.getTableName());

        // 开启视图开关再查询
        if (getDataSourceTablesReq.getIsListViews() == null || getDataSourceTablesReq.getIsListViews()) {
            List<String> views = syncWorkService.views(connection.getMetaData(), tableMetaInfo.getCatalog(),
                tableMetaInfo.getSchema(), tableMetaInfo.getTableName());
            tables.addAll(views);
        }
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
        TableMetaInfo tableMetaInfo = getTableMetaInfo(connection, getDataSourceColumnsReq.getTableName());
        List<ColumnMetaDto> columns = syncWorkService.columns(connection.getMetaData(), tableMetaInfo.getCatalog(),
            tableMetaInfo.getSchema(), tableMetaInfo.getTableName());
        connection.close();

        // 给字段加注释
        columns.forEach(column -> {
            Optional<MetaColumnEntity> columnEntityOptional =
                metaColumnRepository.findByDatasourceIdAndTableNameAndColumnName(datasourceEntity.getId(),
                    getDataSourceColumnsReq.getTableName(), column.getName());
            columnEntityOptional
                .ifPresent(metaColumnEntity -> column.setColumnComment(metaColumnEntity.getColumnComment()));
        });
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
        TableMetaInfo tableMetaInfo = getTableMetaInfo(connection, getCreateTableSqlReq.getTableName());
        ResultSet columns = connection.getMetaData().getColumns(tableMetaInfo.getCatalog(), tableMetaInfo.getSchema(),
            tableMetaInfo.getTableName(), null);
        String sql = String.join(" ", "CREATE TABLE", tableMetaInfo.getTableName(), "(");
        while (columns.next()) {
            sql = String.join(" ", sql, "\n", columns.getString("COLUMN_NAME"), "String,");
        }
        connection.close();
        return GetCreateTableSqlRes.builder().sql(sql.substring(0, sql.length() - 1) + "\n)").build();
    }

    private TableMetaInfo getTableMetaInfo(Connection connection, String tableName) {
        String catalog = null;
        String schema = null;
        String productName = null;
        String userName = null;
        String finalTableName = tableName == null ? null : tableName.trim();

        try {
            catalog = connection.getCatalog();
        } catch (SQLException | AbstractMethodError e) {
            log.debug(e.getMessage(), e);
        }
        try {
            schema = connection.getSchema();
        } catch (SQLException | AbstractMethodError e) {
            log.debug(e.getMessage(), e);
        }
        try {
            productName = connection.getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
        }
        try {
            userName = connection.getMetaData().getUserName();
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
        }

        boolean canSplit = finalTableName != null && !finalTableName.trim().isEmpty() && !finalTableName.contains("^")
            && !finalTableName.contains("*") && !finalTableName.contains("%") && !finalTableName.contains("(")
            && !finalTableName.contains(")") && !finalTableName.contains("|") && !finalTableName.contains("[")
            && !finalTableName.contains("]") && finalTableName.contains(".");

        if (canSplit) {
            String[] split = finalTableName.split("\\.");
            if (split.length == 2) {
                // schema.table 或 catalog.table：优先填 schema，若 schema 为空且有 catalog 则填 catalog
                if ((schema == null || schema.trim().isEmpty()) && !(catalog == null || catalog.trim().isEmpty())) {
                    catalog = split[0];
                } else {
                    schema = split[0];
                }
                finalTableName = split[1];
            } else if (split.length == 3) {
                // catalog.schema.table
                catalog = split[0];
                schema = split[1];
                finalTableName = split[2];
            }
        }

        boolean isOracle = productName != null && productName.toLowerCase(Locale.ROOT).contains("oracle");
        if (isOracle) {
            // Oracle 使用 schema，不使用 catalog
            catalog = null;
            if (schema == null || schema.trim().isEmpty()) {
                if (userName != null && !userName.trim().isEmpty()) {
                    schema = userName.contains("@") ? userName.substring(0, userName.indexOf("@")) : userName;
                }
            }
            if (schema != null && !schema.trim().isEmpty()) {
                schema = schema.toUpperCase(Locale.ROOT);
            }
            boolean canFormat = finalTableName != null && !finalTableName.trim().isEmpty()
                && !finalTableName.contains("^") && !finalTableName.contains("*") && !finalTableName.contains("%");
            if (canFormat) {
                finalTableName = finalTableName.toUpperCase(Locale.ROOT);
            }
        }

        return TableMetaInfo.builder().catalog(catalog).schema(schema).tableName(finalTableName).build();
    }
}
