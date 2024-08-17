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
import org.springframework.stereotype.Service;

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
    protected List<QueryTableDto> queryTable(Connection connection, String database) throws SQLException {
        QueryRunner qr = new QueryRunner();
        List<QueryTableDto> query = qr.query(connection,
            "SELECT tables.table_name as tableName FROM information_schema.tables WHERE table_schema = '" + database
                + "'",
            new BeanListHandler<>(QueryTableDto.class));
        connection.close();
        return query;
    }

    @Override
    protected List<QueryColumnDto> queryColumn(Connection connection, String database, String tableName)
        throws SQLException {
        QueryRunner qr = new QueryRunner();
        List<QueryColumnDto> query = qr.query(connection,
            "SELECT COLUMN_NAME as columnName,DATA_TYPE as columnType FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '"
                + database + "' AND TABLE_NAME = '" + tableName + "'",
            new BeanListHandler<>(QueryColumnDto.class));
        connection.close();
        return query;
    }
}
