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
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class OracleService extends Datasource {

    public OracleService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties,
        AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    // TEMPORARILY RESERVED
    private void checkDatabase(ConnectInfo connectInfo) {
        if ("default".equals(connectInfo.getDatabase()) && StringUtils.isNotBlank(connectInfo.getUsername())) {
            connectInfo.setDatabase(connectInfo.getUsername().toUpperCase());
        }
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.ORACLE;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.ORACLE_DRIVER;
    }

    @Override
    public List<QueryTableDto> queryTable(ConnectInfo connectInfo) throws IsxAppException {

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {

            String sql = "SELECT '" + connectInfo.getDatasourceId()
                + "' AS datasourceId, T.TABLE_NAME AS tableName, C.COMMENTS AS tableComment "
                + "FROM USER_TABLES T LEFT JOIN USER_TAB_COMMENTS C ON T.TABLE_NAME = C.TABLE_NAME";

            if (Strings.isNotEmpty(connectInfo.getTablePattern())) {
                sql += " AND REGEXP_LIKE(T.TABLE_NAME ,'" + connectInfo.getTablePattern() + "')";
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

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            return qr.query(connection, "SELECT '" + connectInfo.getDatasourceId()
                + "' AS datasourceId, 0 AS isPartitionColumn, "
                + "COL.TABLE_NAME AS tableName, COL.COLUMN_NAME AS columnName, COL.DATA_TYPE AS columnType, COMM.COMMENTS AS columnComment "
                + "FROM USER_TAB_COLUMNS COL LEFT JOIN USER_COL_COMMENTS COMM ON COL.TABLE_NAME = COMM.TABLE_NAME AND COL.COLUMN_NAME = COMM.COLUMN_NAME "
                + "WHERE COL.TABLE_NAME = '" + connectInfo.getTableName() + "'",
                new BeanListHandler<>(QueryColumnDto.class));
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
            Object result = qr.query(connection,
                "SELECT bytes FROM user_segments " + "WHERE segment_name = '" + connectInfo.getTableName() + "'",
                new ScalarHandler<>());
            return Long.parseLong(String.valueOf(result));
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
            Object count =
                qr.query(connection, "SELECT COUNT(*) FROM " + connectInfo.getTableName(), new ScalarHandler<>());
            return Long.valueOf(String.valueOf(count));
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
            Object count = qr.query(connection,
                "SELECT COUNT(*) FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '" + connectInfo.getTableName() + "'",
                new ScalarHandler<>());
            return Long.valueOf(String.valueOf(count));
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public String getPageSql(String sql) throws IsxAppException {

        // 以第一个字段作为排序字段
        String[] split = sql.split(",");
        if (split.length < 1 || split[0].length() < 6 || !"select".equals(split[0].substring(0, 6))) {
            throw new IsxAppException("需要首单词为select的查询语句");
        }
        String firstCol = split[0].toLowerCase().trim().substring(7);
        String firstKey = "ROW_NUMBER() OVER (ORDER BY " + firstCol + " ) AS SY_ROW_NUM";
        return "SELECT * FROM ( SELECT SY_TMP.* ," + firstKey + " FROM (" + sql
            + ") SY_TMP ) WHERE SY_ROW_NUM BETWEEN '${page}' AND '${pageSize}'";
    }

    @Override
    public GetDataSourceDataRes getTableData(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");
        Assert.notNull(connectInfo.getRowNumber(), "rowNumber不能为空");

        String getTableDataSql = "SELECT * FROM " + connectInfo.getTableName()
            + ("ALL".equals(connectInfo.getRowNumber()) ? "" : " WHERE ROWNUM <= " + connectInfo.getRowNumber());
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
            case "number":
                return ColumnCode.DECIMAL;
            case "integer":
            case "int":
                return ColumnCode.INT;
            case "float":
            case "binary_float":
                return ColumnCode.FLOAT;
            case "binary_double":
                return ColumnCode.DOUBLE;
            case "char":
                return ColumnCode.CHAR;
            case "varchar2":
            case "varchar":
            case "nvarchar2":
                return ColumnCode.STRING;
            case "clob":
            case "nclob":
            case "long":
                return ColumnCode.TEXT;
            case "date":
                return ColumnCode.DATE;
            case "timestamp":
                return ColumnCode.DATETIME;
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
                columnDef.append("NUMBER(1)");
                break;
            case ColumnCode.INT:
                columnDef.append("INTEGER");
                break;
            case ColumnCode.BIGINT:
                columnDef.append("NUMBER(19)");
                break;
            case ColumnCode.FLOAT:
                columnDef.append("BINARY_FLOAT");
                break;
            case ColumnCode.DOUBLE:
                columnDef.append("BINARY_DOUBLE");
                break;
            case ColumnCode.DECIMAL:
                if (columnMeta.getColumnLength() != null && columnMeta.getColumnLength() > 0) {
                    columnDef.append("NUMBER(").append(columnMeta.getColumnLength()).append(",2)");
                } else {
                    columnDef.append("NUMBER(10,2)");
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
                    columnDef.append("VARCHAR2(").append(columnMeta.getColumnLength()).append(")");
                } else {
                    columnDef.append("VARCHAR2(255)");
                }
                break;
            case ColumnCode.TEXT:
                columnDef.append("CLOB");
                break;
            case ColumnCode.DATE:
                columnDef.append("DATE");
                break;
            case ColumnCode.DATETIME:
            case ColumnCode.TIMESTAMP:
                columnDef.append("TIMESTAMP");
                break;
            default:
                columnDef.append("VARCHAR2(255)");
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


    @Override
    public String convertToFlinkColumnType(String columnType) {

        if (columnType == null) {
            return "STRING";
        }

        String normalizedType = columnType.trim().toLowerCase();
        if (normalizedType.isEmpty()) {
            return "STRING";
        }

        normalizedType = normalizedType.replace("unsigned", "").replace("zerofill", "").trim();

        if (normalizedType.contains("array") || normalizedType.endsWith("[]")) {
            return "ARRAY<STRING>";
        }
        if (normalizedType.contains("map")) {
            return "MAP<STRING, STRING>";
        }

        if (normalizedType.contains("boolean") || normalizedType.contains("bool")
            || normalizedType.matches("bit\\s*\\(\\s*1\\s*\\)")) {
            return "BOOLEAN";
        }

        if (normalizedType.contains("tinyint") || normalizedType.contains("smallint")
            || normalizedType.contains("mediumint") || normalizedType.matches("(^|\\W)int(\\W|$)")
            || normalizedType.contains("integer") || normalizedType.contains("serial")) {
            return "INT";
        }
        if (normalizedType.contains("bigint") || normalizedType.contains("int8")
            || normalizedType.contains("bigserial")) {
            return "BIGINT";
        }

        if (normalizedType.contains("float") || normalizedType.contains("real")
            || normalizedType.contains("binary_float")) {
            return "FLOAT";
        }
        if (normalizedType.contains("double") || normalizedType.contains("binary_double")) {
            return "DOUBLE";
        }
        if (normalizedType.contains("decimal") || normalizedType.contains("numeric")
            || normalizedType.contains("number") || normalizedType.contains("money")) {
            return "DECIMAL(38, 18)";
        }

        if (normalizedType.matches("(^|\\W)date(\\W|$)")) {
            return "DATE";
        }
        if (normalizedType.contains("timestamp") || normalizedType.contains("datetime")
            || normalizedType.contains("smalldatetime") || normalizedType.matches("(^|\\W)time(\\W|$)")) {
            return "TIMESTAMP";
        }

        if (normalizedType.contains("blob") || normalizedType.contains("binary") || normalizedType.contains("varbinary")
            || normalizedType.contains("bytea") || normalizedType.contains("raw") || normalizedType.contains("image")) {
            return "BYTES";
        }

        return "STRING";
    }

}
