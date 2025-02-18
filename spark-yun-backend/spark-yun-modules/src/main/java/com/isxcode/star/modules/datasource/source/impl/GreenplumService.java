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

}
