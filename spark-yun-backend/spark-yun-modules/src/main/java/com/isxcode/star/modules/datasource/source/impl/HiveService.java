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
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<QueryTableDto> queryTable(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getDatabase(), "datasbase不能为空");

        try (Connection connection = getConnection(connectInfo); Statement statement = connection.createStatement();) {

            ResultSet resultSet = statement.executeQuery("SHOW TABLES IN " + connectInfo.getDatabase());
            ArrayList<QueryTableDto> tables = new ArrayList<>();
            while (resultSet.next()) {
                QueryTableDto meta = QueryTableDto.builder().tableName(resultSet.getString(1))
                    .datasourceId(connectInfo.getDatasourceId()).build();
                if (connectInfo.getTablePattern().isEmpty()
                    || meta.getTableName().matches(connectInfo.getTablePattern())) {
                    tables.add(meta);
                }
            }

            // 获取表的备注
            for (QueryTableDto table : tables) {
                resultSet = statement
                    .executeQuery("DESCRIBE FORMATTED " + connectInfo.getDatabase() + "." + table.getTableName());
                while (resultSet.next()) {
                    if (resultSet.getString(2) != null && "comment".equals(resultSet.getString(2).trim())) {
                        if (resultSet.getString(3) != null) {
                            table.setTableComment(resultSet.getString(3));
                        }
                        break;
                    }
                }
            }

            return tables;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public List<QueryColumnDto> queryColumn(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getDatabase(), "datasource不能为空");
        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        try (Connection connection = getConnection(connectInfo); Statement statement = connection.createStatement();) {
            ResultSet resultSet = statement
                .executeQuery("DESCRIBE FORMATTED " + connectInfo.getDatabase() + "." + connectInfo.getTableName());
            ArrayList<QueryColumnDto> columns = new ArrayList<>();
            Boolean isPartitionColumn = false;
            while (resultSet.next()) {
                // 跳过
                if (resultSet.getString(1).isEmpty() || "# col_name            ".equals(resultSet.getString(1))) {
                    continue;
                }
                if ("# Partition Information".equals(resultSet.getString(1))) {
                    isPartitionColumn = true;
                    continue;
                }
                // 中止
                if ("# Detailed Table Information".equals(resultSet.getString(1))) {
                    break;
                }
                QueryColumnDto meta = QueryColumnDto.builder().datasourceId(connectInfo.getDatasourceId())
                    .tableName(connectInfo.getTableName()).isPartitionColumn(isPartitionColumn)
                    .columnName(resultSet.getString(1)).columnType(resultSet.getString(2)).build();
                if (resultSet.getString(3) != null) {
                    meta.setColumnComment(resultSet.getString(3));
                }
                columns.add(meta);
            }
            return columns;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public Long getTableTotalSize(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getDatabase(), "datasource不能为空");
        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        try (Connection connection = getConnection(connectInfo); Statement statement = connection.createStatement();) {
            ResultSet resultSet = statement
                .executeQuery("DESCRIBE FORMATTED " + connectInfo.getDatabase() + "." + connectInfo.getTableName());
            Long tableTotalSize = 0L;
            while (resultSet.next()) {
                if (resultSet.getString(2) != null && "totalSize".equals(resultSet.getString(2).trim())) {
                    tableTotalSize = Long.parseLong(resultSet.getString(3).trim());
                }
            }
            return tableTotalSize;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public Long getTableTotalRows(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getDatabase(), "datasource不能为空");
        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        try (Connection connection = getConnection(connectInfo); Statement statement = connection.createStatement();) {
            ResultSet resultSet = statement
                .executeQuery("DESCRIBE FORMATTED " + connectInfo.getDatabase() + "." + connectInfo.getTableName());
            Long tableTotalSize = 0L;
            while (resultSet.next()) {
                if (resultSet.getString(2) != null && "numRows".equals(resultSet.getString(2).trim())) {
                    tableTotalSize = Long.parseLong(resultSet.getString(3).trim());
                }
            }
            return tableTotalSize;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public Long getTableColumnCount(ConnectInfo connectInfo) throws IsxAppException {

        return Long.parseLong(String.valueOf(queryColumn(connectInfo).size()));
    }

    @Override
    public String getPageSql(String sql) throws IsxAppException {
        return "";
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

        Assert.notNull(connectInfo.getDatabase(), "database不能为空");
        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        // 获取表的分区字段
        List<QueryColumnDto> columns = queryColumn(connectInfo);
        List<String> partitionColumnList = columns.stream().filter(QueryColumnDto::getIsPartitionColumn)
            .map(QueryColumnDto::getColumnName).collect(Collectors.toList());

        try (Connection connection = getConnection(connectInfo); Statement statement = connection.createStatement()) {

            if (partitionColumnList.isEmpty()) {
                statement.execute("analyze table " + connectInfo.getDatabase() + "." + connectInfo.getTableName()
                    + " compute statistics");
            } else {
                statement.execute("analyze table " + connectInfo.getDatabase() + "." + connectInfo.getTableName()
                    + " partition(" + Strings.join(partitionColumnList, ',') + ") compute statistics");
            }

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

}
