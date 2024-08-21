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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class HiveService extends Datasource {

    public HiveService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties, AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.HIVE;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.HIVE_DRIVER;
    }

    @Override
    protected List<QueryTableDto> queryTable(Connection connection, String database, String datasourceId,
        String tablePattern) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SHOW TABLES IN " + database);
        ArrayList<QueryTableDto> tables = new ArrayList<>();
        while (resultSet.next()) {
            QueryTableDto dto = new QueryTableDto();
            dto.setTableName(resultSet.getString(1));
            dto.setDatasourceId(datasourceId);
            if (tablePattern.isEmpty() || dto.getTableName().matches(tablePattern)) {
                tables.add(dto);
            }
        }

        // 获取表的备注
        for (QueryTableDto table : tables) {
            resultSet = statement.executeQuery("DESCRIBE FORMATTED " + database + "." + table.getTableName());
            while (resultSet.next()) {
                if (resultSet.getString(2) != null && "comment".equals(resultSet.getString(2).trim())) {
                    if (resultSet.getString(3) != null) {
                        table.setTableComment(resultSet.getString(3));
                    }
                    break;
                }
            }
        }

        connection.close();
        return tables;
    }

    @Override
    protected List<QueryColumnDto> queryColumn(Connection connection, String database, String datasourceId,
        String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("DESCRIBE FORMATTED " + database + "." + tableName);
        ArrayList<QueryColumnDto> columns = new ArrayList<>();
        while (resultSet.next()) {
            if (resultSet.getString(1).isEmpty() || "# Partition Information".equals(resultSet.getString(1))
                || "# col_name            ".equals(resultSet.getString(1))) {
                continue;
            }
            if ("# Detailed Table Information".equals(resultSet.getString(1))) {
                break;
            }
            QueryColumnDto dto = new QueryColumnDto();
            dto.setDatasourceId(datasourceId);
            dto.setTableName(tableName);
            dto.setColumnName(resultSet.getString(1));
            dto.setColumnType(resultSet.getString(2));
            if (resultSet.getString(3) != null) {
                dto.setColumnComment(resultSet.getString(3));
            }
            columns.add(dto);
        }
        connection.close();
        return columns;
    }

    @Override
    protected Long getTableTotalSize(Connection connection, String database, String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("DESCRIBE FORMATTED " + database + "." + tableName);
        Long tableTotalSize = 0L;
        while (resultSet.next()) {

            if (resultSet.getString(2) != null && "rawDataSize".equals(resultSet.getString(2).trim())) {
                tableTotalSize = Long.parseLong(resultSet.getString(3).trim());
            }
        }
        connection.close();
        return tableTotalSize;
    }

    @Override
    protected Long getTableTotalRows(Connection connection, String database, String tableName) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("DESCRIBE FORMATTED " + database + "." + tableName);
        Long tableTotalSize = 0L;
        while (resultSet.next()) {

            if (resultSet.getString(2) != null && "numRows".equals(resultSet.getString(2).trim())) {
                tableTotalSize = Long.parseLong(resultSet.getString(3).trim());
            }
        }
        connection.close();
        return tableTotalSize;
    }

    @Override
    protected Long getTableColumnCount(Connection connection, String database, String tableName) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("DESCRIBE FORMATTED " + database + "." + tableName);
        ArrayList<QueryColumnDto> columns = new ArrayList<>();
        while (resultSet.next()) {
            if (resultSet.getString(1).isEmpty() || "# Partition Information".equals(resultSet.getString(1))
                || "# col_name            ".equals(resultSet.getString(1))) {
                continue;
            }
            if ("# Detailed Table Information".equals(resultSet.getString(1))) {
                break;
            }
            QueryColumnDto dto = new QueryColumnDto();
            dto.setTableName(tableName);
            dto.setColumnName(resultSet.getString(1));
            columns.add(dto);
        }
        connection.close();
        return Long.parseLong(String.valueOf(columns.size()));
    }

    @Override
    protected String getTableDataSql(String tableName, String rowNumber) {

        return "SELECT * FROM " + tableName + ("ALL".equals(rowNumber) ? "" : " LIMIT " + rowNumber);
    }

    @Override
    protected void refreshTableInfo(Connection connection, String database, String tableName) throws SQLException {

        Statement statement = connection.createStatement();
        statement.execute("analyze table " + database + "." + tableName + " compute statistics");
        connection.close();
    }
}
