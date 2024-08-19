package com.isxcode.star.modules.datasource.source.impl;

import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.pojos.dto.QueryColumnDto;
import com.isxcode.star.api.datasource.pojos.dto.QueryTableDto;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.datasource.service.DatabaseDriverService;
import com.isxcode.star.modules.datasource.source.Datasource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

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
    protected List<QueryTableDto> queryTable(Connection connection, String database, String datasourceId,
        String tablePattern) throws SQLException {
        QueryRunner qr = new QueryRunner();

        List<QueryTableDto> query;
        if (Strings.isNotEmpty(tablePattern)) {
            query = qr.query(connection, "SELECT '" + datasourceId
                + "' as datasourceId,tables.table_name as tableName,tables.table_comment as tableComment FROM information_schema.tables WHERE table_schema = '"
                + database + "' AND  tables.table_name REGEXP '" + tablePattern + "'",
                new BeanListHandler<>(QueryTableDto.class));
        } else {
            query = qr.query(connection, "SELECT '" + datasourceId
                + "' as datasourceId,tables.table_name as tableName,tables.table_comment as tableComment FROM information_schema.tables WHERE table_schema = '"
                + database + "'", new BeanListHandler<>(QueryTableDto.class));
        }

        connection.close();
        return query;
    }

    @Override
    protected List<QueryColumnDto> queryColumn(Connection connection, String database, String datasourceId,
        String tableName) throws SQLException {

        QueryRunner qr = new QueryRunner();
        List<QueryColumnDto> query = qr.query(connection, "SELECT '" + datasourceId + "' as datasourceId,'" + tableName
            + "' as tableName, COLUMN_NAME as columnName,COLUMN_TYPE as columnType,COLUMN_COMMENT as columnComment FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '"
            + database + "' AND TABLE_NAME = '" + tableName + "'", new BeanListHandler<>(QueryColumnDto.class));
        connection.close();
        return query;
    }

    @Override
    protected Long getTableTotalSize(Connection connection, String database, String tableName) throws SQLException {

        QueryRunner qr = new QueryRunner();
        BigInteger query = qr.query(connection,
            "SELECT data_length + index_length FROM information_schema.tables WHERE table_schema = '" + database
                + "' AND table_name = '" + tableName + "'",
            new ScalarHandler<>());
        connection.close();
        return Long.parseLong(String.valueOf(query));
    }

    @Override
    protected Long getTableTotalRows(Connection connection, String database, String tableName) throws SQLException {

        QueryRunner qr = new QueryRunner();
        Long query = qr.query(connection, "SELECT count(*) FROM " + tableName, new ScalarHandler<>());
        connection.close();
        return query;
    }

    @Override
    protected Long getTableColumnCount(Connection connection, String database, String tableName) throws SQLException {

        QueryRunner qr = new QueryRunner();
        Long query = qr.query(connection, "SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '"
            + database + "' AND TABLE_NAME = '" + tableName + "'", new ScalarHandler<>());
        connection.close();
        return query;
    }
}
