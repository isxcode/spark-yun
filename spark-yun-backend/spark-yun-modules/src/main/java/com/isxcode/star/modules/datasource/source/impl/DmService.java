package com.isxcode.star.modules.datasource.source.impl;

import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.dto.ConnectInfo;
import com.isxcode.star.api.datasource.dto.QueryColumnDto;
import com.isxcode.star.api.datasource.dto.QueryTableDto;
import com.isxcode.star.api.model.ao.DataModelColumnAo;
import com.isxcode.star.api.model.constant.ColumnFormatType;
import com.isxcode.star.api.work.res.GetDataSourceDataRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.aes.AesUtils;
import com.isxcode.star.modules.datasource.service.DatabaseDriverService;
import com.isxcode.star.modules.datasource.source.Datasource;
import com.isxcode.star.modules.model.entity.DataModelEntity;
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
public class DmService extends Datasource {

    public DmService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties, AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.DM;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.DM_DRIVER;
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
        return sqlBuilder.toString();
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
            return qr.query(connection, "SELECT COUNT(*) FROM " + connectInfo.getTableName(), new ScalarHandler<>());
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
            return qr.query(connection,
                "SELECT COUNT(*) FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '" + connectInfo.getTableName() + "'",
                new ScalarHandler<>());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public String getPageSql(String sql) throws IsxAppException {
        return "SELECT * FROM (" + sql + ") WHERE ROWNUM BETWEEN '${page}' AND '${pageSize}'";
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
        // DM数据库无需特别的刷新操作
    }

    private String mapColumnType(String columnTypeCode, String columnType) {
        switch (columnTypeCode) {
            case ColumnFormatType.CUSTOM:
                return columnType;
            case ColumnFormatType.TEXT:
                return "TEXT";
            case ColumnFormatType.DATE:
                return "DATE";
            case ColumnFormatType.DOUBLE:
                return "DOUBLE";
            case ColumnFormatType.INT:
                return "INTEGER";
            case ColumnFormatType.DATETIME:
                return "DATETIME";
            case ColumnFormatType.STRING:
                return "VARCHAR(" + (columnType == null ? "200" : columnType) + ")";
            default:
                return "暂不支持该类型字段";
        }
    }
}
