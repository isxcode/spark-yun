package com.isxcode.spark.modules.datasource.source.impl;

import com.isxcode.spark.api.datasource.constants.DatasourceDriver;
import com.isxcode.spark.api.datasource.constants.ColumnCode;
import com.isxcode.spark.api.datasource.dto.ColumnMetaDto;
import com.isxcode.spark.api.datasource.constants.DatasourceType;
import com.isxcode.spark.api.datasource.dto.ConnectInfo;
import com.isxcode.spark.api.datasource.dto.QueryColumnDto;
import com.isxcode.spark.api.datasource.dto.QueryTableDto;
import com.isxcode.spark.api.model.ao.DataModelColumnAo;
import com.isxcode.spark.api.work.res.GetDataSourceDataRes;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.utils.aes.AesUtils;
import com.isxcode.spark.modules.datasource.service.DatabaseDriverService;
import com.isxcode.spark.modules.datasource.source.Datasource;
import com.isxcode.spark.modules.model.entity.DataModelEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class GreenplumService extends Datasource {

    public GreenplumService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties,
        AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.GREENPLUM;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.GREENPLUM_DRIVER;
    }

    @Override
    public List<QueryTableDto> queryTable(ConnectInfo connectInfo) throws IsxAppException {

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {

            String sql = "SELECT '" + connectInfo.getDatasourceId() + "' AS datasourceId, t.tablename AS tableName, "
                + "obj_description(format('%s.%s', t.schemaname, t.tablename)::regclass, 'pg_class') AS tableComment "
                + "FROM pg_catalog.pg_tables t WHERE t.schemaname = 'public'";

            if (Strings.isNotEmpty(connectInfo.getTablePattern())) {
                sql += " AND tablename ~ '" + connectInfo.getTablePattern() + "'";
                return qr.query(connection, sql, new BeanListHandler<>(QueryTableDto.class));
            } else {
                return qr.query(connection, sql, new BeanListHandler<>(QueryTableDto.class));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public List<QueryColumnDto> queryColumn(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        // 设置schema a.attrelid = 'schema.tablename'::regclass
        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            return qr.query(connection, "SELECT '" + connectInfo.getDatasourceId() + "' as datasourceId, '"
                + connectInfo.getTableName() + "' AS tableName, a.attname AS columnName, false as isPartitionColumn, "
                + "pg_catalog.format_type(a.atttypid, a.atttypmod) AS columnType, col_description(a.attrelid, a.attnum) AS columnComment "
                + "FROM pg_catalog.pg_attribute a WHERE a.attrelid = '" + connectInfo.getTableName()
                + "'::regclass AND a.attnum > 0 AND NOT a.attisdropped", new BeanListHandler<>(QueryColumnDto.class));
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public String generateDataModelSql(ConnectInfo connectInfo, List<DataModelColumnAo> modelColumnList,
        DataModelEntity dataModelEntity) throws IsxAppException {
        throw new RuntimeException("数据源暂不支持，请联系管理员");
    }

    @Override
    public Long getTableTotalSize(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            Object query =
                qr.query(connection, "SELECT pg_total_relation_size('" + connectInfo.getTableName() + "'::regclass);",
                    new ScalarHandler<>());
            return Long.parseLong(String.valueOf(query));
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public Long getTableTotalRows(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            Object query =
                qr.query(connection, "SELECT count(*) FROM " + connectInfo.getTableName(), new ScalarHandler<>());
            return Long.valueOf(String.valueOf(query));
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public Long getTableColumnCount(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            Object query = qr.query(
                connection, "SELECT COUNT(*) FROM pg_catalog.pg_attribute " + "WHERE attrelid = '"
                    + connectInfo.getTableName() + "'::regclass AND attnum > 0 AND NOT attisdropped",
                new ScalarHandler<>());
            return Long.valueOf(String.valueOf(query));
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public String getPageSql(String sql) throws IsxAppException {
        return "SELECT * FROM (" + sql + ") AS SY_TMP LIMIT '${pageSize}' OFFSET '${page}' ";
    }

    @Override
    public GetDataSourceDataRes getTableData(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");
        Assert.notNull(connectInfo.getRowNumber(), "rowNumber不能为空");

        String getTableDataSql = "SELECT * FROM \"" + connectInfo.getTableName() + "\""
            + ("ALL".equals(connectInfo.getRowNumber()) ? "" : " LIMIT " + connectInfo.getRowNumber());
        return getTableData(connectInfo, getTableDataSql);
    }

    @Override
    public void refreshTableInfo(ConnectInfo connectInfo) throws IsxAppException {

    }


    @Override
    public String getCreateTableFormat() {
        return "CREATE TABLE %s (%s) %s %s";
    }

    @Override
    public String getCreateTableSuffix(List<ColumnMetaDto> fromColumnList) {
        return "";
    }

    @Override
    public String getCreateTableOptionalSuffix(List<ColumnMetaDto> fromColumnList) {
        return "";
    }

    @Override
    public String convertColumnCode(ColumnMetaDto columnMeta) {
        String type = columnMeta.getType().toLowerCase();
        if (type.contains("(")) {
            type = type.substring(0, type.indexOf("("));
        }
        switch (type) {
            case "smallint":
            case "int2":
            case "integer":
            case "int":
            case "int4":
                return ColumnCode.INT;
            case "bigint":
            case "int8":
                return ColumnCode.BIGINT;
            case "real":
            case "float4":
                return ColumnCode.FLOAT;
            case "double precision":
            case "float8":
                return ColumnCode.DOUBLE;
            case "numeric":
            case "decimal":
                return ColumnCode.DECIMAL;
            case "character":
            case "char":
                return ColumnCode.CHAR;
            case "character varying":
            case "varchar":
            case "text":
                return ColumnCode.STRING;
            case "date":
                return ColumnCode.DATE;
            case "timestamp":
            case "timestamptz":
                return ColumnCode.DATETIME;
            case "boolean":
            case "bool":
                return ColumnCode.BOOLEAN;
            default:
                return ColumnCode.STRING;
        }
    }

    @Override
    public String convertColumnType(ColumnMetaDto columnMeta, String columnCode) {
        StringBuilder columnDef = new StringBuilder();
        columnDef.append(columnMeta.getName()).append(" ");
        switch (columnCode) {
            case ColumnCode.BOOLEAN:
                columnDef.append("BOOLEAN");
                break;
            case ColumnCode.INT:
                columnDef.append("INTEGER");
                break;
            case ColumnCode.BIGINT:
                columnDef.append("BIGINT");
                break;
            case ColumnCode.FLOAT:
                columnDef.append("REAL");
                break;
            case ColumnCode.DOUBLE:
                columnDef.append("DOUBLE PRECISION");
                break;
            case ColumnCode.DECIMAL:
                if (columnMeta.getColumnLength() != null && columnMeta.getColumnLength() > 0) {
                    columnDef.append("NUMERIC(").append(columnMeta.getColumnLength()).append(",2)");
                } else {
                    columnDef.append("NUMERIC(10,2)");
                }
                break;
            case ColumnCode.CHAR:
                if (columnMeta.getColumnLength() != null && columnMeta.getColumnLength() > 0) {
                    columnDef.append("CHAR(").append(columnMeta.getColumnLength()).append(")");
                } else {
                    columnDef.append("CHAR(50)");
                }
                break;
            case ColumnCode.STRING:
                if (columnMeta.getColumnLength() != null && columnMeta.getColumnLength() > 0) {
                    columnDef.append("VARCHAR(").append(columnMeta.getColumnLength()).append(")");
                } else {
                    columnDef.append("VARCHAR(255)");
                }
                break;
            case ColumnCode.TEXT:
                columnDef.append("TEXT");
                break;
            case ColumnCode.DATE:
                columnDef.append("DATE");
                break;
            case ColumnCode.DATETIME:
            case ColumnCode.TIMESTAMP:
                columnDef.append("TIMESTAMP");
                break;
            default:
                columnDef.append("VARCHAR(255)");
                break;
        }
        if (columnMeta.getIsNoNullColumn() != null && columnMeta.getIsNoNullColumn()) {
            columnDef.append(" NOT NULL");
        }
        if (columnMeta.getIsPrimaryColumn() != null && columnMeta.getIsPrimaryColumn()) {
            columnDef.append(" PRIMARY KEY");
        }
        return columnDef.toString();
    }

}
