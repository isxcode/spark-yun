package com.isxcode.spark.modules.datasource.source.impl;

import com.isxcode.spark.api.datasource.constants.DatasourceDriver;
import com.isxcode.spark.api.datasource.dto.ColumnMetaDto;
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class H2Service extends Datasource {

    public H2Service(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties, AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.H2;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.H2_DRIVER;
    }

    @Override
    public List<QueryTableDto> queryTable(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getDatabase(), "数据库不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            String sql;
            if (Strings.isNotEmpty(connectInfo.getTablePattern())) {
                sql = "SELECT '" + connectInfo.getDatasourceId()
                    + "' AS datasourceId, TABLE_NAME AS tableName, '' AS tableComment "
                    + "FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + connectInfo.getDatabase() + "' "
                    + "AND TABLE_NAME LIKE '%" + connectInfo.getTablePattern() + "%'";
            } else {
                sql = "SELECT '" + connectInfo.getDatasourceId()
                    + "' AS datasourceId, TABLE_NAME AS tableName, REMARKS AS tableComment "
                    + "FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'";
            }
            return qr.query(connection, sql, new BeanListHandler<>(QueryTableDto.class));
        } catch (SQLException e) {
            log.error("Error querying tables: {}", e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public List<QueryColumnDto> queryColumn(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getDatabase(), "数据库不能为空");
        Assert.notNull(connectInfo.getTableName(), "表名不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            String sql = "SELECT '" + connectInfo.getDatasourceId() + "' AS datasourceId, '"
                + connectInfo.getTableName()
                + "' AS tableName, COLUMN_NAME AS columnName, DATA_TYPE AS columnType, REMARKS AS columnComment, false AS isPartitionColumn "
                + "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + connectInfo.getDatabase()
                + "' AND TABLE_NAME = '" + connectInfo.getTableName() + "'";
            return qr.query(connection, sql, new BeanListHandler<>(QueryColumnDto.class));
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
        sqlBuilder.append(");");

        return sqlBuilder.toString();
    }

    @Override
    public Long getTableTotalSize(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getDatabase(), "datasource不能为空");
        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            Long result = qr.query(connection,
                "SELECT DISK_SPACE_USED ('\"" + connectInfo.getTableName() + "\"') FROM DUAL", new ScalarHandler<>());
            return result;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public Long getTableTotalRows(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getTableName(), "表名不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            String sql = "SELECT COUNT(*) FROM \"" + connectInfo.getTableName() + "\"";
            return qr.query(connection, sql, new ScalarHandler<>());
        } catch (SQLException e) {
            log.error("Error querying table rows: {}", e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public Long getTableColumnCount(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getTableName(), "表名不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '"
                + connectInfo.getDatabase() + "' AND TABLE_NAME = '" + connectInfo.getTableName() + "'";
            return qr.query(connection, sql, new ScalarHandler<>());
        } catch (SQLException e) {
            log.error("Error querying table column count: {}", e.getMessage(), e);
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

        String getTableDataSql = "SELECT * FROM " + connectInfo.getTableName()
            + ("ALL".equals(connectInfo.getRowNumber()) ? "" : " LIMIT " + connectInfo.getRowNumber());
        return getTableData(connectInfo, getTableDataSql);
    }

    @Override
    public void refreshTableInfo(ConnectInfo connectInfo) throws IsxAppException {
        // Refresh logic can be implemented if needed
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

    @Override
    public String getCreateTableFormat() {
        return "";
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
        return "";
    }

    @Override
    public String convertColumnType(ColumnMetaDto columnMeta, String columnCode) {
        return "";
    }

}
