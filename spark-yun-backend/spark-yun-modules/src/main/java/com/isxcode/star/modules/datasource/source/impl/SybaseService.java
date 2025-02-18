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
import java.util.stream.Collectors;

@Service
@Slf4j
public class SybaseService extends Datasource {

    public SybaseService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties,
        AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.SYBASE;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.SYBASE_DRIVER;
    }

    @Override
    public List<QueryTableDto> queryTable(ConnectInfo connectInfo) throws IsxAppException {

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            List<QueryTableDto> result = qr.query(connection, "SELECT '" + connectInfo.getDatasourceId()
                + "' as datasourceId, sch.name + '.' + tbl.name AS tableName, ep.value AS tableComment "
                + "FROM sys.tables AS tbl JOIN sys.schemas AS sch ON tbl.schema_id = sch.schema_id "
                + "LEFT JOIN sys.extended_properties AS ep ON ep.major_id = tbl.object_id AND ep.minor_id = 0 AND ep.name = 'MS_Description'",
                new BeanListHandler<>(QueryTableDto.class));
            if (Strings.isNotEmpty(connectInfo.getTablePattern())) {
                return result.stream().filter(e -> e.getTableName().matches(connectInfo.getTablePattern()))
                    .collect(Collectors.toList());
            } else {
                return result;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public List<QueryColumnDto> queryColumn(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        String[] names = connectInfo.getTableName().split("\\.");
        if (names.length != 2) {
            throw new RuntimeException("sqlserver table name format must like 'schemaName.tableName'");
        }

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {

            return qr.query(connection, "SELECT '" + connectInfo.getDatasourceId()
                + "' as datasourceId, s.name + '.' + t.name AS tableName, c.name AS columnName, tp.name AS columnType, "
                + "c.max_length AS MaxLength,ep.value AS columnComment, 0 as isPartitionColumn FROM sys.columns c "
                + "INNER JOIN sys.tables t ON c.object_id = t.object_id INNER JOIN sys.schemas s ON t.schema_id = s.schema_id INNER JOIN "
                + "sys.types tp ON c.user_type_id = tp.user_type_id LEFT JOIN sys.extended_properties ep ON c.object_id = ep.major_id "
                + "AND c.column_id = ep.minor_id AND ep.name = 'MS_Description' where s.name = '" + names[0]
                + "' AND t.name = '" + names[1] + "'", new BeanListHandler<>(QueryColumnDto.class));
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public Long getTableTotalSize(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");

        String[] names = connectInfo.getTableName().split("\\.");
        if (names.length != 2) {
            throw new RuntimeException("sqlserver table name format must like 'schemaName.tableName'");
        }

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            Object query = qr.query(connection,
                "SELECT SUM(a.total_pages) * 8 * 1024 " + "FROM sys.tables t "
                    + "INNER JOIN sys.indexes i ON t.OBJECT_ID = i.object_id "
                    + "INNER JOIN sys.partitions p ON i.object_id = p.OBJECT_ID AND i.index_id = p.index_id "
                    + "INNER JOIN sys.allocation_units a ON p.partition_id = a.container_id "
                    + "INNER JOIN sys.schemas s ON t.schema_id = s.schema_id " + "WHERE s.name = '" + names[0]
                    + "' and t.name = '" + names[1] + "' " + "GROUP BY t.NAME, s.name",
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

        String[] names = connectInfo.getTableName().split("\\.");
        if (names.length != 2) {
            throw new RuntimeException("sqlserver tableName format must like 'schemaName.tableName'");
        }

        QueryRunner qr = new QueryRunner();
        try (Connection connection = getConnection(connectInfo)) {
            Object query = qr.query(connection, "SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '"
                + names[0] + "' AND TABLE_NAME = '" + names[1] + "'", new ScalarHandler<>());
            return Long.valueOf(String.valueOf(query));
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public String getPageSql(String sql) throws IsxAppException {

        if (sql.toLowerCase().contains("order by")) {
            throw new IsxAppException("sqlserver不支持order by语法，默认select后面第一个字段升序分页");
        }

        // 以第一个字段作为排序字段
        String[] split = sql.split(",");
        if (split.length < 1 || split[0].length() < 6 || !"select".equals(split[0].substring(0, 6))) {
            throw new IsxAppException("需要首单词为select的查询语句");
        }
        String firstCol = split[0].toLowerCase().trim().substring(7);
        String firstKey = "ROW_NUMBER() OVER (ORDER BY " + firstCol + " ASC) AS SY_ROW_NUM";
        return "SELECT * FROM (" + sql.replace(split[0], split[0] + "," + firstKey)
            + ") AS SubQuery WHERE SY_ROW_NUM BETWEEN '${page}' AND '${pageSize}'";
    }

    @Override
    public GetDataSourceDataRes getTableData(ConnectInfo connectInfo) throws IsxAppException {

        Assert.notNull(connectInfo.getTableName(), "tableName不能为空");
        Assert.notNull(connectInfo.getRowNumber(), "rowNumber不能为空");

        String getTableDataSql =
            "SELECT" + ("ALL".equals(connectInfo.getRowNumber()) ? " " : " TOP " + connectInfo.getRowNumber())
                + "* FROM " + connectInfo.getTableName();

        return getTableData(connectInfo, getTableDataSql);
    }

    @Override
    public void refreshTableInfo(ConnectInfo connectInfo) throws IsxAppException {

    }

}
