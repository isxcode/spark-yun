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
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class GbaseService extends Datasource {

    public GbaseService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties, AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String generateLimitSql(String sql, Integer limit) throws JSQLParserException {

        if (isShowQueryStatement(sql)) {
            return sql;
        }

        net.sf.jsqlparser.statement.Statement statement = CCJSqlParserUtil.parse(sql);
        if (!(statement instanceof Select)) {
            throw new IllegalArgumentException("该Sql不是查询语句");
        }

        Select select = (Select) statement;
        PlainSelect plainSelect = select.getPlainSelect();

        Top top = new Top();
        top.setExpression(new LongValue(limit));
        plainSelect.setTop(top);

        return statement.toString();
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.GBASE;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.GBASE_DRIVER;
    }

    @Override
    public List<QueryTableDto> queryTable(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getDatabase(), "datasbase不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            if (Strings.isNotEmpty(connectInfo.getTablePattern())) {
                return qr.query(connection, "SELECT '" + connectInfo.getDatasourceId()
                    + "' as datasourceId,tables.table_name as tableName,tables.table_comment as tableComment FROM information_schema.tables WHERE table_schema = '"
                    + connectInfo.getDatabase() + "' AND  tables.table_name REGEXP '" + connectInfo.getTablePattern()
                    + "'", new BeanListHandler<>(QueryTableDto.class));
            } else {
                return qr.query(connection, "SELECT '" + connectInfo.getDatasourceId()
                    + "' as datasourceId,tables.table_name as tableName,tables.table_comment as tableComment FROM information_schema.tables WHERE table_schema = '"
                    + connectInfo.getDatabase() + "'", new BeanListHandler<>(QueryTableDto.class));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public List<QueryColumnDto> queryColumn(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getDatabase(), "datasource不能为空");
        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            return qr.query(connection, "SELECT '" + connectInfo.getDatasourceId() + "' as datasourceId,'"
                + connectInfo.getTableName()
                + "' as tableName, COLUMN_NAME as columnName,COLUMN_TYPE as columnType,COLUMN_COMMENT as columnComment,false as isPartitionColumn FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '"
                + connectInfo.getDatabase() + "' AND TABLE_NAME = '" + connectInfo.getTableName() + "'",
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

        Assert.notNull(connectInfo.getDatabase(), "datasource不能为空");
        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            BigInteger result = qr.query(connection,
                "SELECT data_length + index_length FROM information_schema.tables WHERE table_schema = '"
                    + connectInfo.getDatabase() + "' AND table_name = '" + connectInfo.getTableName() + "'",
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
            return qr.query(connection, "SELECT count(*) FROM " + connectInfo.getTableName(), new ScalarHandler<>());
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
            return qr.query(
                connection, "SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '"
                    + connectInfo.getDatabase() + "' AND TABLE_NAME = '" + connectInfo.getTableName() + "'",
                new ScalarHandler<>());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public String getPageSql(String sql) throws IsxAppException {
        return "SELECT * FROM (" + sql + ") AS SY_TMP LIMIT '${page}' , '${pageSize}' ";
    }

    @Override
    public GetDataSourceDataRes getTableData(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");
        Assert.notNull(connectInfo.getRowNumber(), "rowNumber不能为空");

        String getTableDataSql = "SELECT * FROM " + connectInfo.getTableName()
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
        return "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
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
            case "tinyint":
            case "smallint":
            case "mediumint":
            case "int":
            case "integer":
                return ColumnCode.INT;
            case "bigint":
                return ColumnCode.BIGINT;
            case "float":
                return ColumnCode.FLOAT;
            case "double":
            case "real":
                return ColumnCode.DOUBLE;
            case "decimal":
            case "numeric":
                return ColumnCode.DECIMAL;
            case "char":
                return ColumnCode.CHAR;
            case "varchar":
            case "tinytext":
                return ColumnCode.STRING;
            case "text":
            case "mediumtext":
            case "longtext":
                return ColumnCode.TEXT;
            case "date":
                return ColumnCode.DATE;
            case "datetime":
                return ColumnCode.DATETIME;
            case "timestamp":
                return ColumnCode.TIMESTAMP;
            case "boolean":
            case "bool":
            case "bit":
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
                columnDef.append("TINYINT(1)");
                break;
            case ColumnCode.INT:
                columnDef.append("INT");
                break;
            case ColumnCode.BIGINT:
                columnDef.append("BIGINT");
                break;
            case ColumnCode.FLOAT:
                columnDef.append("FLOAT");
                break;
            case ColumnCode.DOUBLE:
                columnDef.append("DOUBLE");
                break;
            case ColumnCode.DECIMAL:
                if (columnMeta.getColumnLength() != null && columnMeta.getColumnLength() > 0) {
                    columnDef.append("DECIMAL(").append(columnMeta.getColumnLength()).append(",2)");
                } else {
                    columnDef.append("DECIMAL(10,2)");
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
                columnDef.append("DATETIME");
                break;
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
