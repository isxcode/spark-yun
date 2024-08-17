package com.isxcode.star.modules.datasource.source.impl;

import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.pojos.dto.QueryTablesDto;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.datasource.service.DatabaseDriverService;
import com.isxcode.star.modules.datasource.source.Datasource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class MysqlService extends Datasource {

    private final JdbcTemplate jdbcTemplate;

    public MysqlService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties, AesUtils aesUtils,
        JdbcTemplate jdbcTemplate) {
        super(dataDriverService, isxAppProperties, aesUtils);
        this.jdbcTemplate = jdbcTemplate;
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
    protected List<QueryTablesDto> queryTables(Connection connection, String database) throws SQLException {

        QueryRunner qr = new QueryRunner();

        List<QueryTablesDto> query = qr.query(connection,
            "SELECT tables.table_name as tableName FROM information_schema.tables WHERE table_schema = '" + database
                + "'",
            new BeanListHandler<>(QueryTablesDto.class));
        connection.close();
        return query;
    }
}
