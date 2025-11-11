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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class ClickhouseService extends Datasource {

    public ClickhouseService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties,
        AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.CLICKHOUSE;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.CLICKHOUSE_DRIVER;
    }

    @Override
    public List<QueryTableDto> queryTable(ConnectInfo connectInfo) throws IsxAppException {
        Assert.notNull(connectInfo.getDatabase(), "database不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            String querySql = Strings.isNotEmpty(connectInfo.getTablePattern())
                ? "SELECT ? as datasourceId, name as tableName, comment as tableComment FROM system.tables WHERE database = ? AND name REGEXP ?"
                : "SELECT ? as datasourceId, name as tableName, comment as tableComment FROM system.tables WHERE database = ?";
            if (Strings.isNotEmpty(connectInfo.getTablePattern())) {
                return qr.query(connection, querySql, new BeanListHandler<>(QueryTableDto.class),
                    connectInfo.getDatasourceId(), connectInfo.getDatabase(), connectInfo.getTablePattern());
            } else {
                return qr.query(connection, querySql, new BeanListHandler<>(QueryTableDto.class),
                    connectInfo.getDatasourceId(), connectInfo.getDatabase());
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public List<QueryColumnDto> queryColumn(ConnectInfo connectInfo) throws IsxAppException {
        Assert.notNull(connectInfo.getDatabase(), "database不能为空");
        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            String querySql =
                "SELECT ? as datasourceId, ? as tableName, name as columnName, type as columnType, comment as columnComment, false as isPartitionColumn FROM system.columns WHERE database = ? AND table = ?";
            return qr.query(connection, querySql, new BeanListHandler<>(QueryColumnDto.class),
                connectInfo.getDatasourceId(), connectInfo.getTableName(), connectInfo.getDatabase(),
                connectInfo.getTableName());
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
        sqlBuilder.append("CREATE TABLE ").append(connectInfo.getDatabase()).append(".")
            .append(dataModel.getTableName()).append(" (\n");

        // 构建字段部分
        for (int i = 0; i < modelColumnList.size(); i++) {
            DataModelColumnAo column = modelColumnList.get(i);
            sqlBuilder.append("    ").append(column.getColumnName()).append(" ")
                .append(mapColumnType(column.getColumnTypeCode(), column.getColumnType()));
            if (Strings.isNotEmpty(column.getRemark())) {
                sqlBuilder.append(" COMMENT '").append(column.getRemark()).append("'");
            }
            if (i < modelColumnList.size() - 1) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append("\n");
        }

        // 结束括号并指定引擎
        sqlBuilder.append(") ENGINE = MergeTree()\n");
        sqlBuilder.append("ORDER BY tuple()");
        return sqlBuilder.toString();
    }

    @Override
    public Long getTableTotalSize(ConnectInfo connectInfo) throws IsxAppException {
        Assert.notNull(connectInfo.getDatabase(), "database不能为空");
        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            return qr.query(connection, "SELECT total_bytes FROM system.tables WHERE database = ? AND name = ?",
                new ScalarHandler<>(), connectInfo.getDatabase(), connectInfo.getTableName());
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
            String tableRef = connectInfo.getDatabase() + "." + connectInfo.getTableName();
            return qr.query(connection, "SELECT count(*) FROM " + tableRef, new ScalarHandler<>());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public Long getTableColumnCount(ConnectInfo connectInfo) throws IsxAppException {
        Assert.notNull(connectInfo.getDatabase(), "database不能为空");
        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            return qr.query(connection, "SELECT COUNT(*) FROM system.columns WHERE database = ? AND table = ?",
                new ScalarHandler<>(), connectInfo.getDatabase(), connectInfo.getTableName());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public String getPageSql(String sql) throws IsxAppException {
        return "SELECT * FROM (" + sql + ") LIMIT '${pageSize}' OFFSET '${page}'";
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
        // ClickHouse 不需要显式刷新表信息，系统表会自动更新
    }

    private String mapColumnType(String columnTypeCode, String columnType) {
        switch (columnTypeCode) {
            case ColumnFormatType.CUSTOM:
                return columnType != null ? columnType : "String";
            case ColumnFormatType.TEXT:
                return "String";
            case ColumnFormatType.DATE:
                return "Date";
            case ColumnFormatType.DOUBLE:
                return "Float64";
            case ColumnFormatType.INT:
                return "Int32";
            case ColumnFormatType.DATETIME:
                return "DateTime";
            case ColumnFormatType.STRING:
                return "String";
            default:
                return "String"; // 默认使用 String 作为兜底类型
        }
    }
}
