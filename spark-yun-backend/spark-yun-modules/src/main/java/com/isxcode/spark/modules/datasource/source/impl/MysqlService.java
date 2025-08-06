package com.isxcode.spark.modules.datasource.source.impl;

import com.isxcode.spark.api.datasource.constants.DatasourceDriver;
import com.isxcode.spark.api.datasource.constants.DatasourceType;
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
