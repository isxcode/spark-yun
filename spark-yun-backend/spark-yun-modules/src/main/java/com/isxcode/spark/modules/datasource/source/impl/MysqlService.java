package com.isxcode.spark.modules.datasource.source.impl;

import com.isxcode.spark.api.datasource.constants.ColumnCode;
import com.isxcode.spark.api.datasource.constants.DatasourceDriver;
import com.isxcode.spark.api.datasource.constants.DatasourceType;
import com.isxcode.spark.api.datasource.dto.ColumnMetaDto;
import com.isxcode.spark.api.datasource.dto.ConnectInfo;
import com.isxcode.spark.api.datasource.dto.QueryColumnDto;
import com.isxcode.spark.api.datasource.dto.QueryTableDto;
import com.isxcode.spark.api.model.ao.DataModelColumnAo;
import com.isxcode.spark.api.model.constant.ColumnFormatType;
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

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class MysqlService extends Datasource {

    public MysqlService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties, AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.MYSQL;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.MYSQL_DRIVER;
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
        DataModelEntity dataModel) throws IsxAppException {

        StringBuilder sqlBuilder = new StringBuilder();

        // 开始构建 CREATE TABLE 语句
        sqlBuilder.append("CREATE TABLE ").append(dataModel.getTableName()).append(" (\n");

        // 构建字段部分
        for (int i = 0; i < modelColumnList.size(); i++) {
            DataModelColumnAo column = modelColumnList.get(i);
            sqlBuilder.append("    ").append(column.getColumnName()).append(" ")
                .append(mapColumnType(column.getColumnTypeCode(), column.getColumnType())).append(" ")
                .append("COMMENT '").append(column.getRemark() != null ? column.getRemark() : "").append("'");

            if (i < modelColumnList.size() - 1) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append("\n");
        }

        // 结束括号
        sqlBuilder.append(")");
        sqlBuilder.append("\nENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        sqlBuilder.append(";");

        return sqlBuilder.toString();
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
        // CREATE TABLE 表名 (字段列表) 后缀 可选后缀
        return "CREATE TABLE %s (%s) %s %s";
    }

    @Override
    public String getCreateTableSuffix(List<ColumnMetaDto> fromColumnList) {
        // MySQL 必需的表后缀
        return "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
    }

    @Override
    public String getCreateTableOptionalSuffix(List<ColumnMetaDto> fromColumnList) {
        // MySQL 可选的表后缀，暂时不需要
        return "";
    }

    @Override
    public String convertColumnCode(ColumnMetaDto columnMeta) {
        // 将 MySQL 字段类型转换为标准的 ColumnCode
        String type = columnMeta.getType().toLowerCase();

        // 处理带括号的类型，如 varchar(255)
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
        // 将 ColumnCode 转换为 MySQL 字段类型定义
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

        // 添加 NOT NULL 约束
        if (columnMeta.getIsNoNullColumn() != null && columnMeta.getIsNoNullColumn()) {
            columnDef.append(" NOT NULL");
        }

        // 添加主键约束
        if (columnMeta.getIsPrimaryColumn() != null && columnMeta.getIsPrimaryColumn()) {
            columnDef.append(" PRIMARY KEY");
        }

        return columnDef.toString();
    }


    private String mapColumnType(String columnTypeCode, String columnType) {
        switch (columnTypeCode) {
            case ColumnFormatType.CUSTOM:
                return columnType;
            case ColumnFormatType.TEXT:
                return "text";
            case ColumnFormatType.DATE:
                return "date";
            case ColumnFormatType.DOUBLE:
                return "double";
            case ColumnFormatType.INT:
                return "int";
            case ColumnFormatType.DATETIME:
                return "datetime";
            case ColumnFormatType.STRING:
                return "varchar(" + (columnType == null ? "200" : columnType) + ")";
            default:
                return "暂不支持该类型字段";
        }
    }

}
