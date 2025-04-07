package com.isxcode.star.modules.datasource.source.impl;

import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.dto.ConnectInfo;
import com.isxcode.star.api.datasource.dto.QueryColumnDto;
import com.isxcode.star.api.datasource.dto.QueryTableDto;
import com.isxcode.star.api.work.res.GetDataSourceDataRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.aes.AesUtils;
import com.isxcode.star.modules.datasource.service.DatabaseDriverService;
import com.isxcode.star.modules.datasource.source.Datasource;
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
}
