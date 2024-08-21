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
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class OceanbaseService extends Datasource {

    public OceanbaseService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties,
        AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.OCEANBASE;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.OCEAN_BASE_DRIVER;
    }

    @Override
    protected List<QueryTableDto> queryTable(Connection connection, String database, String datasourceId,
        String tablePattern) throws SQLException {
        throw new RuntimeException("该数据源暂不支持");
    }

    @Override
    protected List<QueryColumnDto> queryColumn(Connection connection, String database, String datasourceId,
        String tableName) throws SQLException {
        throw new RuntimeException("该数据源暂不支持");
    }

    @Override
    protected Long getTableTotalSize(Connection connection, String database, String tableName) throws SQLException {
        return 0L;
    }

    @Override
    protected Long getTableTotalRows(Connection connection, String database, String tableName) throws SQLException {
        return 0L;
    }

    @Override
    protected Long getTableColumnCount(Connection connection, String database, String tableName) throws SQLException {
        return 0L;
    }

    @Override
    protected String getTableDataSql(String tableName, String rowNumber) {

        return "SELECT * FROM " + tableName + ("ALL".equals(rowNumber) ? "" : " LIMIT " + rowNumber);
    }
}
